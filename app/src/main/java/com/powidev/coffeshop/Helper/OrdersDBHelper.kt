package com.powidev.coffeshop.Helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType

class OrdersDBHelper(context: Context) : SQLiteOpenHelper(
    context, "orders.db",
    null, 2
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE orders (
            title TEXT,
            price REAL,
            numberInCart INTEGER,
            paymentType TEXT,
            orderId INTEGER
        )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS orders")
        onCreate(db)
    }

    fun insertOrder(order: OrderModel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", order.title)
            put("price", order.price)
            put("numberInCart", order.numberInCart)
            put("paymentType", order.paymentType.toString())
            put("orderId", order.orderId)
        }
        return db.insert("orders", null, values)
    }

    fun getOrders(id: Int): List<OrderModel> {
        val list = mutableListOf<OrderModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM orders WHERE orderId = $id", null)
        while (cursor.moveToNext()) {
            val orders = OrderModel(
                title = cursor.getString(0),
                price = cursor.getDouble(1),
                numberInCart = cursor.getInt(2),
                paymentType = PaymentType.valueOf(cursor.getString(3)),
                orderId = cursor.getInt(4)
            )
            list.add(orders)
        }
        cursor.close()
        return list
    }
}