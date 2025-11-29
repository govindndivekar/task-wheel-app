package com.taskwheel.app.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wheel_settings")

class WheelRepository(private val context: Context) {

    private object PreferencesKeys {
        val SEGMENTS = stringPreferencesKey("segments")
    }

    val segmentsFlow: Flow<List<WheelSegment>> = context.dataStore.data
        .map { preferences ->
            val segmentsJson = preferences[PreferencesKeys.SEGMENTS]
            if (segmentsJson.isNullOrEmpty()) {
                WheelSegment.getDefaultSegments()
            } else {
                deserializeSegments(segmentsJson)
            }
        }

    suspend fun saveSegments(segments: List<WheelSegment>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEGMENTS] = serializeSegments(segments)
        }
    }

    suspend fun resetToDefaults() {
        saveSegments(WheelSegment.getDefaultSegments())
    }

    private fun serializeSegments(segments: List<WheelSegment>): String {
        return segments.joinToString("|") { segment ->
            "${segment.id},${segment.label},${segment.color.toArgb()}"
        }
    }

    private fun deserializeSegments(json: String): List<WheelSegment> {
        return try {
            json.split("|").map { segmentStr ->
                val parts = segmentStr.split(",")
                WheelSegment(
                    id = parts[0].toInt(),
                    label = parts[1],
                    color = Color(parts[2].toInt())
                )
            }
        } catch (e: Exception) {
            WheelSegment.getDefaultSegments()
        }
    }
}
