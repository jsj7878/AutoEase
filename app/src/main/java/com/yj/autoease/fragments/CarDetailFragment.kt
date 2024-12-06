package com.yj.autoease.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.yj.autoease.R
import com.yj.autoease.models.Car

class CarDetailFragment : Fragment() {
    private lateinit var view : View
    private var car: Car? = null
    private lateinit var carText : TextView
    private lateinit var tempText : TextView
    private lateinit var seekbar : SeekBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_car_detail, container, false)
        carText = view.findViewById<TextView>(R.id.carText)
        tempText = view.findViewById<TextView>(R.id.tempText)
        getCar()
        seekbar = view.findViewById<SeekBar>(R.id.seekBar)
        seekbar.max = 1000
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var displayValue : Double = 0.0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                displayValue = progress / 10.0 // 소수점 변환
                tempText.text = String.format("%.1f", displayValue) + "°C" // 소수점 1자리 표시
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }


            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 터치 종료 시 필요한 동작
                updateTemp(displayValue)
            }
        })
        val buttonArray = arrayOf(
            view.findViewById<ImageButton>(R.id.lockButton),
            view.findViewById<ImageButton>(R.id.blueButton),
            view.findViewById<ImageButton>(R.id.clarkButton),
            view.findViewById<ImageButton>(R.id.emerButton),
            view.findViewById<ImageButton>(R.id.heaterButton),
            view.findViewById<ImageButton>(R.id.powerButton)
        )
        for (button in buttonArray){
                button.setOnClickListener {
                    button.isSelected = !button.isSelected
                    when(button.id){
                        R.id.lockButton ->{
                            button.setImageResource(if (button.isSelected) R.drawable.ic_lock else R.drawable.ic_lockopen)
                            updateState(R.id.lockButton)
                            Toast.makeText(requireContext(), if(button.isSelected)"차 문이 잠깁니다" else "차 문이 열립니다",Toast.LENGTH_SHORT).show()
                        }
                        R.id.powerButton ->{
                            updateState(R.id.powerButton)
                            Toast.makeText(requireContext(), if(button.isSelected)"시동이 걸렸습니다" else "시동이 꺼졌습니다",Toast.LENGTH_SHORT).show()
                        }
                        R.id.clarkButton ->{
                            updateState(R.id.clarkButton)
                            Toast.makeText(requireContext(), if(button.isSelected)"경적이 울립니다" else "경적이 꺼졌습니다",Toast.LENGTH_SHORT).show()
                        }
                        R.id.blueButton ->{
                            Toast.makeText(requireContext(), if(button.isSelected)"블루투스 연결" else "블루투스 해제",Toast.LENGTH_SHORT).show()
                        }
                        R.id.heaterButton ->{
                            Toast.makeText(requireContext(), if(button.isSelected)"히터 켜짐" else "히터 꺼짐",Toast.LENGTH_SHORT).show()
                        }
                        R.id.emerButton ->{
                            Toast.makeText(requireContext(), if(button.isSelected)"비상등 켜짐" else "비상등 꺼짐",Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
        return view
    }
    private fun getCar() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if(userId == null) {
            Toast.makeText(requireContext(), "로그인 유저 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val db = com.google.firebase.ktx.Firebase.firestore

        db.collection("cars")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // 데이터 변환, 문서 ID도 포함
                    Log.d("@@@@@", document.toString()) // schedules:[{content:123123,date:2024-12-17},{content:451,date:2024-12-19}]
                    val foundCar = document.toObject(Car::class.java).copy(id = document.id)
                    car = foundCar
                    carText.text = car?.vehicleNumber
                    tempText.text = "${car?.temperature}°C"
                    seekbar.progress = car!!.temperature.toInt()*10
                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                println("Error getting documents: $exception")
            }
    }

    private fun updateTemp(temp: Double){
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if(userId == null) {
            Toast.makeText(requireContext(), "로그인 유저 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        val db = Firebase.firestore
        db.collection("cars")
            .document(userId)
            .update("temperature",temp)
            .addOnSuccessListener {
                Log.d("@@@@@", "SUCCESS")
            }
            .addOnFailureListener { Log.e("@@@@@", "FAILED") }
    }

    private fun updateState(id : Int){
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if(userId == null) {
            Toast.makeText(requireContext(), "로그인 유저 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        val db = Firebase.firestore
        when(id){
            R.id.powerButton ->{
                db.collection("cars")
                    .document(userId)
                    .update("powerOn", !car!!.isPowerOn)
                    .addOnSuccessListener {
                        Log.d("@@@@@", "SUCCESS")
                    }
                    .addOnFailureListener { Log.e("@@@@@", "FAILED") }
            }
            R.id.lockButton ->{
                db.collection("cars")
                    .document(userId)
                    .update("doorOpen", !car!!.isDoorOpen)
                    .addOnSuccessListener {
                        Log.d("@@@@@", "SUCCESS")
                    }
                    .addOnFailureListener { Log.e("@@@@@", "FAILED") }
            }
            R.id.clarkButton ->{
                db.collection("cars")
                    .document(userId)
                    .update("isSoundOn", !car!!.isSoundOn)
                    .addOnSuccessListener {
                        Log.d("@@@@@", "SUCCESS")
                    }
                    .addOnFailureListener { Log.e("@@@@@", "FAILED") }
            }
            else -> {
                return
            }
        }


    }

}