package com.powidev.coffeshop.Activity.payment

import android.content.Context
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType
import com.powidev.coffeshop.Helper.ManagmentCart
import com.powidev.coffeshop.Helper.OrdersDBHelper

class CashPayment(val context: Context) {
    private lateinit var dbHelper: OrdersDBHelper
    lateinit var managementCart: ManagmentCart

    fun saveOrder() {
        dbHelper = OrdersDBHelper(context)
        managementCart = ManagmentCart(context)
        managementCart.getListCart().forEach {
            val order = OrderModel(it.title, it.price, it.numberInCart, PaymentType.CASH, 1)
            dbHelper.insertOrder(order)
        }
        managementCart.clearListCart()
    }
}