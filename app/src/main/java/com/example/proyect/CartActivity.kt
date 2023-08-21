package com.example.proyect


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.example.proyect.Config.Config


import com.example.proyect.listener.ICartLoadListener



import com.example.proyect.R
import com.example.proyect.activities.ProductsActivity
import com.example.proyect.adapter.MyCartAdapter
import com.example.proyect.eventbus.UpdateCartEvent
import com.example.proyect.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paypal.android.sdk.payments.*
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.mainLayout
import kotlinx.android.synthetic.main.activity_main.*


import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import java.math.BigDecimal

class CartActivity : AppCompatActivity(), ICartLoadListener {



    //inicio métodod de pago
    var btnPayNow: Button? = null
    var edtAmount: EditText? = null
    var amount = ""


    override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }


    //fin metodo de pago







    var cartLoadListener: ICartLoadListener?=null


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateCartEvent(event: UpdateCartEvent)
    {
        loadCartFromFirebase()
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        init()
        loadCartFromFirebase()




        //inicio pago




        //start Paypal service
        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)
        btnPayNow = findViewById<View>(R.id.btnPayNow) as Button

        btnPayNow!!.setOnClickListener { processPayment() }
        //fin pago




    }




    //inicio pago

    private fun processPayment() {


        amount = txtTotal!!.text.toString()
        val payPalPayment = PayPalPayment(
            BigDecimal(amount), "USD",
            "Pagos TOCHI", PayPalPayment.PAYMENT_INTENT_SALE
        )

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment)
            startActivityForResult(intent, PAYPAL_REQUEST_CODE)




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val confirmation =
                    data!!.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirmation != null) {
                    try {
                        val paymentDetails = confirmation.toJSONObject().toString(4)
                        startActivity(
                            Intent(this, PaymentDetails::class.java)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", amount)
                        ) //minuto 13:55
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } else if (resultCode == RESULT_CANCELED) Toast.makeText(
                this,
                "Cancel",
                Toast.LENGTH_SHORT
            ).show()
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) Toast.makeText(
            this,
            "Invalid",
            Toast.LENGTH_SHORT
        ).show()
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val PAYPAL_REQUEST_CODE = 7171
        private val config = PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID)
    }


    //fin pago












    private fun loadCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children)
                    {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }

            })
    }

    private fun init(){
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnBack!!.setOnClickListener{finish()}
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum = 0.0
        for(cartModel in cartModelList!!){
            sum+= cartModel!!.totalPrice
        }
        txtTotal.text = StringBuilder().append(sum)
        val  adapter = MyCartAdapter(this, cartModelList)
        recycler_cart!!.adapter = adapter
    }






    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()

    }


}





