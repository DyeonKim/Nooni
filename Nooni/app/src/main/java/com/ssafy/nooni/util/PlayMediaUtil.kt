package com.ssafy.nooni.util

import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.ssafy.nooni.R
import java.lang.Exception
import java.net.URLDecoder
import java.net.URLEncoder

class PlayMediaUtil {
    private var mediaPlayer: MediaPlayer? = MediaPlayer()
    fun start(url: String) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
            }
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(URLDecoder.decode(url, "UTF-8"))
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: Exception) {

        }
    }
    fun stop(){
        if (mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.stop();
        }
        mediaPlayer!!.reset();
        mediaPlayer!!.release();
        mediaPlayer = null;
    }
}