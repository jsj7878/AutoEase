package com.yj.autoease

import android.os.Bundle
import android.text.Editable
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

        val modelRow = findViewById<LinearLayout>(R.id.modelRow)
        val modelYearRow = findViewById<LinearLayout>(R.id.modelYearRow)

        val slideInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 9) {
                    if(s.toString() == "123가 4567") {
                        spinner.visibility = View.VISIBLE

                        lifecycleScope.launch {
                            delay(2000)

                            modelRow.visibility = View.VISIBLE
                            modelRow.startAnimation(slideInAnim)

                            delay(2000)

                            modelYearRow.visibility = View.VISIBLE
                            modelYearRow.startAnimation(slideInAnim)

                            spinner.visibility = View.GONE
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}