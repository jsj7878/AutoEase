package com.yj.autoease.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.yj.autoease.R
import com.yj.autoease.models.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment() {
    private var schedules: MutableList<Schedule> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        schedules = getSchedules()

        val calendarView = view.findViewById<MaterialCalendarView>(R.id.calendarView)

        calendarView.addDecorators(todayDecorator(requireContext()), eventDecorator(requireContext()))

        return view
    }

    private fun getSchedules(): MutableList<Schedule> {
        return mutableListOf(
            Schedule("2024-12-15", "엔진오일 교체"),
            Schedule("2024-12-11", "타이어 공기압")
        )
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
                schedules.forEach { schedule ->
                    val dateTime = LocalDate.parse(schedule.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    events.add(CalendarDay.from(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth))
                }
            }

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return events.contains(day)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.apply {
                    view.addSpan(DotSpan(10F, ContextCompat.getColor(context, R.color.primary)))
                }
            }
        }
    }
}