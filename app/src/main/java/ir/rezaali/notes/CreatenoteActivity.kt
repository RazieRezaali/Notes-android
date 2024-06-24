package ir.rezaali.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import ir.rezaali.notes.databinding.ActivityCreatenoteBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CreatenoteActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreatenoteBinding
    lateinit var auth: FirebaseAuth
    lateinit var store: FirebaseFirestore
    lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreatenoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(findViewById(R.id.toolbarofcreatenote))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        progressbar = findViewById(R.id.progressbarofcreatenoteactivity)

        binding.savenote.setOnClickListener{
            val title = binding.createtitleofnote.text.toString()
            val content = binding.createcontentofnote.text.toString()
            if (title.isEmpty() || content.isEmpty()){
                Toast.makeText(this, "هر دو فیلد الزامی هستند", Toast.LENGTH_SHORT).show()
            }
            else{
                progressbar.visibility=View.VISIBLE
                if (user != null) {
                    val documentReference : DocumentReference = store.collection("notes")
                        .document(user.uid)
                        .collection("myNotes")
                        .document()
                    val note = mutableMapOf<String, Any>()
                    note["title"]=title
                    note["content"]=content

                    //note["currentDate"]= LocalDateTime.now().toString()
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    val formatted = current.format(formatter)
                    note["currentDate"]= formatted

                    documentReference.set(note).addOnSuccessListener {
                        Toast.makeText(this, "یادداشت شما با موفقیت ایجاد شد", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, NotesActivity::class.java))
                    }.addOnFailureListener{
                        progressbar.visibility=View.INVISIBLE
                        Toast.makeText(this, "ذخیره یادداشت شما با شکست مواجه شد", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//       if (item.itemId==android.R.id.home){
//           onBackPressed()
//       }
//        return super.onOptionsItemSelected(item)
//    }
}