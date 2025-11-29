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
    val textRadius = radius * 0.7f
    val angleRad = Math.toRadians(angle.toDouble())
    val x = center.x + textRadius * cos(angleRad).toFloat()
    val y = center.y + textRadius * sin(angleRad).toFloat()

    drawContext.canvas.nativeCanvas.apply {
        save()
        translate(x, y)

        // Adjust rotation to keep text readable (not upside down)
        // Text should be rotated perpendicular to the radius
        var textRotation = angle + 90f

        // If text would be upside down (on left side of wheel), flip it
        if (angle > 90f && angle < 270f) {
            textRotation = angle - 90f
        }

        rotate(textRotation)

        val paint = android.graphics.Paint().apply {
            this.color = color.toArgb()
            textSize = 13.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }

        // Draw text with shadow for better visibility
        val shadowPaint = android.graphics.Paint(paint).apply {
            this.color = android.graphics.Color.BLACK
            alpha = 120
        }

        // Draw shadow slightly offset for depth
        drawText(text, 1.5f, 1.5f, shadowPaint)
        drawText(text, 0f, 0f, paint)

        restore()
    }
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
