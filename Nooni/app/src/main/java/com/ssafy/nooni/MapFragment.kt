package com.ssafy.nooni

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ssafy.nooni.databinding.FragmentMapBinding

import com.skt.Tmap.poi_item.TMapPOIItem

import android.graphics.Bitmap

import android.graphics.BitmapFactory
import com.skt.Tmap.TMapData.*

import com.skt.Tmap.TMapData.FindAroundNamePOIListenerCallback

import android.graphics.Color
import android.location.Location
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.skt.Tmap.*

import com.ssafy.nooni.util.GpsTracker

import java.lang.Exception
import com.skt.Tmap.TMapData.TMapPathType
import com.ssafy.nooni.entity.Contact
import com.ssafy.nooni.ui.SelectDialog
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import android.location.LocationManager

import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat

import com.skt.Tmap.TMapPoint

import android.location.LocationListener





class MapFragment : Fragment(),TMapGpsManager.onLocationChangedCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mainActivity: MainActivity

    private val key = BuildConfig.TMAP_API_KEY

    private lateinit var tMapView: TMapView
    private lateinit var tMapGpsManager: TMapGpsManager
    private var gpsTracker: GpsTracker? = null
    var minDistance = Double.POSITIVE_INFINITY

    private lateinit var pointFrom: TMapPoint
    private lateinit var pointTo: TMapPoint

    var latitude = 0.0
    var longitude = 0.0

    //원 크기 지정
    var minRadius = 500.0 // 500 meters
    var radius100 = 100.0 // 100 meters

    //마커 핀 이미지
    var markerItem1: TMapMarkerItem? = null
    var bitmap: Bitmap? = null
    var bitmap2:Bitmap? = null
    private var tMapPoint: TMapPoint? = null  //현재 위치 포인트

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.findViewById<TextView>(R.id.tv_title).text = "길안내"
        mainActivity.ttsSpeak("길 안내 화면입니다.")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.ic_baseline_location_on_24)
        bitmap2 = BitmapFactory.decodeResource(requireContext().resources, R.drawable.icon_marker)
        bitmap2 = Bitmap.createScaledBitmap(bitmap2!!, 50, 50, false)

        setAuth()
        setMap()
        setLocationManager()
        findCVS()

        binding.llTmap.addView(tMapView)


    }

    private fun setAuth(){
        tMapView = TMapView(requireContext())
        tMapView.setSKTMapApiKey(key)

    }

    private fun setMap(){
        tMapView.zoomLevel =  17
        tMapView.setIconVisibility(true)
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN)
//        tMapView.setCompassMode(true)
        tMapView.setSightVisible(true)  // 시야표출 사용 여부
        tMapView.setTrackingMode(true)  // 화면 중심을 단말의 현재 위치로 이동

    }

    private fun setLocationManager(){
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                var latitude = 0.0
                var longitude = 0.0
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                }
                val tp = TMapPoint(latitude, longitude)
                tMapView.setLocationPoint(longitude, latitude)
                tMapView.setCenterPoint(longitude, latitude)
                Log.d("테스트", tp.toString())
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000,
            0f,
            locationListener
        )

    }

    //원 그리기
    private fun drawCircle() {
        val tMapPoint = TMapPoint(latitude, longitude)

        //위치 옮기고 원을 다시 그려주기 전에 이전의 원 지우기
        tMapView.removeAllTMapCircle()

        //500m 원
        val tMapCircle = TMapCircle()
        tMapCircle.centerPoint = tMapPoint
        tMapCircle.radius = minRadius
        tMapCircle.circleWidth = 1f
        tMapCircle.lineColor = Color.GRAY
        tMapCircle.areaColor = Color.GRAY
        tMapCircle.areaAlpha = 50
        tMapView.addTMapCircle("circle1", tMapCircle)

        //100m 원
        val tMapCircleSmall = TMapCircle()
        tMapCircleSmall.centerPoint = tMapPoint
        tMapCircleSmall.radius = radius100
        tMapCircleSmall.circleWidth = 1f
        tMapCircle.lineColor = Color.LTGRAY
        tMapCircleSmall.areaColor = Color.LTGRAY
        tMapCircleSmall.areaAlpha = 50
        tMapView.addTMapCircle("circle2", tMapCircleSmall)
    }

    //편의점 찾기
    private fun findCVS() {
        val tMapData = TMapData()

        //지도를 내 현재위치로, 지도의 센터포인트를 내 현재위치로
        gpsTracker = GpsTracker(requireContext())
        latitude = gpsTracker!!.getLatitude()
        longitude = gpsTracker!!.getLongitude()
        tMapPoint = TMapPoint(latitude, longitude)
        markerItem1 = TMapMarkerItem()
        markerItem1!!.setIcon(bitmap) //마커핀 이미지 연결
        markerItem1!!.setPosition(0.5f, 1.0f) //마커핀 위치 조정
        markerItem1!!.setTMapPoint(tMapPoint) //마커핀 위치 연결
        tMapView.addMarkerItem("현재 나의 위치", markerItem1)

        //지도 중심 좌표 조정
        tMapView.setCenterPoint(longitude, latitude, false)
        tMapView.setLocationPoint(longitude, latitude)

        //500m, 100m 원 그리는 메소드
        drawCircle()

        var itemInfo = TMapPOIItem()
        //"편의점" 키워드로 검색
        tMapData.findAroundNamePOI(tMapPoint, "편의점",
            FindAroundNamePOIListenerCallback { poiItem ->
                if (poiItem == null) return@FindAroundNamePOIListenerCallback
                val tMapPointStart = TMapPoint(latitude, longitude) // 출발지
                tMapView.removeAllMarkerItem()
//                var minDistance = Double.POSITIVE_INFINITY
                var minDistancePoint: TMapPoint? = null


                //텍스트 뷰에 넣을 편의점 정보
//                var itemInfo = TMapPOIItem()
                for (i in 0 until poiItem.size) {
                    val item = poiItem[i] as TMapPOIItem
                    val distance = item.getDistance(tMapPointStart)


                    //500m 안에 있는 편의점들 마커핀으로 표시
                    if (distance < minRadius) {
                        val markerItem = TMapMarkerItem()
                        markerItem.icon = bitmap2
                        markerItem.setPosition(0.5f, 1.0f)
                        markerItem.tMapPoint = item.poiPoint // 마커의 좌표 지정
                        markerItem.name = item.poiName.toString()
                        markerItem.canShowCallout = true
                        markerItem.calloutTitle = item.poiName.toString()
                        tMapView.addMarkerItem("poi_$i", markerItem)
                        val tMapPointEnd = item.poiPoint
                        if (distance < minDistance) {
                            minDistance = distance
                            minDistancePoint = tMapPointEnd
                            itemInfo = poiItem[i] as TMapPOIItem
                        }
                    }
                }

                pointFrom = tMapPointStart
                if (minDistancePoint != null) {
                    pointTo = minDistancePoint
                }

                try {
                    val minDistancePolyLine = TMapData().findPathDataWithType(
                        TMapPathType.PEDESTRIAN_PATH,
                        tMapPointStart,
                        minDistancePoint
                    )

                    // //내 위치 마커
                    markerItem1!!.setTMapPoint(tMapPoint)
                    tMapView.addMarkerItem("현재 나의 위치", markerItem1)

                    //가까운 거리 인식 되면 선 그리고 편의점 정보 텍스트뷰에 올리기
                    if (minDistancePolyLine != null) {
                        minDistancePolyLine.lineColor = R.color.nooni
                        minDistancePolyLine.outLineColor = R.color.nooni
                        minDistancePolyLine.lineWidth = 5f
//                        tMapView.addTMapPolyLine("minDistanceLine", minDistancePolyLine)
                        tMapView.addTMapPath(minDistancePolyLine)
                        binding.textView5.text = itemInfo.poiName

                        mainActivity.tts.speak("현 위치에서 가장 가까운 편의점은 ${itemInfo.poiName} 이며, 거리는 ${minDistance.toInt()} 미터입니다.", TextToSpeech.QUEUE_ADD, null)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

        binding.textView5.setOnClickListener {
            mainActivity.ttsSpeak("길 안내를 시작하시겠습니까?")
            showSelectDialog()
        }
    }

    override fun onLocationChange(p0: Location?) {
        tMapView.setCenterPoint(p0!!.longitude, p0!!.latitude, true)
        tMapView.setLocationPoint(p0!!.longitude, p0!!.latitude)
    }

    private fun showSelectDialog(){
        SelectDialog(requireContext())
            .setContent("길 안내를 시작하시겠습니까?")
            .setOnNegativeClickListener{
                mainActivity.tts.stop()
            }
            .setOnPositiveClickListener{
                startNavi()
            }.build().show()
    }

    private fun startNavi(){
        tMapGpsManager = TMapGpsManager(requireContext())
        tMapGpsManager.apply{
            minTime = 1000
            minDistance = 1F
            provider = TMapGpsManager.NETWORK_PROVIDER
        }
        tMapGpsManager.OpenGps()

        // 이동경로에 대한 description 받아오기
        val tMapData = TMapData()
        tMapData.findPathDataAllType(TMapPathType.PEDESTRIAN_PATH, pointFrom, pointTo,
            FindPathDataAllListenerCallback { document ->
                val root: Element = document.documentElement
                val nodeListPlacemark: NodeList = root.getElementsByTagName("Placemark")
                Log.d("DOC", "startNavi:node length = ${nodeListPlacemark.length} ")
                for (i in 0 until nodeListPlacemark.length) {

                    val nodeListPlacemarkItem: NodeList = nodeListPlacemark.item(i).childNodes
                    for (j in 0 until nodeListPlacemarkItem.length) {
                        Log.d("DOC", "${nodeListPlacemarkItem.item(j).nodeName} = ${nodeListPlacemarkItem.item(j).textContent} ")
                        if (nodeListPlacemarkItem.item(j).nodeName.equals("description")) {
                            Log.d("debug", "#$i : ${nodeListPlacemarkItem.item(j).textContent.trim()}")
                            mainActivity.ttsSpeak(("${nodeListPlacemarkItem.item(j).textContent.trim()}"))
                        }
                    }
                }
            })

        tMapGpsManager.CloseGps()
    }

}