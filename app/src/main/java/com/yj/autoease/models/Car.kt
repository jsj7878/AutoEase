package com.yj.autoease.models

data class Car(
    val id: String = "",
    val userId: String = "",
    val vehicleNumber: String = "",
    var isPowerOn: Boolean = false,
    var isDoorOpen: Boolean = false,
    var isSoundOn: Boolean = false,
    var temperature: Double = 0.0,
    var fuel: Int = 0,
    var tirePressure: Int = 0,
    var schedules: MutableList<Schedule> = mutableListOf()
)
