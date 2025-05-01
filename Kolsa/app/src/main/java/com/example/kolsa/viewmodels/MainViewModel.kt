package com.example.kolsa.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kolsa.MainRepository
import com.example.kolsa.data.Video
import com.example.kolsa.data.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Empty)
    val networkState = _networkState.asStateFlow()

    private val filtersApplied =
        MutableStateFlow<List<FiltersApplied>>(listOf(FiltersApplied.Type(0)))
    val filters = filtersApplied.asStateFlow()

    private val _workoutsStateFlow = MutableStateFlow<List<Workout>>(listOf())

    private val _filteredWorkoutsStateFlow = MutableStateFlow<List<Workout>>(listOf())
    val filteredWorkoutsStateFlow = _filteredWorkoutsStateFlow.asStateFlow()

    init {
        applyFilters()
    }

    fun getWorkouts(){
        viewModelScope.launch {
            _networkState.emit(NetworkState.Loading)
            runCatching {
                mainRepository.getWorkouts()
            }.fold (
                onSuccess = {
                    _workoutsStateFlow.emit(it)
                    _filteredWorkoutsStateFlow.emit(it)
                    _networkState.emit(NetworkState.Success)
                },
                onFailure = {
                    Log.d("ERROR", it.message.toString())
                    _networkState.emit(NetworkState.Error)
                }
            )
        }
    }

    fun applyFilters(){
        viewModelScope.launch {
            filters.collect { filters ->
                var filteredWorkouts = _workoutsStateFlow.value
                filters.forEach { filter ->
                    filteredWorkouts = when(filter){
                        is FiltersApplied.Keyword -> {
                            filteredWorkouts.filter {
                                it.title.lowercase(Locale.ROOT)
                                    .contains(
                                        filter.keyword.lowercase(Locale.ROOT)
                                    )
                            }
                        }

                        is FiltersApplied.Type -> {
                            filteredWorkouts.filter {
                                it.type == filter.type
                            }
                        }
                    }
                }
                _filteredWorkoutsStateFlow.emit(filteredWorkouts)
            }
        }
    }

    fun setKeywordFilter(currentKeyword: String){
        viewModelScope.launch {
            val filtersList = filtersApplied.value.toMutableList()
            filtersList.remove(filtersList.find {
                it is FiltersApplied.Keyword
            })
            if (currentKeyword.isNotEmpty())
                filtersList.add(FiltersApplied.Keyword(currentKeyword))
            filtersApplied.emit(filtersList.toList())
        }
    }

    fun setTypeFilter(selectedType: FiltersApplied.Type){
        viewModelScope.launch {
            val filtersList = filtersApplied.value.toMutableList()
            filtersList.remove(filtersList.find {
                it is FiltersApplied.Type
            })
            if (selectedType.type != 0L)
                filtersList.add(selectedType)
            filtersApplied.emit(filtersList.toList())
        }
    }

    sealed class FiltersApplied {
        data class Keyword(val keyword: String): FiltersApplied()
        data class Type(val type: Long): FiltersApplied()
    }

    sealed class NetworkState {
        data object Loading: NetworkState()
        data object Success: NetworkState()
        data object Error: NetworkState()
        data object Empty: NetworkState()
    }
}