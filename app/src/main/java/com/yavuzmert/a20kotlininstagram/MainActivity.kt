package com.yavuzmert.a20kotlininstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.yavuzmert.a20kotlininstagram.databinding.ActivityFeedBinding
import com.yavuzmert.a20kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun signInClicked(view: View){

    }

    fun signUpClicked(view: View){

    }

}
/*
    - Firebase, cloud server'dır, database olarak kullanacağımız bir web service ve app'imize bağlayacağız firebase.google.com
    - firebase'de projemizi android için seçerek ve androidStudio'daki package name ile birebir olacak şekilde yazıyoruz ve bizim için oluşturduğı json dosyasını indirip Studio'da project kısmına girerek ekliyoruz ve bunu sürükle bırak ile yapabiliriz, firebase dashboard kısmında devam edersek SDK kısmını build.gradle-project yapısıyla projemize entegre yapmamız lazım daha sonra build.gradle-module kısmına eklememiz gerekenler var
 */