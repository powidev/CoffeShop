package com.powidev.coffeshop.Helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.powidev.coffeshop.Domain.OrderModel

class OrderDBHelper(context: Context) : SQLiteOpenHelper(
    context, "orders.db",
    null, 3
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE orders (
            orderId INTEGER PRIMARY KEY AUTOINCREMENT,
            totalPrice REAL,
            date TEXT
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
            put("totalPrice", order.totalPrice)
            put("date", order.date)
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
                totalPrice = cursor.getDouble(1),
                date = cursor.getString(2),
            )
            list.add(orders)
        }
        cursor.close()
        return list
    }
}
