package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        managerCategoryAdapter = ManagerCategoryAdapter(
            categories,
            onEdit = { showEditDialog(it) },
            onDelete = { deleteCategory(it) }
        )
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

        val backBtn = findViewById<LinearLayout>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
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

    private fun showEditDialog(category: CategoryModel) {
        val editText = EditText(this)
        editText.setText(category.title)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Categoría")
            .setView(editText)
            .setPositiveButton("Actualizar") { _, _ ->
                val newTitle = editText.text.toString().trim()
                if (newTitle.isNotEmpty()) {
                    updateCategory(category, newTitle)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun updateCategory(category: CategoryModel, newTitle: String) {
        val index = categories.indexOfFirst { it.id == category.id }
        if (index != -1) {
            val updatedCategory = category.copy(title = newTitle)
            categories[index] = updatedCategory

            database.setValue(categories).addOnSuccessListener {
                Toast.makeText(this, "Categoría actualizada exitosamente.", Toast.LENGTH_SHORT).show()
                managerCategoryAdapter.notifyItemChanged(index)
            }.addOnFailureListener {
                Toast.makeText(this, "Error al actualizar la categoría.", Toast.LENGTH_SHORT).show()
            }
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
