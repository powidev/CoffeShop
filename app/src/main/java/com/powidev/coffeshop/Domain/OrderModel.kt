package com.powidev.coffeshop.Domain

data class OrderModel(
    val orderID: Int = 0,
    var totalPrice: Double,
    val date: String
)
