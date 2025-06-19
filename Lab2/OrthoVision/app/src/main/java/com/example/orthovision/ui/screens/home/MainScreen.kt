package com.example.orthovision.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.orthovision.R
import com.example.orthovision.ui.screens.home.HomeScreen
import com.example.orthovision.ui.screens.home.HomeViewModel
import com.example.orthovision.data.TokenManager

@Composable
fun MainScreen(
    viewModel: HomeViewModel,
    onClinicClick: (clinicId: String) -> Unit,
    onNavigateToCard: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToGlasses: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userName = TokenManager.getUserName() ?: "Користувач"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                userName = userName,
                onCardClick = {
                    onNavigateToCard()
                    scope.launch { drawerState.close() }
                },
                onRecordsClick = {
                    onNavigateToRecords()
                    scope.launch { drawerState.close() }
                },
                onGlassesClick = {
                    onNavigateToGlasses()
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("OrthoVision") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Меню")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(Color.White)
                    .fillMaxSize()
            ) {
                HomeScreen(
                    viewModel = viewModel,
                    onClinicClick = onClinicClick
                )
            }
        }
    }
}

@Composable
fun DrawerContent(
    userName: String,
    onCardClick: () -> Unit,
    onRecordsClick: () -> Unit,
    onGlassesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 280.dp)
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    )
    {
        // Аватарка
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопки навігації — стилізуємо як кнопки, щоб виглядали чітко
        DrawerMenuItem(text = "🗂️ Моя карта пацієнта", onClick = onCardClick)
        DrawerMenuItem(text = "📝 Мої записи", onClick = onRecordsClick)
        DrawerMenuItem(text = "👓 Мої окуляри", onClick = onGlassesClick)
    }
}


@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = Color.Gray.copy(alpha = 0.15f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

