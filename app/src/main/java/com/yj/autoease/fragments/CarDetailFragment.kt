package com.yj.autoease.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.yj.autoease.R
import com.yj.autoease.models.Car

class CarDetailFragment : Fragment() {

    private var car: Car? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        getCar()
        val view = inflater.inflate(R.layout.fragment_car_detail, container, false)
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
                        }
                        R.id.powerButton ->{

                        }
                        R.id.clarkButton ->{

                        }
                        R.id.blueButton ->{

                        }
                        R.id.heaterButton ->{

                        }
                        R.id.emerButton ->{

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

                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                println("Error getting documents: $exception")
            }
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