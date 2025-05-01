package com.example.kolsa

import com.example.kolsa.data.Video
import com.example.kolsa.data.Workout
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val mainApi: MainApi
) {
    suspend fun getWorkouts(): List<Workout>{
        return mainApi.getWorkouts()
    }

    suspend fun getVideo(id: Int): Video{
        return mainApi.getVideo(id)
    }
}