package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.powidev.coffeshop.Adapter.ManagerCategoryAdapter
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.R

class ManageCategoryActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance().getReference("Category")
    private val categories = mutableListOf<CategoryModel>()
    private lateinit var managerCategoryAdapter: ManagerCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)

        val editTextCategory = findViewById<EditText>(R.id.editTextCategory)
        val buttonAddCategory = findViewById<Button>(R.id.buttonAddCategory)
        val recyclerViewCategories = findViewById<RecyclerView>(R.id.recyclerViewCategories)

        // Configurar RecyclerView
        managerCategoryAdapter = ManagerCategoryAdapter(categories) { deleteCategory(it) }
        recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        recyclerViewCategories.adapter = managerCategoryAdapter

        loadCategories()

        // Botón para agregar categoría
        buttonAddCategory.setOnClickListener {
            val categoryName = editTextCategory.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa un nombre para la categoría.", Toast.LENGTH_SHORT).show()
            } else {
                addCategory(categoryName)
                editTextCategory.text.clear() // Limpia el campo después de agregar
            }
        }
    }

    private fun loadCategories() {
        database.get().addOnSuccessListener { snapshot ->
            categories.clear()
            snapshot.children.mapNotNullTo(categories) { it.getValue(CategoryModel::class.java) }
            managerCategoryAdapter.notifyDataSetChanged()
        }
    }

    private fun addCategory(title: String) {
        val newId = categories.size
        val newCategory = CategoryModel(title,newId)
        categories.add(newCategory)
        database.setValue(categories).addOnSuccessListener {
            Toast.makeText(this, "Categoría agregada exitosamente.", Toast.LENGTH_SHORT).show()
            managerCategoryAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al agregar la categoría.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCategory(category: CategoryModel) {
        categories.remove(category)
        categories.forEachIndexed { index, item -> item.id = index } // Recalcular IDs
        database.setValue(categories).addOnSuccessListener {
            Toast.makeText(this, "Categoría eliminada.", Toast.LENGTH_SHORT).show()
            managerCategoryAdapter.notifyDataSetChanged()
        }
    }
}
