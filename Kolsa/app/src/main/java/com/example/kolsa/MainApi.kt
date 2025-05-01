package com.example.kolsa

import com.example.kolsa.data.Video
import com.example.kolsa.data.Workout
import retrofit2.http.GET
import retrofit2.http.Query

interface MainApi {
    @GET("/get_workouts")
    suspend fun getWorkouts(): List<Workout>

    @GET("/get_video")
    suspend fun getVideo(
        @Query("id") id: Int
    ): Video
}