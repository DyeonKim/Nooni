package com.ssafy.nooni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startLoading()
    }

    fun startLoading(){
        val handler = Handler()
        handler.postDelayed(Runnable {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }, 3000)
    }
}