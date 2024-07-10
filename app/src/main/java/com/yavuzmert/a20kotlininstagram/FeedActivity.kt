package com.yavuzmert.a20kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yavuzmert.a20kotlininstagram.databinding.ActivityFeedBinding


class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_post){
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        } else if(item.itemId == R.id.signout){
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

/*
    - options menu'yu oluşturduk, res altına file ekleyerek sonra xml ile menüyü  yazdık ve daha sonra menuyü bağlayacağımız feed activity altında iki tane func override etmemiz gerekiyor
    - onCreateOptionsMenu altında, önce menu'yü bağlıyoruz ve daha sonra ki func altında ise menu'deki itemlerdan biri seçilirse ne olacak şeçiyoruz
    - signout için auth signout yapmamız için sunucuya bildirmemiz lazım.
 */