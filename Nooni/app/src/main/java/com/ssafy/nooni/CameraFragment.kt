package com.ssafy.nooni

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
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
import com.ssafy.nooni.ml.Model
import com.ssafy.nooni.util.ShakeUtil
import com.ssafy.nooni.util.PlayMediaUtil
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.lang.Exception
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CameraFragment"

class CameraFragment : Fragment() {
    lateinit var binding: FragmentCameraBinding
    lateinit var allergyRVAdapter: AllergyRVAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mShakeUtil: ShakeUtil

    private val mediaUtil = PlayMediaUtil()

    private val IMAGE_SIZE = 224
    private val classes = arrayOf(
        "꼬깔콘고소한맛",
        "크라운콘초",
        "해태맛동산",
        "오리온고소미",
        "해태에이스",
        "머거본알땅콩",
        "해태오예스",
        "해태오사쯔",
        "해태구운감자",
        "크라운초코하임",
        "맥콜",
        "킨사이다",
        "코카콜라",
        "펩시",
        "갈배사이다",
        "아침에사과",
        "하늘보리",
        "환타오렌지",
        "환타파인애플",
        "레쓰비",
        "광동제약위생천",
        "마데카솔",
        "바른생각익스트림에어핏",
        "안티푸라민연고",
        "해피홈아쿠아밴드",
        "가그린오리지널",
        "유한해피홈멸균밴드",
        "오카모토리얼핏003",
        "페리오46cm쿨민트치약",
        "카카오프렌즈밴드중형"
    )

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
        initSensor()

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

        behavior = BottomSheetBehavior.from(binding.llCameraFBottomSheet)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    describeTTS()
                } else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    mainActivity.tts.stop()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        setBottomSheetRecyclerView()
    }


    private fun initSensor(){
        mSensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mShakeUtil = ShakeUtil()
        mShakeUtil.setOnShakeListener(object : ShakeUtil.OnShakeListener {
            override fun onShake(count: Int) {
                describeTTS()
            }
        })
    }

//    override fun onStart() {
//        super.onStart()
//        Toast.makeText(requireActivity(), "camera onStart called", Toast.LENGTH_SHORT).show()
//        mainActivity.tts.speak("상품 인식 화면입니다." + binding.tvCameraFDescription.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
//    }

    override fun onResume() {
        super.onResume()
        startCamera()
        mainActivity.findViewById<TextView>(R.id.tv_title).text = "상품 인식"
//        Toast.makeText(requireActivity(), "camera onResume called", Toast.LENGTH_SHORT).show()
        mainActivity.ttsSpeak("상품 인식 화면입니다." + binding.tvCameraFDescription.text.toString())

        mSensorManager.registerListener(
            mShakeUtil,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
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

    // TODO: 현수님 코드. 카메라로 촬영한 이미지를 저장하지 않고 bitmap으로 저장해서 처리할 생각 중이라 우선 일단 버리지 않고 킵
//    fun takePicture() {
//        val imageCapture = this.imageCapture ?: return
//
//        //MediaStore에 저장할 파일 이름 생성
//        val name =
//            SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA).format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "nooni$name.jpg")
//            put(MediaStore.Images.ImageColumns.TITLE, "nooni$name.jpg")
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(
//                    MediaStore.Images.Media.RELATIVE_PATH,
//                    "${Environment.DIRECTORY_PICTURES}/nooni"
//                )
//            }
//        }
//
//        // 파일과 메타데이터를 포함하는 아웃풋 옵션 설정
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(
//                requireActivity().contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues
//            )
//            .build()
//
//        //캡처 리스너 세팅, 이벤트 발생하면 위에서 지정한 경로로 이미지 저장
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(requireActivity()),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    Toast.makeText(requireContext(), "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Log.d(TAG, "onCaptureSavedError: ${exception.message}")
//                }
//            }
//        )
//    }

    private fun takePicture() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer: ByteBuffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)
                    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

                    image.close()

                    // 90도 돌리기
                    var rotateMatrix = Matrix()
                    rotateMatrix.postRotate(90.0f)

                    // 244
                    var cropImage = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, false)
                    cropImage = Bitmap.createBitmap(cropImage, 0, 0, cropImage.width, cropImage.height, rotateMatrix, false)
                    var originImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, false)

                    classifyImage(cropImage, originImage)
                    super.onCaptureSuccess(image)
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

    private fun classifyImage(image: Bitmap, originImage: Bitmap) {
        try {
            var model: Model = Model.newInstance(requireContext())

            val inputFeature0: TensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, IMAGE_SIZE, IMAGE_SIZE, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
            image.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, image.width, image.height)

            // 244 244
            var pixel = 0
            for (i in 0 until IMAGE_SIZE) {
                for (j in 0 until IMAGE_SIZE) {
                    val values = intValues[pixel++] // RGB

                    byteBuffer.putFloat((values shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((values shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((values and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences: FloatArray = outputFeature0.floatArray
            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            Toast.makeText(context, "${String.format("%s: %.1f%%\n", classes[maxPos], confidences[maxPos] * 100)}", Toast.LENGTH_SHORT).show()

            model.close()
        } catch (e: IOException) {
            Log.e(TAG, "Photo capture failed: ${e.message}")
        }
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


    private fun describeTTS(){
        // TODO : 추후 상품 인식 기능 넣어서 상품 정보 가져올 경우, 가져온 정보에 따라 출력할 문자열 가공 필요
        var string = "${binding.tvCameraFBsName.text.toString()}, 가격 23000원, 알레르기 유발성분 밀, 우유, 콩,  320 칼로리"
        mainActivity.ttsSpeak(string)
    }

    override fun onPause() {
        mSensorManager.unregisterListener(mShakeUtil)
        super.onPause()
    }

}

