package com.powidev.coffeshop.Helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType

class OrderDBHelper(context: Context) : SQLiteOpenHelper(
    context, "orders.db",
    null, 5
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE orders (
            orderId INTEGER PRIMARY KEY AUTOINCREMENT,
            grossPrice REAL,
            totalPrice REAL,
            date TEXT,
            paymentType TEXT
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
            put("grossPrice", order.grossPrice)
            put("totalPrice", order.totalPrice)
            put("date", order.date)
            put("paymentType", order.paymentType.toString())
        }
        return db.insert("orders", null, values)
    }

    fun getOrders(): List<OrderModel> {
        val list = mutableListOf<OrderModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM orders", null)
        while (cursor.moveToNext()) {
            val orders = OrderModel(
                orderID = cursor.getInt(0),
                grossPrice = cursor.getDouble(1),
                totalPrice = cursor.getDouble(2),
                date = cursor.getString(3),
                paymentType = PaymentType.valueOf(cursor.getString(4)),
            )
            list.add(orders)
        }
        cursor.close()
        return list
    }


    fun getOrderById(id: Int): OrderModel {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM orders WHERE orderId = $id", null)
        cursor.moveToNext()
        val order = OrderModel(
            orderID = cursor.getInt(0),
            grossPrice = cursor.getDouble(1),
            totalPrice = cursor.getDouble(2),
            date = cursor.getString(3),
            paymentType = PaymentType.valueOf(cursor.getString(4)),
        )
        cursor.close()
        return order
    }
}
