package com.yj.autoease.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yj.autoease.R
import com.yj.autoease.models.Car
import org.w3c.dom.Text

class MainFragment : Fragment() {
    private lateinit var vehicleNumberTextView: TextView
    private lateinit var noLatestTextView: TextView
    private lateinit var latestWrapper: LinearLayout
    private lateinit var latestDateTextView: TextView
    private lateinit var latestContentTextView: TextView
    private lateinit var car: Car

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val buttonArray = arrayOf(
            view.findViewById<ImageButton>(R.id.buttonLock),
            view.findViewById<ImageButton>(R.id.buttonPower),
            view.findViewById<ImageButton>(R.id.buttonSound)
        )

        vehicleNumberTextView = view.findViewById(R.id.vehicleNumberTextView)
        noLatestTextView = view.findViewById(R.id.noLatestTextView)
        latestWrapper = view.findViewById(R.id.latestWrapper)
        latestDateTextView = view.findViewById(R.id.latestDateTextView)
        latestContentTextView = view.findViewById(R.id.latestContentTextView)

        for (button in buttonArray){
            if(button.id == R.id.buttonLock){
                button.setOnClickListener {
                    button.isSelected = !button.isSelected
                    button.setImageResource(if (button.isSelected) R.drawable.ic_lock else R.drawable.ic_lockopen)
                }
            } else{
                button.setOnClickListener {
                    button.isSelected = !button.isSelected
                }
            }
        }

        getCar()

        return view
    }

    private fun getCar() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if(userId == null) {
            Toast.makeText(requireContext(), "로그인 유저 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val db = Firebase.firestore

        db.collection("cars")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // 데이터 변환, 문서 ID도 포함
                    val foundCar = document.toObject(Car::class.java).copy(id = document.id)

                    car = foundCar

                    vehicleNumberTextView.text = foundCar.vehicleNumber

                    if(foundCar.schedules.isNotEmpty()) {
                        foundCar.schedules.sortByDescending { schedule ->
                            schedule.date
                        }

                        latestDateTextView.text = foundCar.schedules[0].date
                        latestContentTextView.text = foundCar.schedules[0].content

                        latestWrapper.visibility = View.VISIBLE
                        noLatestTextView.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                println("Error getting documents: $exception")
            }
    }
}