package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.powidev.coffeshop.Domain.OrderDetailModel
import com.powidev.coffeshop.Domain.PaymentType
import com.powidev.coffeshop.Helper.OrderDetailDBHelper
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.OrdersListviewBinding

class OrderDetailsActivity : AppCompatActivity() {
    lateinit var binding: OrdersListviewBinding
    private lateinit var dbHelper: OrderDetailDBHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var orders = mutableListOf<OrderDetailModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrdersListviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = OrderDetailDBHelper(this)
        listView = findViewById(R.id.lvOrders)
        loadList()
    }

    private fun loadList() {
        orders = dbHelper.getOrders(1).toMutableList()

        val perOrder = orders.map {
            """      
              ${getString(R.string.coffee)}: ${it.title}
              ${getString(R.string.price)}: S/${it.price}
              ${getString(R.string.quantity)}: ${it.numberInCart}
              ${getString(R.string.payment_type)}: ${
                when (it.paymentType) {
                    PaymentType.CASH -> getString(R.string.cash)
                    PaymentType.YAPE -> getString(R.string.yape)
                    PaymentType.PAYPAL -> getString(R.string.paypal)
                    PaymentType.CREDIT_CARD -> getString(R.string.credit_debit_card)
                }
            }
            """.trimIndent()
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, perOrder)
        listView.adapter = adapter
    }
}