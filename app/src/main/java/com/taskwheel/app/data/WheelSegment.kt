package com.taskwheel.app.data

import androidx.compose.ui.graphics.Color

data class WheelSegment(
    val id: Int,
    val label: String,
    val color: Color
) {
    companion object {
        fun getDefaultSegments(): List<WheelSegment> = listOf(
            WheelSegment(1, "Work", Color(0xFF1976D2)),              // Blue
            WheelSegment(2, "Reading books", Color(0xFF388E3C)),      // Green
            WheelSegment(3, "3D printing", Color(0xFFD32F2F)),        // Red
            WheelSegment(4, "Vedanta", Color(0xFF7B1FA2)),            // Purple
            WheelSegment(5, "Bike Rides/Travel", Color(0xFFF57C00)),  // Orange
            WheelSegment(6, "Exploring Finance", Color(0xFF0097A7)),  // Cyan
            WheelSegment(7, "Tabla/Hindustani music", Color(0xFFC2185B)), // Pink
            WheelSegment(8, "Freelancing work", Color(0xFF5D4037))    // Brown
        )
    }
}
