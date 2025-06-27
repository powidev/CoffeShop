package com.powidev.coffeshop.Helper

import android.content.Context
import android.widget.Toast
import com.powidev.coffeshop.Helper.ChangeNumberItemsListener
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.Helper.TinyDB
import kotlin.math.roundToInt


class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItems(item: ItemsModel) {
        var listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title }
        val index = listItem.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listItems[position].numberInCart == 1) {
            listItems.removeAt(position)
        } else {
            listItems[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }
    fun removeItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {

        listItems.removeAt(position)

        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    fun plusItem(listItems: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItems[position].numberInCart++
        tinyDB.putListObject("CartList", listItems)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
        }
        return fee
    }
    
    fun getCalculatedFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        var taxAmount = 0.02

        for (item in listItem) {
            fee += item.price * item.numberInCart
        }

        val taxedFee = (fee * taxAmount * 100.0).roundToInt() / 100.0
        return ((fee + taxedFee + 15) * 100.0).roundToInt() / 100.0
    }

    fun clearListCart() {
        tinyDB.remove("CartList")
    }
}