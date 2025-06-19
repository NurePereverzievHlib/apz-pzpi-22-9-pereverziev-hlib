package com.example.orthovision.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(viewModel: LoginViewModel, onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    val loginResult by viewModel.loginResult.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginResult) {
        if (loginResult == true) {
            onLoginSuccess()
            Toast.makeText(context, "Вітаємо Ви авторизовані!", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Вітаємо в OrthoVision", style = MaterialTheme.typography.h5)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Увійти")
                }

                // Клікабельний текст для переходу на реєстрацію
                Text(
                    text = "Немає профілю? Зареєструватись",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onRegisterClick() },
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body2,
                    textDecoration = TextDecoration.Underline
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (loginResult == false) {
                    Text(
                        text = "Не вдалось увійти. Перевірте логін і пароль.",
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}
