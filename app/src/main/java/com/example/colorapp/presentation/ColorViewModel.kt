package com.example.colorapp.presentation

import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.colorapp.data.ColorEntity
import com.example.colorapp.data.ColorRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ColorViewModel @Inject constructor(private val repository: ColorRepository) : ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _colors = MutableStateFlow<ColorState>(ColorState.Loading)
    val colors: StateFlow<ColorState> = _colors.asStateFlow()

    private val _pendingColors = MutableStateFlow(0)
    val pendingColors: StateFlow<Int> = _pendingColors.asStateFlow()

    init {
        getColors()
    }

    fun getColors() {
        viewModelScope.launch {
            repository.getUnSyncedColors().observeForever { unsyncedColors ->
                repository.getAllColors().observeForever { allColors ->
                    _colors.value = ColorState.Success(
                        allColors = allColors,
                        unsyncedColors = unsyncedColors
                    )
                    _pendingColors.value = unsyncedColors.size
                }
            }
        }
    }

    fun addColor(color: ColorEntity) {
        viewModelScope.launch {
            repository.insertColor(color)
            getColors()
            incrementPendingColors()
        }
    }

    fun syncColors() {
        viewModelScope.launch {
            try {
                Log.d("ColorViewModel", "Starting syncColors function...")

                repository.getUnSyncedColors().observeForever { colors ->
                    if (colors.isNotEmpty()) {
                        val colorsRef = firebaseDatabase.reference.child("colors")

                        val totalColors = colors.size
                        var syncedColors = 0

                        for (color in colors) {
                            val newColorRef = colorsRef.push()
                            val colorData = mapOf(
                                "color" to color.color,
                                "time" to color.time
                            )

                            newColorRef.setValue(colorData)
                                .addOnSuccessListener {
                                    syncedColors++
                                    Log.d("ColorViewModel", "Successfully synced color: ${color.color}")

                                    // Mark color as synced in the local database
                                    viewModelScope.launch {
                                        repository.markColorAsSynced(color.id)
                                    }

                                    if (syncedColors == totalColors) {
                                        resetPendingColors()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ColorViewModel", "Error syncing color ${color.color}: ${e.message}")
                                }
                        }
                    } else {
                        Log.d("ColorViewModel", "No colors to sync; the list is empty.")
                        resetPendingColors()
                    }
                }
            } catch (e: Exception) {
                Log.e("ColorViewModel", "Error syncing colors: ${e.message}")
            }
        }
    }

    private fun incrementPendingColors() {
        _pendingColors.value++
    }

    private fun resetPendingColors() {
        _pendingColors.value = 0
    }
}

sealed class ColorState {
    object Loading : ColorState()
    data class Success(
        val allColors: List<ColorEntity>,
        val unsyncedColors: List<ColorEntity>
    ) : ColorState()
    data class Error(val message: String) : ColorState()
}
