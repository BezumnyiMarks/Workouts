package com.example.kolsa.viewmodels

import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kolsa.MainRepository
import com.example.kolsa.data.Video
import com.example.kolsa.data.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.compareTo

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Empty)
    val networkState = _networkState.asStateFlow()

    private val _videoStateFlow = MutableStateFlow<Video>(Video())
    val videoStateFlow = _videoStateFlow.asStateFlow()

    fun getVideo(id: Int){
        viewModelScope.launch {
            _networkState.emit(NetworkState.Loading)
            runCatching {
                mainRepository.getVideo(id)
            }.fold (
                onSuccess = {
                    _videoStateFlow.emit(it)
                    _networkState.emit(NetworkState.Success)
                },
                onFailure = {
                    Log.d("ERROR", it.message.toString())
                    _networkState.emit(NetworkState.Error)
                }
            )
        }
    }

    sealed class NetworkState {
        data object Loading: NetworkState()
        data object Success: NetworkState()
        data object Error: NetworkState()
        data object Empty: NetworkState()
    }
}