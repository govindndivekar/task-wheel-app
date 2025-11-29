package com.taskwheel.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskwheel.app.R
import com.taskwheel.app.data.WheelSegment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    segments: List<WheelSegment>,
    isSpinning: Boolean,
    selectedSegment: WheelSegment?,
    onSpinClick: () -> Unit,
    onSpinComplete: (WheelSegment) -> Unit,
    onSettingsClick: () -> Unit,
    onDismissResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Spinning Wheel
                SpinningWheel(
                    segments = segments,
                    isSpinning = isSpinning,
                    onSpinComplete = onSpinComplete,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // Spin Button
                Button(
                    onClick = onSpinClick,
                    enabled = !isSpinning && segments.isNotEmpty(),
                    modifier = Modifier
                        .padding(32.dp)
                        .height(64.dp)
                        .fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.spin_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Result Dialog
            if (selectedSegment != null) {
                ResultDialog(
                    segment = selectedSegment,
                    onDismiss = onDismissResult
                )
            }
        }
    }
}

@Composable
fun ResultDialog(
    segment: WheelSegment,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "🎯 Result!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.result_message, segment.label),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = segment.color,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .size(60.dp)
                ) {}
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", fontSize = 16.sp)
            }
        }
    )
}
