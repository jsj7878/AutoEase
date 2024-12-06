package com.yj.autoease.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.yj.autoease.R
import com.yj.autoease.models.Car
import com.yj.autoease.models.Schedule
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment() {
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var scheduleTextView: TextView
    private lateinit var addButtonWrapper: LinearLayout
    private var car: Car? = null
    private var selectedDate: String = ""
    private var selectedSchedule: Schedule? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        selectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        getCar()

        calendarView = view.findViewById(R.id.calendarView)
        addButtonWrapper = view.findViewById(R.id.addButtonWrapper)
        scheduleTextView = view.findViewById(R.id.scheduleTextView)
        val addButton = view.findViewById<TextView>(R.id.addButton)

        calendarView.setOnDateChangedListener { _, date, _ ->
            Log.d("@@@@@", "onDateChanged Call")
            selectedDate = "${date.year}-${date.month}-${if(date.day < 10) "0${date.day}" else date.day}"
            selectedSchedule = car!!.schedules.find { schedule ->
                return@find schedule.date == selectedDate
            }

            if(selectedSchedule == null) {
                scheduleTextView.visibility = View.GONE
                addButtonWrapper.visibility = View.VISIBLE
            } else {
                scheduleTextView.text = selectedSchedule!!.content
                scheduleTextView.visibility = View.VISIBLE
                addButtonWrapper.visibility = View.GONE
            }
        }

        addButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_schedule, null)

            builder.setView(dialogView)
                .setPositiveButton("저장") { _, _ ->
                val scheduleContentEditText = dialogView.findViewById<EditText>(R.id.scheduleContentEditText)
                val newScheduleContent = scheduleContentEditText.text.toString()
                val newSchedule = Schedule(selectedDate, newScheduleContent)

                car!!.schedules.add(newSchedule)

                updateCar(newSchedule)
            }.create().show()
        }

        return view
    }

    private fun updateCar(newSchedule: Schedule) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if(userId == null) {
            Toast.makeText(requireContext(), "로그인 유저 정보가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val db = Firebase.firestore

        db.collection("cars")
            .document(userId)
            .update("schedules", car!!.schedules)
            .addOnSuccessListener {
                Log.d("@@@@@", "SUCCESS")
                calendarView.removeDecorators()
                calendarView.invalidateDecorators()
                calendarView.addDecorators(
                    todayDecorator(requireContext()),
                    eventDecorator(requireContext())
                )

                selectedSchedule = newSchedule
                selectedDate = newSchedule.date

                scheduleTextView.text = newSchedule.content
                scheduleTextView.visibility = View.VISIBLE
                addButtonWrapper.visibility = View.GONE
            }
            .addOnFailureListener { Log.e("@@@@@", "FAILED") }
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
                    Log.d("@@@@@", document.toString()) // schedules:[{content:123123,date:2024-12-17},{content:451,date:2024-12-19}]
                    val foundCar = document.toObject(Car::class.java).copy(id = document.id)

                    car = foundCar

                    calendarView.removeDecorators()
                    calendarView.invalidateDecorators()
                    calendarView.addDecorators(
                        todayDecorator(requireContext()),
                        eventDecorator(requireContext())
                    )
                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                println("Error getting documents: $exception")
            }
    }

    fun todayDecorator(context: Context): DayViewDecorator {
        return object: DayViewDecorator {
            private val drawable = ContextCompat.getDrawable(context, R.drawable.calendar_circle_today)
            private val today = CalendarDay.today()

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == today
            }

            override fun decorate(view: DayViewFacade?) {
                view?.apply {
                    setBackgroundDrawable(drawable!!)

                }
            }
        }
    }

    fun eventDecorator(context: Context): DayViewDecorator {
        return object: DayViewDecorator {
            private val events = HashSet<CalendarDay>()

            init {
                if( car != null ) {
                    car!!.schedules.forEach { schedule ->
                        val dateTime = LocalDate.parse(schedule.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                        events.add(CalendarDay.from(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth))
                    }
                }
            }

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return events.contains(day)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.apply {
                    view.addSpan(DotSpan(10F, ContextCompat.getColor(context, R.color.red)))
                }
            }
        }
    }
}