package com.yj.autoease.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import com.yj.autoease.R

class CarDetailFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        //            if(button.id == R.id.lockButton){
//                button.setOnClickListener {
//                    button.isSelected = !button.isSelected
//                }
//                button.setImageResource(if (button.isSelected) R.drawable.ic_lock else R.drawable.ic_lockopen)
//            } else {
//                button.setOnClickListener {
//                    button.isSelected = !button.isSelected
//                }
//            }
        return view
    }

}