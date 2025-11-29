package com.taskwheel.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskwheel.app.data.WheelRepository
import com.taskwheel.app.data.WheelSegment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WheelViewModel(private val repository: WheelRepository) : ViewModel() {

    private val _segments = MutableStateFlow<List<WheelSegment>>(emptyList())
    val segments: StateFlow<List<WheelSegment>> = _segments.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _selectedSegment = MutableStateFlow<WheelSegment?>(null)
    val selectedSegment: StateFlow<WheelSegment?> = _selectedSegment.asStateFlow()

    init {
        loadSegments()
    }

    private fun loadSegments() {
        viewModelScope.launch {
            repository.segmentsFlow.collect { segments ->
                _segments.value = segments
            }
        }
    }

    fun saveSegments(segments: List<WheelSegment>) {
        viewModelScope.launch {
            repository.saveSegments(segments)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaults()
        }
    }

    fun startSpin() {
        _isSpinning.value = true
        _selectedSegment.value = null
    }

    fun stopSpin(segment: WheelSegment) {
        _isSpinning.value = false
        _selectedSegment.value = segment
    }

    fun clearSelection() {
        _selectedSegment.value = null
    }

    class Factory(private val repository: WheelRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WheelViewModel::class.java)) {
                return WheelViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
