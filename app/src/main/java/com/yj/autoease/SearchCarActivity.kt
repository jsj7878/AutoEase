package com.yj.autoease

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yj.autoease.activities.MainActivity
import com.yj.autoease.models.Car
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchCarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_car)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val spinner = findViewById<ProgressBar>(R.id.spinner)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)

        val modelRow = findViewById<LinearLayout>(R.id.modelRow)
        val modelYearRow = findViewById<LinearLayout>(R.id.modelYearRow)
        val frameRow = findViewById<LinearLayout>(R.id.frameRow)
        val loginButtonWrapper = findViewById<LinearLayout>(R.id.loginButtonWrapper)

        val slideInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 9) {
                    searchEditText.inputType = InputType.TYPE_NULL
                    spinner.visibility = View.VISIBLE

                    lifecycleScope.launch {
                        delay(2000)

                        modelRow.visibility = View.VISIBLE
                        modelRow.startAnimation(slideInAnim)

                        delay(2000)

                        modelYearRow.visibility = View.VISIBLE
                        modelYearRow.startAnimation(slideInAnim)

                        delay(2000)
                        frameRow.visibility = View.VISIBLE
                        frameRow.startAnimation(slideInAnim)
                        loginButtonWrapper.visibility = View.VISIBLE

                        spinner.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loginButton.setOnClickListener {
            val userId = getUserId()
            if(userId == null) {
                Toast.makeText(this, "로그인 유저 정보가 없습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newCar = Car("", userId, searchEditText.text.toString(),
                isPowerOn = false,
                isDoorOpen = false,
                isSoundOn = false,
                temperature = 36.5,
                fuel = 100,
                tirePressure = 30,
                mutableListOf()
            )

            saveCar(newCar)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserId(): String? {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser?.uid
    }

    private fun saveCar(car: Car) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("cars")
            .document(car.userId)
            .set(car)
            .addOnSuccessListener {
                // 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error writing document", e)
            }
    }
}