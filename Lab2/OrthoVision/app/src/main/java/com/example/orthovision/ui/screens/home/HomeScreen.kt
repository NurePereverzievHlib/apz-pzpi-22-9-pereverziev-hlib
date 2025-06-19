package com.example.orthovision.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orthovision.ui.theme.ClinicCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onClinicClick: (clinicId: String) -> Unit
) {
    val clinics = viewModel.filteredClinics
    val searchQuery = viewModel.searchQuery

    LaunchedEffect(Unit) {
        viewModel.loadClinics()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Оберіть клініку для запису",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color.Black
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            label = { Text("Пошук клініки") },
            placeholder = { Text("Введіть назву клініки", color = Color.Gray.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.White,
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = Color.Gray,
                cursorColor = MaterialTheme.colors.primary,
                textColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(clinics) { clinic ->
                ClinicCard(
                    clinic = clinic,
                    modifier = Modifier.clickable { onClinicClick(clinic.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
