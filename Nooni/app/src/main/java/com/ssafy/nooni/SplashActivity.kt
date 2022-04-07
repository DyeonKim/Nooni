package com.ssafy.nooni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.ssafy.nooni.tutorial.TutorialActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val gifLogo: ImageView = findViewById<View>(R.id.logo) as ImageView
        Glide.with(this).load(R.drawable.logo_gif).into(gifLogo)

        startLoading()
    }

    private fun startLoading(){
        val handler = Handler()
        handler.postDelayed(Runnable {
            PreferenceManager.getDefaultSharedPreferences(this).apply {
                if(getBoolean("COMPLETED_ONBOARDING", false)) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    startActivity(Intent(applicationContext, TutorialActivity::class.java))
                }
            }
            finish()
        }, 3000)
    }
}