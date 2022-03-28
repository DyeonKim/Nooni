package com.ssafy.nooni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import com.ssafy.nooni.tutorial.TutorialActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startLoading()
    }

    fun startLoading(){
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