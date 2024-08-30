package com.example.colorapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.colorapp.R
import com.example.colorapp.data.ColorEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun ColorScreen(
    viewModel: ColorViewModel = hiltViewModel()
) {
    val colorState by viewModel.colors.collectAsState()
    val pendingColors by viewModel.pendingColors.collectAsState()
    var syncedColorsList by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var unsyncedColorsList by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    LaunchedEffect(colorState) {
        when (colorState) {
            is ColorState.Success -> {
                syncedColorsList = (colorState as ColorState.Success).allColors
                    .filter { it.synced }
                    .map { it.color to it.time }

                unsyncedColorsList = (colorState as ColorState.Success).unsyncedColors
                    .map { it.color to it.time }
            }
            else -> {
            }
        }
    }

    Scaffold(
        topBar = {
            AppBar(
                onSyncClick = {
                    viewModel.syncColors()
                },
                pendingSyncColorsCount = pendingColors
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newColor = generateRandomColor()
                    val currentDate = getCurrentDate()
                    viewModel.addColor(ColorEntity(0, newColor, currentDate))
                },
                containerColor = Color(0xFFB6B9FF)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .height(34.dp)
                        .width(120.dp)
                        .background(color = Color(0xFFB6B9FF), shape = RoundedCornerShape(20.dp))
                ) {
                    Text(
                        text = "Add Color",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        modifier =Modifier.padding(start = 8.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add Color",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .padding(2.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(syncedColorsList) { color ->
                ColorCard(color.first, color.second)
            }
        }
    }
}

@Composable
fun ColorCard(colorHex: String, createdDate: String) {
    val parsedColor = try {
        if (colorHex.isNotEmpty() && colorHex.length >= 7 && colorHex[0] == '#') {
            Color(android.graphics.Color.parseColor(colorHex))
        } else {
            Color.Black
        }
    } catch (e: IllegalArgumentException) {
        Color.Black
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = parsedColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column {
                Text(
                    text = colorHex,
                    color = Color.White,
                    fontSize = 18.sp,
                )
                Box (
                        modifier = Modifier
                            .width(90.dp)
                            .height(1.dp)
                            .background(Color.White)
                        )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.BottomEnd)
                    .padding(2.dp)
            ) {
                Column {
                    Text(
                        text = "Created at",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Text(
                        text = createdDate,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onSyncClick: () -> Unit,
    pendingSyncColorsCount: Int
) {
    TopAppBar(
        title = {
            Text(
                text = "Color App",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(color = Color(0xFFB6B9FF), shape = RoundedCornerShape(20.dp))
                    .width(70.dp),

            ) {
                Text(
                    text = pendingSyncColorsCount.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = { onSyncClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_sync_24),
                        contentDescription = "Sync",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary // Set background to primary color
                )
    )
}

fun generateRandomColor(): String {
    val color = Random.nextInt(0xFFFFFF)
    return String.format("#%06X", color)
}

fun getCurrentDate(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date())
}
