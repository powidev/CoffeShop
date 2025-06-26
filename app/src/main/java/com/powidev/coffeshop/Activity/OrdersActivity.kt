package com.powidev.coffeshop.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Adapter.OrdersAdapter
import com.powidev.coffeshop.Helper.OrderDBHelper
import com.powidev.coffeshop.databinding.ActivityOrderBinding

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var orderDBHelper: OrderDBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(binding.root)
        enableEdgeToEdge()

        loadPlacedOrders()
    }

    private fun loadPlacedOrders() {
        orderDBHelper = OrderDBHelper(this)
        val listOrders = orderDBHelper.getOrders()

        recyclerView = binding.recyclerOrders
        recyclerView.layoutManager = LinearLayoutManager(this)
        ordersAdapter = OrdersAdapter(listOrders)
        recyclerView.adapter = ordersAdapter
    }
}