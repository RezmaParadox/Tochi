
package com.example.proyect.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyect.eventbus.UpdateCartEvent

import com.example.proyect.listener.IRecyclerClickListener


import com.example.proyect.R
import com.example.proyect.listener.ICartLoadListener
import com.example.proyect.model.CartModel
import com.example.proyect.model.DrinkModel


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus



class MyDrinkAdapter (
    private val context: Context,
    private val list:List<DrinkModel>,
    private val cartListener: ICartLoadListener


): RecyclerView.Adapter<MyDrinkAdapter.MyDrinkViewHolder>(){
    class MyDrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView: ImageView?=null
        var txtName: TextView?=null
        var txtPrice: TextView?=null

        private var clickListener: IRecyclerClickListener? = null

        fun setClickListener(clickListener: IRecyclerClickListener)
        {
            this.clickListener = clickListener;
        }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView;
            txtName = itemView.findViewById(R.id.txtName) as TextView;
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView;

            itemView.setOnClickListener(this)


        }

        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v,adapterPosition)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyDrinkViewHolder {
        return MyDrinkViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_drink_item,parent, false))
    }

    override fun onBindViewHolder(holder: MyDrinkViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].imagen)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].nombre)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].precio)


        holder.setClickListener(object: IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }

        })



    }

    private fun addToCart(drinkModel: DrinkModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(drinkModel.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())//if item alreaddy in cart, just update
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String,Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity+1
                        updateData["quantity"] = cartModel!!.quantity;
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.precio!!.toFloat()

                        userCart.child(drinkModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Artículo agregado al carrito")

                            }
                            .addOnFailureListener{ e-> cartListener.onLoadCartFailed(e.message)}


                    } else{ //if item not in cart, add new
                        val cartModel = CartModel()
                        cartModel.key = drinkModel.key
                        cartModel.nombre = drinkModel.nombre
                        cartModel.imagen = drinkModel.imagen
                        cartModel.precio = drinkModel.precio
                        cartModel.quantity = 1
                        cartModel.totalPrice = drinkModel.precio!!.toFloat()

                        userCart.child(drinkModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Success add to cart")
                            }
                            .addOnFailureListener{ e-> cartListener.onLoadCartFailed(e.message)}

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }


            })


    }

    override fun getItemCount(): Int {
        return list.size
    }
}
