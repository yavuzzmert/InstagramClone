package com.yavuzmert.a20kotlininstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yavuzmert.a20kotlininstagram.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}