package com.ssafy.nooni

import android.content.ContentValues
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ssafy.nooni.adapter.AllergyRVAdapter
import com.ssafy.nooni.databinding.FragmentCameraBinding
import com.ssafy.nooni.util.PlayMediaUtil
import java.lang.Exception
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CameraFragment"

class CameraFragment : Fragment() {
    lateinit var binding: FragmentCameraBinding
    lateinit var allergyRVAdapter: AllergyRVAdapter
    private lateinit var mainActivity: MainActivity
    private val mediaUtil = PlayMediaUtil()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        // 아래와 같이 url에서 음성파일 실행할 수 있음
        // TODO: 음성파일 이름 규칙을 만들어야 url 접근이 용이
        // 현재는 https://storage.googleapis.com/nooni-a587a.appspot.com/results/vocgan_{ }.wav 인데
        // 괄호안의 형태를 이미지 클래스 분류 output에 맞춰야 할 것같음

//        val url = URLEncoder.encode(
//            "https://storage.googleapis.com/nooni-a587a.appspot.com/results/vocgan_콘초 입니다 .wav",
//            "UTF-8"
//        )
//        mediaUtil.start(url)

    }


    private fun init() {
        var gestureListener = MyGesture()
        var doubleTapListener = MyDoubleGesture()
        var gestureDetector = GestureDetector(requireContext(), gestureListener)
        gestureDetector.setOnDoubleTapListener(doubleTapListener)
        binding.constraintLayoutCameraF.setOnTouchListener { v, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        // 왜인지는 모르겠으나 onTouchListener만 달아놓으면 더블클릭 인식이 안되고 clickListener도 같이 달아놔야만 더블클릭 인식됨; 뭐징
        binding.constraintLayoutCameraF.setOnClickListener {}

        setBottomSheetRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
        mainActivity.findViewById<TextView>(R.id.tv_title).text = "상품 인식"
    }

    private var imageCapture: ImageCapture? = null
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewViewCameraF.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            //후면 카메라 기본으로 세팅
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 카메라와 라이프사이클 바인딩 전 모든 바인딩 해제
                cameraProvider.unbindAll()

                // 카메라와 라이프사이클 바인딩
                cameraProvider.bindToLifecycle(
                    requireActivity(),
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed: ", e)
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    fun takePicture() {
        val imageCapture = this.imageCapture ?: return

        //MediaStore에 저장할 파일 이름 생성
        val name =
            SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "nooni$name.jpg")
            put(MediaStore.Images.ImageColumns.TITLE, "nooni$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/nooni"
                )
            }
        }

        // 파일과 메타데이터를 포함하는 아웃풋 옵션 설정
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        //캡처 리스너 세팅, 이벤트 발생하면 위에서 지정한 경로로 이미지 저장
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d(TAG, "onCaptureSavedError: ${exception.message}")
                }
            }
        )
    }

    private fun setBottomSheetRecyclerView() {
        allergyRVAdapter = AllergyRVAdapter()
        binding.rvCameraFBsAllergy.apply {
            adapter = allergyRVAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
        allergyRVAdapter.setData(listOf("밀", "우유", "콩"))
    }

    inner class MyGesture : GestureDetector.OnGestureListener {
        override fun onDown(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(p0: MotionEvent?) {}

        override fun onSingleTapUp(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            return false
        }

        override fun onLongPress(p0: MotionEvent?) {}

        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
            val SWIPE_THRESHOLD = 100
            val SWIPE_VELOCITY_THRESHOLD = 10

            var result = false
            try {
                val diffY = p1!!.y - p0!!.y
                val diffX = p1!!.x - p0!!.x
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(p3) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }

        private val behavior = BottomSheetBehavior.from(binding.llCameraFBottomSheet)
        private fun onSwipeBottom() {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        private fun onSwipeTop() {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

    inner class MyDoubleGesture : GestureDetector.OnDoubleTapListener {
        override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
            return false
        }

        override fun onDoubleTap(p0: MotionEvent?): Boolean {
            takePicture()
            return true
        }

        override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
            return false
        }
    }
}

