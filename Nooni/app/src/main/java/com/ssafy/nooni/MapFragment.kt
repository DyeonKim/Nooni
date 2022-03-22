package com.ssafy.nooni

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapView
import com.ssafy.nooni.databinding.FragmentMapBinding
import android.location.LocationManager

import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import android.location.LocationListener
import android.util.Log


class MapFragment : Fragment(), TMapGpsManager.onLocationChangedCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mainActivity: MainActivity

    private val key = BuildConfig.TMAP_API_KEY

    private lateinit var tMapView: TMapView
    private lateinit var tMapGpsManager: TMapGpsManager

    var isFirst = true

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

        setAuth()
        setMap()
        setGps()

    }

    private fun setAuth(){
        tMapView = TMapView(requireContext())
        tMapGpsManager = TMapGpsManager(requireActivity())

        tMapView.setSKTMapApiKey(key)
    }

    private fun setMap(){
        tMapView.zoomLevel =  17
        tMapView.setIconVisibility(true)
        tMapView.mapType = TMapView.MAPTYPE_STANDARD
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN)

        binding.llTmap.addView(tMapView)
    }

    private fun setGps() {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
        }
        lm.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,  // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
            1000, 1f,  // 통지사이의 최소 변경거리 (m)
            mLocationListener
        )
    }

    override fun onLocationChange(location: Location) {
        tMapView.setLocationPoint(location.longitude, location.latitude)
        tMapView.setCenterPoint(location.longitude, location.latitude)
    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            //현재위치의 좌표를 알수있는 부분
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                if (isFirst) {
                    tMapView.setCenterPoint(longitude, latitude)
                    isFirst = false
                }
                tMapView.setLocationPoint(longitude, latitude)
                Log.d("TmapTest", "$longitude,$latitude")
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }



}