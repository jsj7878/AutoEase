package com.yj.autoease.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.yj.autoease.R

class MainFragment : Fragment() {


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

        return view
    }


}