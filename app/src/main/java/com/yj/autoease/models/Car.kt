package com.yj.autoease.models

data class Car(
    val id: String,
    val userId: String,
    val vehicleNumber: String,
    var isPowerOn: Boolean,
    var isDoorOpen: Boolean,
    var temperature: Double,
    var fuel: Int,
    var tirePressure: Int,
    var schedules: MutableList<Schedule>
)
