package ir.rezaali.notes

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ir.rezaali.notes.databinding.ActivityNotesBinding
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import kotlin.random.Random

class NotesActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotesBinding
    lateinit var auth: FirebaseAuth
    lateinit var store: FirebaseFirestore
    lateinit var recyclerView: RecyclerView
    lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    lateinit var noteAdapter: FirestoreRecyclerAdapter<NoteModel, NoteViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        store = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title="همه یادداشت ها"

        binding.createnotefab.setOnClickListener{
            startActivity(Intent(this, CreatenoteActivity::class.java))
        }

        val query: Query
        if (user != null) {
            query=store.collection("notes")
                .document(user.uid).
                collection("myNotes").
                orderBy("title",Query.Direction.ASCENDING)
        }
        //not necessary
        else{
            query=store.collection("notes")
        }

        val allusernote: FirestoreRecyclerOptions<NoteModel> =
            FirestoreRecyclerOptions.Builder<NoteModel>().setQuery(query,NoteModel::class.java).build()

        noteAdapter = object : FirestoreRecyclerAdapter<NoteModel, NoteViewHolder>(allusernote) {
            override fun onBindViewHolder(noteViewHolder: NoteViewHolder, i: Int, firebasemodel: NoteModel) {

                val popupbutton :ImageView = noteViewHolder.itemView.findViewById(R.id.menupopbutton)

                val colorCode =getRandomColor()
                noteViewHolder.note.setBackgroundColor(noteViewHolder.itemView.resources.getColor(colorCode,null))

                noteViewHolder.notetitle.setText(firebasemodel.title)
                noteViewHolder.notecontent.setText(firebasemodel.content)

                val docId:String = noteAdapter.snapshots.getSnapshot(i).id

                noteViewHolder.itemView.setOnClickListener {
                    val intent = Intent(noteViewHolder.itemView.context, NotedetailsActivity::class.java)
                    intent.putExtra("title",firebasemodel.title)
                    intent.putExtra("content",firebasemodel.content)
                    intent.putExtra("currentDate",firebasemodel.currentDate)
                    intent.putExtra("noteId",docId)
                    noteViewHolder.itemView.context.startActivity(intent)
                }

                popupbutton.setOnClickListener {
                    val popupMenu = PopupMenu(noteViewHolder.itemView.context, popupbutton)
                    popupMenu.gravity = Gravity.END

                    popupMenu.menu.add("ویرایش").setOnMenuItemClickListener {
                        val intent = Intent(noteViewHolder.itemView.context, EditActivity::class.java)
                        intent.putExtra("title",firebasemodel.title)
                        intent.putExtra("content",firebasemodel.content)
                        intent.putExtra("noteId",docId)
                        noteViewHolder.itemView.context.startActivity(intent)
                        false
                    }

                    popupMenu.menu.add("حذف").setOnMenuItemClickListener {

                        val dialog = AlertDialog.Builder(this@NotesActivity)
                        dialog.setMessage("از حذف این یادداشت اطمینان دارید؟")
                        dialog.setCancelable(false)
                        dialog.setNegativeButton("بله"){_,_ ->
                            if (user != null) {
                                val documentReference: DocumentReference =
                                    store.collection("notes").document(user.uid)
                                        .collection("myNotes").document(docId)
                                documentReference.delete().addOnSuccessListener {
                                    Toast.makeText(noteViewHolder.itemView.context,"یادداشت انتخاب شده با موفقیت حذف شد",Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener {
                                    Toast.makeText(noteViewHolder.itemView.context,"عملیات با شکست مواجه شد",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        dialog.setNeutralButton("بستن"){_,_ ->

                        }
                        val alertDialogBox =dialog.create()
                        alertDialogBox.show()
                        false
                    }

                    popupMenu.show()

                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
                val view: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.notes_layout,parent,false)
                return NoteViewHolder(view)
            }
        }


        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)

        ///////////////////////////////////////////////////////////////////////////////////////
        val layoutParams = recyclerView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.BELOW, R.id.my_toolbar)
        recyclerView.layoutParams = layoutParams

        staggeredGridLayoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        recyclerView.layoutManager = staggeredGridLayoutManager
        recyclerView.adapter= noteAdapter
    }




    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val notetitle: TextView = itemView.findViewById(R.id.notetitle)
         val notecontent: TextView = itemView.findViewById(R.id.notecontent)
         val note: LinearLayout = itemView.findViewById(R.id.note)

        init {
            // Animate RecyclerView
//            val translateAnim = AnimationUtils.loadAnimation(itemView.context, R.anim.translate_anim)
//            note.startAnimation(translateAnim)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                auth.signOut()
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        when (item.itemId) {
            R.id.setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart(){
        super.onStart()
        noteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (noteAdapter != null){
            noteAdapter.startListening()
        }
    }

    fun getRandomColor() :Int{
        val colorCode = mutableListOf<Int>()

        colorCode.add(R.color.a)
        colorCode.add(R.color.b)
        colorCode.add(R.color.c)
        colorCode.add(R.color.d)
        colorCode.add(R.color.e)
        colorCode.add(R.color.f)
        colorCode.add(R.color.g)
        colorCode.add(R.color.h)
        colorCode.add(R.color.i)
        colorCode.add(R.color.j)
        colorCode.add(R.color.k)
        colorCode.add(R.color.l)
        colorCode.add(R.color.m)
        colorCode.add(R.color.n)
        colorCode.add(R.color.o)
        colorCode.add(R.color.p)
        colorCode.add(R.color.q)
        colorCode.add(R.color.r)
        colorCode.add(R.color.s)

        val number = Random.nextInt(colorCode.size)
        return colorCode[number]
    }
}