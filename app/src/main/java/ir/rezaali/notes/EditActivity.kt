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
import ir.rezaali.notes.databinding.ActivityEditBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EditActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditBinding
    lateinit var data : Intent
    lateinit var auth: FirebaseAuth
    lateinit var store: FirebaseFirestore
    lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data= intent

        auth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        progressbar = findViewById(R.id.progressbarofeditnoteactivity)

        val notetitle : String? = data.getStringExtra("title")
        val notecontent : String? = data.getStringExtra("content")

        binding.edittitleofnote.setText(notetitle)
        binding.editcontentofnote.setText(notecontent)

        binding.gotonotes.setOnClickListener{
            startActivity(Intent(this, NotesActivity::class.java))
        }

        binding.saveeditnote.setOnClickListener{
            val newTitle = binding.edittitleofnote.text.toString()
            val newContent = binding.editcontentofnote.text.toString()
            if (newTitle.isEmpty() || newContent.isEmpty()){
                Toast.makeText(this, "هر دو فیلد الزامی هستند", Toast.LENGTH_SHORT).show()
            }
            else{
                progressbar.visibility= View.VISIBLE
                if (user != null) {
                    val documentReference : DocumentReference = store.collection("notes")
                        .document(user.uid)
                        .collection("myNotes")
                        .document(data.getStringExtra("noteId").toString())
                    val note = mutableMapOf<String, Any>()
                    note["title"]=newTitle
                    note["content"]=newContent

                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    val formatted = current.format(formatter)
                    note["currentDate"]= formatted

                    documentReference.set(note).addOnSuccessListener {
                        Toast.makeText(this, "یادداشت شما با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, NotesActivity::class.java))
                    }.addOnFailureListener{
                        progressbar.visibility= View.INVISIBLE
                        Toast.makeText(this, "ویرایش یادداشت شما با شکست مواجه شد", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}