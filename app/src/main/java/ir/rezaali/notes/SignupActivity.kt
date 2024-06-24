package ir.rezaali.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import ir.rezaali.notes.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        progressbar = findViewById(R.id.progressbarofsingup)

        binding.gotologin.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.signup.setOnClickListener{
            val password = binding.signuppassword.text.toString()
            val email = binding.signupemail.text.toString()
            if (email.isEmpty()){
                binding.signupemail.setError("لطفا ایمیل خود را وارد کنید")
            }
            else if (password.isEmpty()){
                binding.signuppassword.setError("لطفا رمز عبور خود را وارد کنید")
            }
            else if (password.length < 8){
                binding.signuppassword.setError("پسورد شما باید بیشتر از 8 کاراکتر باشد")
            }
            else{
                progressbar.visibility= View.VISIBLE
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "ثبت نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show()
                            sendEmailVerification()
                        } else {
                            Toast.makeText(this, "عملیات با شکست مواجه شد: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            progressbar.visibility= View.INVISIBLE
                        }
                    }
            }
        }
    }

    fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "ایمیل تاییدیه به آدرس ایمیل شما ارسال شد، ایمیل را تایید کرده و دوباره وارد شوید", Toast.LENGTH_LONG).show()
                    auth.signOut()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    progressbar.visibility= View.INVISIBLE
                    Toast.makeText(this, "خطا در ارسال ایمیل تاییدیه: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}