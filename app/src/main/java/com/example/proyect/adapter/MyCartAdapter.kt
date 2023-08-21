package com.example.proyect.adapter

import android.app.AlertDialog
import android.content.Context
import com.google.firebase.database.FirebaseDatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


import com.example.proyect.R
import com.example.proyect.eventbus.UpdateCartEvent
import com.example.proyect.model.CartModel

import org.greenrobot.eventbus.EventBus

class MyCartAdapter (
    private val context: Context,
    private val cartModelList:List<CartModel>


        ): RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {







    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var btnMinus:ImageView? = null
        var btnPlus:ImageView? = null
        var imageView:ImageView? = null
        var btnDelete: ImageView?= null
        var txtName:TextView? = null
        var txtPrice:TextView? = null
        var txtQuantity:TextView? = null

        init {
            btnMinus = itemView.findViewById(R.id.btnMinus) as ImageView
            btnPlus = itemView.findViewById(R.id.btnPlus) as ImageView
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            btnDelete = itemView.findViewById(R.id.btnDelete) as ImageView

            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView
            txtQuantity = itemView.findViewById(R.id.txtQuantity) as TextView

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_cart_item,parent,false))
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].imagen)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(cartModelList[position].nombre)
        holder.txtPrice!!.text = StringBuilder("$").append(cartModelList[position].precio)
        holder.txtQuantity!!.text = StringBuilder("").append(cartModelList[position].quantity)

        //Event
        holder.btnMinus!!.setOnClickListener{ minusCartItem(holder,cartModelList[position])}
        holder.btnPlus!!.setOnClickListener{ plusCartItem(holder,cartModelList[position])}
        holder.btnDelete!!.setOnClickListener{
            val dialog = AlertDialog.Builder(context)
                .setTitle("ELIMINAR")
                .setMessage("Realmente quieres eliminar este artículo?")
                .setNegativeButton("CANCELAR"){dialog,_ -> dialog.dismiss()}
                .setPositiveButton("ELIMINAR"){dialog,_ ->

                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance()
                        .getReference("Cart")
                        .child("UNIQUE_USER_ID")
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
                }
                .create()
            dialog.show()

        }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity += 1
        cartModel.totalPrice = cartModel.quantity * cartModel.precio!!.toFloat()
        holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
        updateFirebase(cartModel)
    }

    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if(cartModel.quantity > 1)
        {
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel.quantity * cartModel.precio!!.toFloat()
            holder.txtQuantity!!.text = StringBuilder("").append(cartModel.quantity)
            updateFirebase(cartModel)
        }
    }

    private fun updateFirebase(cartModel: CartModel) {
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .child(cartModel.key!!)
            .setValue(cartModel)
            .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }
}

