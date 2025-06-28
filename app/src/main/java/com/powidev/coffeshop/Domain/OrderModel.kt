package com.powidev.coffeshop.Domain

data class OrderModel(
    val orderID: Int = 0,
    var grossPrice: Double,
    var totalPrice: Double,
    val paymentType: PaymentType,
    val date: String
)
