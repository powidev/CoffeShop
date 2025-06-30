package com.powidev.coffeshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.powidev.coffeshop.R

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Navegar a la gestión de productos
        val cardProducto = findViewById<CardView>(R.id.cardProducto)
        cardProducto.setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        // Navegar a la gestión de categorías
        val cardCategory: CardView = findViewById(R.id.cardCategory)
        cardCategory.setOnClickListener {
            startActivity(Intent(this, ManageCategoryActivity::class.java))
        }

        // Agregar nuevo café
        val cardAddCoffee: CardView = findViewById(R.id.cardAddCoffee)
        cardAddCoffee.setOnClickListener {
            startActivity(Intent(this, AddCoffeeActivity::class.java))
        }

        // Ir a la Vista Cliente
        val cardGoToClient = findViewById<CardView>(R.id.cardGoToClientView)
        cardGoToClient.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("isAdmin", true)
            startActivity(intent)
        }
    }
}
