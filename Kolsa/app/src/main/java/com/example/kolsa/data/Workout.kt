package com.example.kolsa.data

import com.google.gson.annotations.SerializedName


data class Workout(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("type")
    val type: Long = 0,
    @SerializedName("duration")
    val duration: String =""
)
