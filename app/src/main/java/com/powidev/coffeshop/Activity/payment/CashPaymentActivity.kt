package com.powidev.coffeshop.Activity.payment

import android.content.Context
import android.widget.Toast
import com.powidev.coffeshop.Domain.OrderDetailModel
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType
import com.powidev.coffeshop.Helper.ManagmentCart
import com.powidev.coffeshop.Helper.OrderDBHelper
import com.powidev.coffeshop.Helper.OrderDetailDBHelper
import com.powidev.coffeshop.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CashPaymentActivity(val context: Context) {
    private lateinit var orderDBHelper: OrderDBHelper
    private lateinit var orderDetailDBHelper: OrderDetailDBHelper
    lateinit var managementCart: ManagmentCart

    fun saveOrder() {
        orderDBHelper = OrderDBHelper(context)
        orderDetailDBHelper = OrderDetailDBHelper(context)
        managementCart = ManagmentCart(context)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = format.format(Date())

        val order = OrderModel(date = currentDate, totalPrice = managementCart.getTotalFee())
        val orderId = orderDBHelper.insertOrder(order)

        if(orderId.toInt() == -1) {
            Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            return
        }

        managementCart.getListCart().forEach {
            val order = OrderDetailModel(it.title, it.price, it.numberInCart, PaymentType.CASH, orderId.toInt())
            orderDetailDBHelper.insertOrder(order)
        }
        managementCart.clearListCart()
    }
}