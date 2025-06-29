package com.powidev.coffeshop.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.R
import com.powidev.coffeshop.Repository.MainRepository
import com.powidev.coffeshop.ViewModel.DetailAdminViewModel
import com.powidev.coffeshop.ViewModelFactory.ViewModelFactory

class DetailAdminActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailAdminViewModel
    private lateinit var productImage: ImageView
    private lateinit var titleEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var changeImageFab: FloatingActionButton

    private var currentItem: ItemsModel? = null
    private var selectedImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this)
                .load(it)
                .into(productImage)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailAdmin)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupViewModel()
        loadItemData()
        setupButtonListeners()
    }

    private fun setupViewModel() {
        val repository = MainRepository()
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DetailAdminViewModel::class.java]
    }

    private fun initViews() {
        productImage = findViewById(R.id.picMain)
        titleEditText = findViewById(R.id.titleTxt)
        priceEditText = findViewById(R.id.priceTxt)
        descriptionEditText = findViewById(R.id.descriptionTxt)
        saveButton = findViewById(R.id.button_save)
        editButton = findViewById(R.id.button_edit)
        deleteButton = findViewById(R.id.button_delete)
        changeImageFab = findViewById(R.id.fab_change_image)
    }

    private fun loadItemData() {
        currentItem = intent.getSerializableExtra("object") as? ItemsModel

        if (currentItem == null) {
            Toast.makeText(this, "Error: No se recibió el producto", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentItem?.let { item ->
            titleEditText.setText(item.title)
            priceEditText.setText(item.price.toString())
            descriptionEditText.setText(item.description)

            if (item.picUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(item.picUrl[0])
                    .into(productImage)
            }
        }

        updateUIState()
    }

    private fun setupButtonListeners() {
        changeImageFab.setOnClickListener {
            imagePicker.launch("image/*")
        }

        saveButton.setOnClickListener {
            if (validateFields()) {
                currentItem?.let { updateItem() } ?: createItem()
            }
        }

        editButton.setOnClickListener {
            enableEditing(true)
            Toast.makeText(this, "Modo edición activado", Toast.LENGTH_SHORT).show()
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun validateFields(): Boolean {
        val price = priceEditText.text.toString().toDoubleOrNull()

        return when {
            titleEditText.text.toString().isBlank() -> {
                titleEditText.error = "Ingrese un título válido"
                false
            }
            price == null || price < 0 -> {
                priceEditText.error = "Ingrese un precio válido"
                false
            }
            descriptionEditText.text.toString().isBlank() -> {
                descriptionEditText.error = "Ingrese una descripción"
                false
            }
            else -> true
        }
    }

    private fun createItem() {
        val newItem = ItemsModel(
            title = titleEditText.text.toString(),
            price = priceEditText.text.toString().toDouble(),
            description = descriptionEditText.text.toString(),
            picUrl = selectedImageUri?.let { listOf(it.toString()) } ?: emptyList(),
            categoryId = "1"
        )

        viewModel.createItem(newItem).observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al crear producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateItem() {
        currentItem?.let { originalItem ->
            val updatedItem = originalItem.copy(
                title = titleEditText.text.toString(),
                price = priceEditText.text.toString().toDouble(),
                description = descriptionEditText.text.toString(),
                picUrl = selectedImageUri?.let { listOf(it.toString()) } ?: originalItem.picUrl
            )

            viewModel.updateItem(updatedItem).observe(this) { success ->
                if (success) {
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    currentItem = updatedItem
                    enableEditing(false)
                } else {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de eliminar este producto?")
            .setPositiveButton("Eliminar") { _, _ ->
                currentItem?.id?.let { deleteItem(it) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteItem(itemId: String) {
        viewModel.deleteItem(itemId).observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enableEditing(enable: Boolean) {
        titleEditText.isEnabled = enable
        priceEditText.isEnabled = enable
        descriptionEditText.isEnabled = enable
        changeImageFab.isEnabled = enable
        updateUIState()
    }

    private fun updateUIState() {
        val isEditing = titleEditText.isEnabled
        val isNewItem = currentItem == null

        saveButton.visibility = if (isEditing || isNewItem) View.VISIBLE else View.GONE
        editButton.visibility = if (!isNewItem && !isEditing) View.VISIBLE else View.GONE
        deleteButton.visibility = if (!isNewItem) View.VISIBLE else View.GONE
    }
}