package com.yj.autoease.fragments

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.MarkerIcons
import com.yj.autoease.R
import com.yj.autoease.models.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {
    private var myPosition = Location("my").apply{
        latitude = 37.481945
        longitude = 126.879523
    }
    private var carPosition = Location("car").apply {
        latitude = 37.481084
        longitude = 126.882164
    }
    private lateinit var naverMap: NaverMap
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapContainerView) as MapFragment ?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.mapContainerView, it).commit()
            }
        mapFragment.getMapAsync(this)
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val distanceText = view.findViewById<TextView>(R.id.mapText)

        val distance = myPosition.distanceTo(carPosition)
        val addressText = view.findViewById<TextView>(R.id.geoText)
        distanceText.text = "${String.format("%.2f",distance)}M"
        lifecycleScope.launch {
            val myAddress = getAddressFromLatLngAsync(myPosition.latitude, myPosition.longitude)
            addressText.text = myAddress
        }
        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.481945, 126.879523))
        naverMap.moveCamera(cameraUpdate)
        lifecycleScope.launch {
            //현재 위치 (일단 가산으로 고정함)
            addMarker(myPosition, "my")
            // 차 위치
            addMarker(carPosition, "car")
        }
    }

    private fun addMarker(position: Location, type: String){
        val marker = Marker()
        marker.position = LatLng(position.latitude,position.longitude)
        if(position.provider == "my"){
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
        }
        marker.map = naverMap
    }

    private suspend fun getAddressFromLatLngAsync(latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses!!.isNotEmpty()) {
                    addresses!![0].getAddressLine(0)
                } else {
                    "주소를 찾을 수 없습니다."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Geocoder 오류: ${e.message}"
            }
        }
    }


}
