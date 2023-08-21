package com.example.proyect.listener

import com.example.proyect.model.DrinkModel


interface IDrinkLoadListener {
    fun onDrinkLoadSuccess(drinkModelList:List<DrinkModel>?)
    fun onDrinkLoadFailed(message:String?)
}