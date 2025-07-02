package com.powidev.coffeshop.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.powidev.coffeshop.Domain.BannerModel
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.Domain.ItemsModel

class MainRepository {
    private val firebaseDatabase= FirebaseDatabase.getInstance()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        val listData = MutableLiveData<MutableList<BannerModel>>()
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BannerModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Repository", "Error al cargar banners", error.toException())
            }
        })
        return listData
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        val listData=MutableLiveData<MutableList<CategoryModel>>()
        val ref=firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list=mutableListOf<CategoryModel>()
                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadPopular():LiveData<MutableList<ItemsModel>>{
        val listData=MutableLiveData<MutableList<ItemsModel>>()
        val ref=firebaseDatabase.getReference("Popular")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list=mutableListOf<ItemsModel>()
                for(childSnapshot in snapshot.children){
                    val item=childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value=list
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadItemsByCategory(categoryId: String): LiveData<MutableList<ItemsModel>> {
        val itemsLiveData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
            .orderByChild("categoryId")
            .equalTo(categoryId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemsModel>()
                snapshot.children.forEach { childSnapshot ->
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let { items.add(it) }
                }
                itemsLiveData.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al cargar ítems", error.toException())
                itemsLiveData.value = mutableListOf()
            }
        })
        return itemsLiveData
    }

    fun createItem(item: ItemsModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        try {
            val itemsRef = firebaseDatabase.getReference("Items")
            val itemId = itemsRef.push().key ?: run {
                result.value = false
                return result
            }

            item.id = itemId
            itemsRef.child(itemId).setValue(item)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Repository", "Item creado con ID: $itemId")
                        result.value = true
                    } else {
                        Log.e("Repository", "Error al crear item", task.exception)
                        result.value = false
                    }
                }
        } catch (e: Exception) {
            Log.e("Repository", "Excepción al crear item", e)
            result.value = false
        }
        return result
    }

    fun updateItem(item: ItemsModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        // Obtén la lista actual de items
        firebaseDatabase.getReference("Items")
            .get()
            .addOnSuccessListener { snapshot ->
                // Convierte el snapshot a una lista de ItemsModel
                val items = snapshot.getValue(object : GenericTypeIndicator<List<ItemsModel>>() {}) ?: emptyList()

                // Encuentra el índice del producto a actualizar por título
                val index = items.indexOfFirst { it.title == item.title }

                if (index != -1) {
                    // Actualiza el producto en el índice específico
                    firebaseDatabase.getReference("Items/$index")
                        .updateChildren(item.getUpdatableFields())
                        .addOnSuccessListener { result.value = true }
                        .addOnFailureListener {
                            Log.e("UpdateItem", "Error al actualizar: ${it.message}")
                            result.value = false
                        }
                } else {
                    Log.e("UpdateItem", "Producto no encontrado")
                    result.value = false
                }
            }
            .addOnFailureListener {
                Log.e("UpdateItem", "Error al obtener los productos: ${it.message}")
                result.value = false
            }

        return result
    }

    fun deleteItemByTitle(title: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val ref = firebaseDatabase.getReference("Items")

        ref.orderByChild("title").equalTo(title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            childSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    result.value = true
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Repository", "Error al eliminar el producto", e)
                                    result.value = false
                                }
                        }
                    } else {
                        Log.e("Repository", "No se encontró el producto con título: $title")
                        result.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Repository", "Error al buscar el producto", error.toException())
                    result.value = false
                }
            })

        return result
    }

    fun addToPopular(item: ItemsModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val ref = FirebaseDatabase.getInstance().getReference("Popular")

        ref.get().addOnSuccessListener { snapshot ->
            val newIndex = snapshot.childrenCount.toInt()
            ref.child(newIndex.toString()).setValue(item)
                .addOnSuccessListener { result.value = true }
                .addOnFailureListener { error ->
                    Log.e("Repository", "Error al agregar a populares", error)
                    result.value = false
                }
        }.addOnFailureListener { error ->
            Log.e("Repository", "Error al obtener índice para populares", error)
            result.value = false
        }
        return result
    }

    fun removeFromPopular(item: ItemsModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val ref = FirebaseDatabase.getInstance().getReference("Popular")

        ref.get().addOnSuccessListener { snapshot ->
            val updatedList = mutableListOf<ItemsModel>()

            // Buscar todos los items excepto el que queremos eliminar
            for (child in snapshot.children) {
                val value = child.getValue(ItemsModel::class.java)
                if (value != null && value.title != item.title) {
                    updatedList.add(value)
                }
            }

            // Borrar todos y reescribir con índices consecutivos
            ref.setValue(null).addOnSuccessListener {
                for ((index, newItem) in updatedList.withIndex()) {
                    ref.child(index.toString()).setValue(newItem)
                }
                result.value = true
            }.addOnFailureListener {
                result.value = false
            }

        }.addOnFailureListener {
            result.value = false
        }

        return result
    }

    fun uploadImage(imageUri: Uri, callback: (String?) -> Unit) {
        val imageName = "product_${System.currentTimeMillis()}.jpg"
        val imageRef = storageRef.child("product_images/$imageName")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Repository", "Error al subir imagen", e)
                callback(null)
            }
    }

}