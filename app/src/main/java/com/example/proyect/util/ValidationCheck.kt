package com.example.proyect.util

import android.util.Patterns

fun validateEmail(email:String): RegisterValidation{
    if(email.isEmpty())
        return RegisterValidation.Failed("Correo electrónico no puede estar vacio")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Formato incorrecto del Correo electrónico")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation{
    if(password.isEmpty())
        return RegisterValidation.Failed("La Contraseña no puede estar vacia")
    if(password.length < 6)
        return RegisterValidation.Failed("La contraseña debe de tener 6 caracteres minimo")
    return RegisterValidation.Success
}