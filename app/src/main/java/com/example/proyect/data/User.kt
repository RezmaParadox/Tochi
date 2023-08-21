package com.example.proyect.data

data class User(
    val firtsName:String,
    val fatherName: String,
    val motherName: String,
    val email: String,
    val imagePath: String = ""
){
    constructor(): this("","","","","")
}
