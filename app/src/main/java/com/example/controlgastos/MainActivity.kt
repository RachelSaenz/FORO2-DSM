package com.example.controlgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.controlgastos.ui.theme.ControlGastosPersonalesTheme

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Home : Screen()
    object Gastos : Screen()
}

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ControlGastosPersonalesTheme {
                // Determinar pantalla inicial según sesión activa
                var currentScreen by remember {
                    mutableStateOf<Screen>(
                        if (authViewModel.currentUser != null) Screen.Home else Screen.Login
                    )
                }

                when (currentScreen) {
                    is Screen.Login -> LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToRegister = { currentScreen = Screen.Register },
                        onLoginSuccess = { currentScreen = Screen.Home }
                    )

                    is Screen.Register -> RegisterScreen(
                        viewModel = authViewModel,
                        onNavigateToLogin = { currentScreen = Screen.Login },
                        onRegisterSuccess = { currentScreen = Screen.Home }
                    )

                    is Screen.Home -> HomeScreen(
                        viewModel = authViewModel,
                        onLogout = { currentScreen = Screen.Login },
                        onNavigateToGastos = { currentScreen = Screen.Gastos }
                    )

                    is Screen.Gastos -> GastosScreen(
                        onBackToHome = { currentScreen = Screen.Home }
                    )
                }
            }
        }
    }
}