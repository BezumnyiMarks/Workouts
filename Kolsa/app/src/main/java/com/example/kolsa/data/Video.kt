package com.example.kolsa.data

import com.google.gson.annotations.SerializedName


data class Video(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("link")
    val link: String = "",
)
