package com.example.proyect.listener

import com.example.proyect.model.CartModel


interface ICartLoadListener {
    fun onLoadCartSuccess(cartModelList: List<CartModel>)
    fun onLoadCartFailed(message:String?)
}