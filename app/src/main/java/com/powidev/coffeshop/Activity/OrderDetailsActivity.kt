package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.powidev.coffeshop.Domain.OrderDetailModel
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType
import com.powidev.coffeshop.Helper.OrderDBHelper
import com.powidev.coffeshop.Helper.OrderDetailDBHelper
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.OrdersListviewBinding
import kotlin.math.roundToInt

class OrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: OrdersListviewBinding
    private lateinit var orderDetailDBHelper: OrderDetailDBHelper
    private lateinit var orderDBHelper: OrderDBHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var order: OrderModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OrdersListviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderDetailDBHelper = OrderDetailDBHelper(this)
        orderDBHelper = OrderDBHelper(this)
        listView = findViewById(R.id.lvOrders)
        val orderID = intent.getIntExtra("orderID", -1)

        loadProductsListView(orderID)
        setOrderDetails(orderID)
        setVariable()
    }

    private fun loadProductsListView(orderID: Int) {
        var details = mutableListOf<OrderDetailModel>()
        details = orderDetailDBHelper.getOrders(orderID).toMutableList()

        val perOrder = details.map {
            """      
              ${getString(R.string.coffee)}: ${it.title}
              ${getString(R.string.price)}: S/${it.price}
              ${getString(R.string.quantity)}: ${it.numberInCart}
            """.trimIndent()
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, perOrder)
        listView.adapter = adapter
    }

    private fun setOrderDetails(orderID: Int) {
        order = orderDBHelper.getOrderById(orderID)

        val tax : Double = ((order.grossPrice * 0.02) * 100.0).roundToInt() / 100.0

        binding.textOrderId.text = order.orderID.toString()

        binding.textPaymentType.text = when (order.paymentType) {
            PaymentType.CASH -> getString(R.string.cash)
            PaymentType.YAPE -> getString(R.string.yape)
            PaymentType.PAYPAL -> getString(R.string.paypal)
            PaymentType.CREDIT_CARD -> getString(R.string.credit_debit_card)
        }

        binding.textOrderDate.text = order.date.toString()
        binding.textGrossPayment.text = "S/" + order.grossPrice.toString()
        binding.textTaxPayment.text = "S/" + tax.toString()
        binding.textDeliveryFee.text = "S/15"
        binding.textTotalPayment.text = "S/" + order.totalPrice.toString()
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }
}