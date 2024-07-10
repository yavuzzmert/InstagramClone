package com.yavuzmert.a20kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yavuzmert.a20kotlininstagram.databinding.ActivityFeedBinding
import com.yavuzmert.a20kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signInClicked(view: View){
        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                //it.user.email
                val intent = Intent(this@MainActivity, FeedActivity:: class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun signUpClicked(view: View){

        val email = binding.emailText.text.toString()
        val password = binding.passwordText.text.toString()

        if(email.isEmpty() || password.isEmpty()){
               Toast.makeText(this,"Enter email and password!", Toast.LENGTH_LONG).show()
        } else{
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener{
                val intent = Intent(this@MainActivity, FeedActivity:: class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

}
/*
    - Firebase, cloud server'dır, database olarak kullanacağımız bir web service ve app'imize bağlayacağız firebase.google.com
    - firebase'de projemizi android için seçerek ve androidStudio'daki package name ile birebir olacak şekilde yazıyoruz ve bizim için oluşturduğı json dosyasını indirip Studio'da project kısmına girerek ekliyoruz ve bunu sürükle bırak ile yapabiliriz, firebase dashboard kısmında devam edersek SDK kısmını build.gradle-project yapısıyla projemize entegre yapmamız lazım daha sonra build.gradle-module kısmına eklememiz gerekenler var
    - kullanıcı girişi yapılacak yerden başlıyoruz, kayıt ol diyerek, firebase açarak doc authentication diyoruz ve neleri implement edeceğiz onları görüyoruz
    - firebase'den gidip authentication kısmından email active hala getireceğiz
    - sonra, dokümantasyona bakarak, ilk giriş kaydı oluşturacağız
    - isteği gönderiyoruz, firebase sunucularına o email kontrolü sağlayacak ve biz bunu arka planda yapmamız lazım yani asynecron, ve success durumunda intent ile diğer activity'e yolluyoruz veya failure durumunda toast message veriyoruz
    - Uygulamanın kapanması durumunda tekrar authentication durumuna düşmemesi için onCreate altında kullanıcının giriş yapma durumunu kontrol etmemiz lazım ve bu durumda direkt intent yapabiliriz.
 */