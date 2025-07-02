package com.powidev.coffeshop.Activity

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.R
import java.util.*

class AddCoffeeActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextExtra: EditText // Campo extra
    private lateinit var ratingBar: RatingBar
    private lateinit var imageViewSelected: ImageView
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonAddCoffee: Button

    private var selectedImageUri: Uri? = null
    private lateinit var categories: List<CategoryModel>

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(imageViewSelected)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_coffee)

        initViews()
        loadCategories()
        setupListeners()

        val backBtn = findViewById<LinearLayout>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextExtra = findViewById(R.id.editTextExtra) // Inicializar el campo extra
        ratingBar = findViewById(R.id.ratingBar)
        imageViewSelected = findViewById(R.id.imageViewSelected)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        buttonAddCoffee = findViewById(R.id.buttonAddCoffee)
    }

    private fun loadCategories() {
        FirebaseDatabase.getInstance().getReference("Category")
            .get()
            .addOnSuccessListener { snapshot ->
                categories = snapshot.children.map {
                    it.getValue(CategoryModel::class.java)!!
                }

                val categoryTitles = categories.map { it.title }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryTitles)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }
    }

    private fun setupListeners() {
        buttonSelectImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        buttonAddCoffee.setOnClickListener {
            if (validateInputs()) {
                uploadImageAndSaveCoffee()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (editTextTitle.text.isBlank() || editTextDescription.text.isBlank() ||
            editTextPrice.text.isBlank() || selectedImageUri == null
        ) {
            Toast.makeText(this, "Completa todos los campos e incluye una imagen", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun uploadImageAndSaveCoffee() {
        val storageRef = FirebaseStorage.getInstance().reference.child("product_images/${UUID.randomUUID()}.jpg")
        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    saveCoffee(imageUrl.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCoffee(imageUrl: String) {
        val database = FirebaseDatabase.getInstance().reference
        val itemsRef = database.child("Items")

        // Obtener todos los IDs existentes
        itemsRef.get().addOnSuccessListener { snapshot ->
            val currentIds = snapshot.children.mapNotNull { it.key?.toIntOrNull() }.sorted()

            // Encuentra el menor ID faltante en la secuencia
            val nextId = generateNextId(currentIds)

            val newItem = ItemsModel(
                id = nextId.toString(),
                title = editTextTitle.text.toString(),
                description = editTextDescription.text.toString(),
                price = editTextPrice.text.toString().toDouble(),
                rating = ratingBar.rating.toDouble(),
                picUrl = listOf(imageUrl),
                categoryId = categories[spinnerCategory.selectedItemPosition].id.toString()
            )

            // Guardar el nuevo elemento con el ID calculado
            itemsRef.child(nextId.toString())
                .setValue(newItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Café agregado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al agregar el café", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener los IDs existentes", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para generar el siguiente ID faltante
    private fun generateNextId(currentIds: List<Int>): Int {
        for (i in currentIds.indices) {
            if (i != currentIds[i]) {
                return i // Retorna el menor número faltante
            }
        }
        return currentIds.size // Si no falta ninguno, retorna el siguiente en la secuencia
    }
}
