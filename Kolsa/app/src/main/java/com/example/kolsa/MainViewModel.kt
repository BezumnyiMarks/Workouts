package com.example.kolsa

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kolsa.data.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    //private val filtersApplied = MutableStateFlow<List<FiltersApplied>>(listOf(
    //    FiltersApplied.Status(SelectedStatus.None)
    //))
    //val filters = filtersApplied.asStateFlow()

    //private val sortApplied = MutableStateFlow<SortApplied>(SortApplied.Default)
    //val sort = sortApplied.asStateFlow()

    private val _workoutsStateFlow = MutableStateFlow<List<Workout>>(listOf())
    val workoutsStateFlow = _workoutsStateFlow.asStateFlow()

    fun getWorkouts(){
        viewModelScope.launch {
            kotlin.runCatching {
                mainRepository.getWorkouts()
            }.fold (
                onSuccess = {
                    _workoutsStateFlow.emit(it)
                },
                onFailure = {
                    Log.d("ERROR", it.message.toString())
                }
            )
        }
    }
}