package com.powidev.coffeshop.Helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.powidev.coffeshop.Domain.OrderDetailModel

class OrderDetailDBHelper(context: Context) : SQLiteOpenHelper(
    context, "orderDetail.db",
    null, 2
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE orderDetail (
            title TEXT,
            price REAL,
            numberInCart INTEGER,
            orderId INTEGER
        )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS orderDetail")
        onCreate(db)
    }

    fun insertOrder(order: OrderDetailModel): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", order.title)
            put("price", order.price)
            put("numberInCart", order.numberInCart)
            put("orderId", order.orderId)
        }
        return db.insert("orderDetail", null, values)
    }

    fun getOrders(id: Int): List<OrderDetailModel> {
        val list = mutableListOf<OrderDetailModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM orderDetail WHERE orderId = $id", null)
        while (cursor.moveToNext()) {
            val orders = OrderDetailModel(
                title = cursor.getString(0),
                price = cursor.getDouble(1),
                numberInCart = cursor.getInt(2),
                orderId = cursor.getInt(3)
            )
            list.add(orders)
        }
        cursor.close()
        return list
    }
}