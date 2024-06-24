package ir.rezaali.notes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import ir.rezaali.notes.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var progressbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        progressbar = findViewById(R.id.progressbarofmainactivity)

        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient= GoogleSignIn.getClient(this,gso)


        val user = auth.currentUser
        if (user!=null){
            finish()
            startActivity(Intent(this, NotesActivity::class.java))
        }

        binding.login.setOnClickListener{
            val password = binding.loginpassword.text.toString()
            val email = binding.loginemail.text.toString()
            if (email.isEmpty()){
                binding.loginemail.setError("لطفا ایمیل خود را وارد کنید")
            }
            else if (password.isEmpty()){
                binding.loginpassword.setError("لطفا رمز عبور خود را وارد کنید")
            }
            else{
                progressbar.visibility= View.VISIBLE
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkMailVerification();
                    } else {
                        Toast.makeText(this, "حسابی با این مشخصات یافت نشد", Toast.LENGTH_SHORT).show()
                        progressbar.visibility= View.INVISIBLE
                    }
                }
            }
        }

        binding.gotosignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.gotoforgotpassword.setOnClickListener {
            startActivity(Intent(this, ForgotpasswordActivity::class.java))
        }

        binding.googleSingin.setOnClickListener{
            progressbar.visibility= View.VISIBLE
            signInGoogle()
        }
    }

    fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }
        else{
            progressbar.visibility= View.INVISIBLE
            Toast.makeText(this,"عملیات با شکست مواجه شد",Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential  = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener{task ->
            if (task.isSuccessful){
                progressbar.visibility= View.INVISIBLE
                Toast.makeText(this, "با موفقیت وارد شدید", Toast.LENGTH_SHORT).show()
//                finish()
                startActivity(Intent(this, NotesActivity::class.java))
            }else{
                progressbar.visibility= View.INVISIBLE
                Toast.makeText(this,"عملیات با شکست مواجه شد",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkMailVerification() {
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            Toast.makeText(this, "با موفقیت وارد شدید", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, NotesActivity::class.java))
        }
        else{
            progressbar.visibility= View.INVISIBLE
            Toast.makeText(this, "ابتدا ایمیل خود را تایید کنید", Toast.LENGTH_SHORT).show()
            auth.signOut()
        }
    }


    override fun onBackPressed() {
        if (isTaskRoot) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

}