package com.example.simplerates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback

class FavouritesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        var callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var intent = Intent(this@FavouritesActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }
}
