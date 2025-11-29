package com.taskwheel.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskwheel.app.data.WheelSegment
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SpinningWheel(
    segments: List<WheelSegment>,
    isSpinning: Boolean,
    onSpinComplete: (WheelSegment) -> Unit,
    modifier: Modifier = Modifier
) {
    if (segments.isEmpty()) return

    var targetRotation by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isSpinning) {
        if (isSpinning && segments.isNotEmpty()) {
            // Calculate random rotation (3-5 full rotations plus random segment)
            val fullRotations = Random.nextInt(3, 6) * 360f
            val randomAngle = Random.nextFloat() * 360f
            targetRotation = rotation.value + fullRotations + randomAngle

            // Animate the spin with easing
            rotation.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(
                    durationMillis = 3000,
                    easing = FastOutSlowInEasing
                )
            )

            // Calculate which segment was selected
            val normalizedRotation = (360f - (targetRotation % 360f)) % 360f
            val degreesPerSegment = 360f / segments.size
            val selectedIndex = (normalizedRotation / degreesPerSegment).toInt() % segments.size
            onSpinComplete(segments[selectedIndex])
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            val canvasSize = min(size.width, size.height)
            val radius = canvasSize / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            rotate(rotation.value, pivot = center) {
                drawWheel(
                    segments = segments,
                    center = center,
                    radius = radius
                )
            }

            // Draw the pointer/indicator at the top
            drawPointer(center, radius)
        }
    }
}

private fun DrawScope.drawWheel(
    segments: List<WheelSegment>,
    center: Offset,
    radius: Float
) {
    val degreesPerSegment = 360f / segments.size
    val startAngle = -90f // Start from top

    segments.forEachIndexed { index, segment ->
        val currentAngle = startAngle + (index * degreesPerSegment)

        // Draw segment
        drawArc(
            color = segment.color,
            startAngle = currentAngle,
            sweepAngle = degreesPerSegment,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // Draw segment border
        drawArc(
            color = Color.White,
            startAngle = currentAngle,
            sweepAngle = degreesPerSegment,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = 3f)
        )

        // Draw text label
        drawSegmentText(
            text = segment.label,
            center = center,
            radius = radius,
            angle = currentAngle + degreesPerSegment / 2,
            color = Color.White
        )
    }

    // Draw center circle
    drawCircle(
        color = Color.White,
        radius = radius * 0.15f,
        center = center
    )
    drawCircle(
        color = Color(0xFF424242),
        radius = radius * 0.12f,
        center = center
    )
}

private fun DrawScope.drawSegmentText(
    text: String,
    center: Offset,
    radius: Float,
    angle: Float,
    color: Color
) {
    // Normalize angle to 0-360 range
    val normalizedAngle = ((angle % 360f) + 360f) % 360f

    val textRadius = radius * 0.68f
    val angleRad = Math.toRadians(angle.toDouble())
    val x = center.x + textRadius * cos(angleRad).toFloat()
    val y = center.y + textRadius * sin(angleRad).toFloat()

    drawContext.canvas.nativeCanvas.apply {
        save()
        translate(x, y)

        // Calculate rotation: text should be perpendicular to radius
        // For right side (315-45 deg or 45-135 deg): rotate angle + 90
        // For left side (135-315 deg): rotate angle - 90 to keep text upright
        val textRotation = if (normalizedAngle in 90f..270f) {
            angle - 90f  // Left side - flip to keep readable
        } else {
            angle + 90f  // Right side - normal rotation
        }

        rotate(textRotation)

        // Split long text into multiple lines
        val lines = splitTextIntoLines(text, maxCharsPerLine = 12)
        val lineHeight = 12.sp.toPx()
        val totalHeight = lines.size * lineHeight
        val startY = -totalHeight / 2 + lineHeight / 2

        val paint = android.graphics.Paint().apply {
            this.color = color.toArgb()
            textSize = 11.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }

        val shadowPaint = android.graphics.Paint(paint).apply {
            this.color = android.graphics.Color.BLACK
            alpha = 150
        }

        // Draw each line
        lines.forEachIndexed { index, line ->
            val yPos = startY + (index * lineHeight)
            // Draw shadow
            drawText(line, 1f, yPos + 1f, shadowPaint)
            // Draw text
            drawText(line, 0f, yPos, paint)
        }

        restore()
    }
}

private fun splitTextIntoLines(text: String, maxCharsPerLine: Int): List<String> {
    // If text fits in one line, return it
    if (text.length <= maxCharsPerLine) {
        return listOf(text)
    }

    // Try to split on slash or space
    val words = text.split("/", " ")
    val lines = mutableListOf<String>()
    var currentLine = ""

    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (testLine.length <= maxCharsPerLine) {
            currentLine = testLine
        } else {
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine)
            }
            currentLine = word
        }
    }

    if (currentLine.isNotEmpty()) {
        lines.add(currentLine)
    }

    return lines.ifEmpty { listOf(text) }
}

private fun DrawScope.drawPointer(center: Offset, radius: Float) {
    val pointerHeight = radius * 0.15f
    val pointerWidth = radius * 0.1f
    val pointerY = center.y - radius - 10f

    val path = Path().apply {
        moveTo(center.x, pointerY)
        lineTo(center.x - pointerWidth, pointerY + pointerHeight)
        lineTo(center.x + pointerWidth, pointerY + pointerHeight)
        close()
    }

    drawPath(
        path = path,
        color = Color.Red
    )

    drawPath(
        path = path,
        color = Color.White,
        style = Stroke(width = 2f)
    )
}
