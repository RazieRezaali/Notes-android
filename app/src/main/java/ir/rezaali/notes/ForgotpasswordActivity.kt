package ir.rezaali.notes



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ir.rezaali.notes.databinding.ActivityForgotpasswordBinding


class ForgotpasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotpasswordBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityForgotpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.passwordrecoverbutton.setOnClickListener{
            var email = binding.forgotpassword.text.toString()
            if (email.isEmpty()){
                binding.forgotpassword.setError("لطفا ایمیل خود را وارد کنید")
            }
            else{
                auth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "ایمیل ارسال شد، میتوانید از آن برای بازیابی رمز عبور خود استفاده کنید", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "چنین ایمیلی ثبت نشده است", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.gobacktologin.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}