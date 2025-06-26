package com.powidev.coffeshop.Domain

data class OrderDetailModel(
    val title: String,
    val price: Double,
    var numberInCart: Int,
    val paymentType: PaymentType,
    val orderId : Int
)
