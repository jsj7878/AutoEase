package com.yj.autoease

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.yj.autoease.activities.MainActivity
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
                    if(s.toString() == "123가 4567") {
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
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}