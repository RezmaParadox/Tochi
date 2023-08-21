package com.example.proyect.util

sealed class RegisterValidation(){
    object Success: RegisterValidation()
    data class Failed(val message: String):RegisterValidation()
}

data class RegisterFieldSate(
    val email: RegisterValidation,
    val password: RegisterValidation
)
