@file:OptIn(ExperimentalAnimationApi::class)

package com.example.Linageisp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import com.example.Linageisp.R
import com.example.Linageisp.ui.components.*
import com.example.Linageisp.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedTestScreen(
    modifier: Modifier = Modifier
) {
    var testState by remember { mutableStateOf(SpeedTestState.IDLE) }
    var currentResult by remember { mutableStateOf(SpeedTestResult()) }
    var testHistory by remember { mutableStateOf(generateMockHistory()) }
    var showHistory by remember { mutableStateOf(false) }
    
    LaunchedEffect(testState) {
        if (testState == SpeedTestState.TESTING) {
            // Simulate download test
            for (i in 1..50) {
                currentResult = currentResult.copy(download = i * 2f)
                delay(100)
            }
            
            // Simulate upload test
            for (i in 1..30) {
                currentResult = currentResult.copy(upload = i * 1.5f)
                delay(80)
            }
            
            // Set ping
            currentResult = currentResult.copy(ping = (15..50).random())
            testState = SpeedTestState.COMPLETED
            
            // Add to history
            testHistory = listOf(currentResult) + testHistory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LinageBackground,
                        Color.White
                    )
                )
            )
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.speed_test_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = LinageOrange
                    )
                )
            },
            actions = {
                IconButton(
                    onClick = { showHistory = !showHistory }
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = stringResource(R.string.speed_test_history),
                        tint = LinageOrange
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        if (showHistory) {
            SpeedTestHistoryContent(
                history = testHistory,
                onBack = { showHistory = false }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SpeedTestMainContent(
                    testState = testState,
                    currentResult = currentResult,
                    onStartTest = {
                        testState = SpeedTestState.TESTING
                        currentResult = SpeedTestResult()
                    },
                    onShareResult = {
                        // Share functionality would go here
                    }
                )
            }
        }
    }
}

@Composable
private fun SpeedTestMainContent(
    testState: SpeedTestState,
    currentResult: SpeedTestResult,
    onStartTest: () -> Unit,
    onShareResult: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        // Speed meter
        if (testState == SpeedTestState.TESTING) {
            LoadingSpeedMeter()
        } else {
            SpeedMeter(
                currentSpeed = if (testState == SpeedTestState.COMPLETED) 
                    currentResult.download else 0f,
                state = testState
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Test results
        AnimatedVisibility(
            visible = testState == SpeedTestState.COMPLETED,
            enter = fadeIn() + slideInVertically()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                SpeedResultCard(
                    title = stringResource(R.string.speed_test_download),
                    value = "${currentResult.download.toInt()}",
                    unit = stringResource(R.string.speed_test_mbps),
                    icon = Icons.Default.Download
                )
                
                SpeedResultCard(
                    title = stringResource(R.string.speed_test_upload),
                    value = "${currentResult.upload.toInt()}",
                    unit = stringResource(R.string.speed_test_mbps),
                    icon = Icons.Default.Upload
                )
                
                SpeedResultCard(
                    title = stringResource(R.string.speed_test_ping),
                    value = "${currentResult.ping}",
                    unit = stringResource(R.string.speed_test_ms),
                    icon = Icons.Default.Speed
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start/Restart test button
            Button(
                onClick = onStartTest,
                enabled = testState != SpeedTestState.TESTING,
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LinageOrange
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = if (testState == SpeedTestState.IDLE) 
                            stringResource(R.string.speed_test_start) 
                        else stringResource(R.string.speed_test_start),
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Share button
            if (testState == SpeedTestState.COMPLETED) {
                OutlinedButton(
                    onClick = onShareResult,
                    modifier = Modifier
                        .height(56.dp)
                        .weight(0.5f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LinageOrange
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        Brush.horizontalGradient(
                            colors = listOf(LinageOrange, LinageOrangeLight)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.speed_test_share)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedResultCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LinageOrange,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrange
                )
            )
            
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LinageGray
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = LinageGray
                )
            )
        }
    }
}

@Composable
private fun SpeedTestHistoryContent(
    history: List<SpeedTestResult>,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = LinageOrange
                )
            }
            
            Text(
                text = stringResource(R.string.speed_test_history),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = LinageOrange
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history.take(10)) { result ->
                HistoryItemCard(result = result)
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    result: SpeedTestResult
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Test realizado",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "Hace ${(1..24).random()} horas",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LinageGray
                    )
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = LinageOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${result.download.toInt()} Mbps",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        tint = LinageOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${result.upload.toInt()} Mbps",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = LinageOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${result.ping} ms",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private fun generateMockHistory(): List<SpeedTestResult> {
    return listOf(
        SpeedTestResult(download = 95f, upload = 45f, ping = 23),
        SpeedTestResult(download = 87f, upload = 41f, ping = 28),
        SpeedTestResult(download = 92f, upload = 43f, ping = 25),
        SpeedTestResult(download = 89f, upload = 39f, ping = 31),
        SpeedTestResult(download = 94f, upload = 46f, ping = 22)
    )
}