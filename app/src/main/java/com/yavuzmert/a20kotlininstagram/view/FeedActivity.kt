package com.yavuzmert.a20kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yavuzmert.a20kotlininstagram.R
import com.yavuzmert.a20kotlininstagram.adapter.FeedRecyclerAdapter
import com.yavuzmert.a20kotlininstagram.databinding.ActivityFeedBinding
import com.yavuzmert.a20kotlininstagram.model.Post


class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var feedAdapter : FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        db = Firebase.firestore
        postArrayList = ArrayList<Post>()
        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter
    }

    private fun getData(){

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(this, error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if(value != null){
                    if(!value.isEmpty){
                        val documents= value.documents

                        postArrayList.clear()

                        for (document in documents){
                            //casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            //println(comment)

                            val post = Post(userEmail,comment,downloadUrl)
                            postArrayList.add(post)
                        }

                        feedAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
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
            val intent = Intent(this, MainActivity::class.java)
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

    4. verileri çekmek için önce bir db oluşturduk firebaseFirestore'dan daha sonra onCreate alttında initialize ediyoruz.
        -ayrı bir fun ile getData diyerek
        -verileri aldıktan sonra tek bir paket olarak kaydetmek için paket oluşturacağız view, model, adapter olarak
        -verileri bir liste içerisine atıp tümünü göstermek için liste oluşturup onCreate altında initialize ettikten sonra oluşturduğumuz package Model sınfının prop kullanarak gösterdik ve  artık recyclerview içerisinde gösterme işlemimiz kaldı

     5. recyclerView için;
        1.layout altında recycler_row adında bir tane layout resource file oluşturuyoruz
        2.recycler_row altında xml ile linear layout kullanarak kodlarımızı ekliyoruz çünkü alt alta sıralı olmasını istiyoruz.
        3.daha sonra adapter oluşturacağız, adapter klasörü altında bir tane kotlin sınıfı oluşturacağız
        -feedAdapter.notifyDataSetChanged() veriler yeniden gelince değişikleri gör ve düzenle

     6. image çekip göstermek için picasso kütüphanesini kullanacağız.

     7.filtreleme ve sıralı çekme işlemi için getData() altında
        - db.collection("Posts").orderBy("date", Query.Direction.DESCENDING)...
        - db.collection("Posts").whereEqualTo("userEmail", "yavuz.mert@hotmail.com")...
 */