package com.ssafy.nooni.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.link.rx
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.ssafy.nooni.MainActivity
import com.ssafy.nooni.R
import com.ssafy.nooni.config.ApplicationClass.Companion.storageRef
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "KakaoUtil"

class KakaoUtil(val context: Context) {
    var imgFileName = ""

    fun sendKakaoLink(bitmap: Bitmap) {
        uploadImage(bitmapToUri(bitmap))
    }

    private fun bitmapToUri(bitmap: Bitmap): ByteArray {
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, stream)
        return stream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray) {
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imgFileName = "IMAGE_$timeStamp"
        storageRef.child("images")?.child(imgFileName).putBytes(byteArray).addOnCompleteListener {
            Toast.makeText(context, "이미지를 성공적으로 가져왔습니다", Toast.LENGTH_SHORT).show()
            sendMessage("테스트입니다")
        }.addOnFailureListener {
            Toast.makeText(context, "이미지를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage(content: String) {
        val defaultFeed = FeedTemplate(
            content = Content(
                title = "Test Title",
                description = content,
                imageUrl = getImageUrl(),
                link = Link(
                    mobileWebUrl = "https://naver.com"
                ),
            )
        )

        var disposable = CompositeDisposable()

        LinkClient.rx.defaultTemplate(context, defaultFeed)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ linkResult ->
                Log.d(TAG, "sendKakaoLink: 카카오링크 보내기 성공 ${linkResult.intent}")
                (context as MainActivity).startActivity(linkResult.intent)
                storageRef.child("images")?.child(imgFileName).delete()
            }, { error ->
                Log.d(TAG, "sendKakaoLink: 카카오링크 보내기 실패 $error")
            })
            .addTo(disposable)
    }

    private fun getImageUrl(): String {
        return "${context.resources.getString(R.string.firebase_storage_url_head)}${imgFileName}"
    }
}