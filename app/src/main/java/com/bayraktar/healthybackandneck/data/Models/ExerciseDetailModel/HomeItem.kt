package com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel


data class HomeItem(
    val homeId: Int= 0,
    val title : String,
    val desc: String,
    val imageMain : Int,
    val progress: Int,
    val dayOfProgram: Int
)
