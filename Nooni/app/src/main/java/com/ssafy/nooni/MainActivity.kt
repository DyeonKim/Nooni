package com.ssafy.nooni

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.nooni.adapter.ViewpagerFragmentAdapter
import com.ssafy.nooni.databinding.ActivityMainBinding
import com.ssafy.nooni.util.PermissionUtil
import org.w3c.dom.Text
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var permissionUtil: PermissionUtil
    lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionUtil = PermissionUtil(this)
        permissionUtil.permissionListener = object : PermissionUtil.PermissionListener {
            override fun run() {
                init()
            }
        }
    }

    private fun init() {
        val viewpager: ViewPager2 = binding.viewpager
        val viewpagerFragmentAdapter = ViewpagerFragmentAdapter(this)

        viewpager.adapter = viewpagerFragmentAdapter
        viewpager.currentItem = 1

        tts = TextToSpeech(this, TextToSpeech.OnInitListener {
            @Override
            fun onInit(status: Int){
                if(status != ERROR){
                    tts.language = Locale.KOREA
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {
        if(!permissionUtil.checkPermissions(listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE))) {
            permissionUtil.requestPermissions()
        } else {
            init()
        }
    }
}