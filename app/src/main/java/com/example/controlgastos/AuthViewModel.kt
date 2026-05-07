package com.example.controlgastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

data class ValidationResult(
    val isValid: Boolean,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser get() = auth.currentUser

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El correo es obligatorio"
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailRegex.matches(email.trim())) return "Ingresa un correo válido"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña es obligatoria"
        if (password.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        if (!password.any { it.isDigit() }) return "La contraseña debe contener al menos un número"
        if (!password.any { it.isLetter() }) return "La contraseña debe contener al menos una letra"
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isBlank()) return "Confirma tu contraseña"
        if (password != confirm) return "Las contraseñas no coinciden"
        return null
    }

    fun validateLoginFields(email: String, password: String): ValidationResult {
        val emailError = validateEmail(email)
        val passwordError = if (password.isBlank()) "La contraseña es obligatoria" else null
        return ValidationResult(
            isValid = emailError == null && passwordError == null,
            emailError = emailError,
            passwordError = passwordError
        )
    }

    fun validateRegisterFields(email: String, password: String, confirm: String): ValidationResult {
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val confirmError = validateConfirmPassword(password, confirm)
        return ValidationResult(
            isValid = emailError == null && passwordError == null && confirmError == null,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmError
        )
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                _authState.value = AuthState.Success
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error("Correo o contraseña incorrectos")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                _authState.value = AuthState.Success
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authState.value = AuthState.Error("Contraseña muy débil. Usa al menos 6 caracteres")
            } catch (e: FirebaseAuthUserCollisionException) {
                _authState.value = AuthState.Error("Ya existe una cuenta con este correo")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error("El formato del correo no es válido")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error al registrarse: ${e.message}")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
