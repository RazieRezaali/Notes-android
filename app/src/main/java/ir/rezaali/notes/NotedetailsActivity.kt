package ir.rezaali.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.rezaali.notes.databinding.ActivityNotedetailsBinding

class NotedetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotedetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotedetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data= intent
        binding.titleofnotedetail.text = data.getStringExtra("title")
        binding.contentofnotedetail.text = data.getStringExtra("content")
        binding.date.text = " آخرین تغییرات در ".plus(data.getStringExtra("currentDate"))
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.gotoeditnote.setOnClickListener{
            val intent = Intent(it.context, EditActivity::class.java)
            intent.putExtra("title",data.getStringExtra("title"))
            intent.putExtra("content",data.getStringExtra("content"))
            intent.putExtra("noteId",data.getStringExtra("noteId"))
            it.context.startActivity(intent)
        }

        binding.gotonotes.setOnClickListener{
            startActivity(Intent(this, NotesActivity::class.java))
        }
    }
}