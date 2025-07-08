package com.powidev.coffeshop.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.powidev.coffeshop.Domain.ItemsModel

class FavoriteRepository {
    private val database = FirebaseDatabase.getInstance()

    fun addToFavorites(item: ItemsModel, uid: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val ref = FirebaseDatabase.getInstance().getReference("Favorites").child(uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var maxIndex = -1
                for (child in snapshot.children) {
                    val key = child.key?.toIntOrNull()
                    if (key != null && key > maxIndex) {
                        maxIndex = key
                    }
                }

                val nextIndex = maxIndex + 1
                ref.child(nextIndex.toString()).setValue(item).addOnCompleteListener { task ->
                    result.value = task.isSuccessful
                }
            }

            override fun onCancelled(error: DatabaseError) {
                result.value = false
            }
        })

        return result
    }

    fun loadFavorites(uid: String): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = database.getReference("Favorites").child(uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemsModel>()
                for (child in snapshot.children) {
                    val item = child.getValue(ItemsModel::class.java)
                    item?.let { list.add(it) }
                }
                listData.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavoriteRepository", "Error al cargar favoritos", error.toException())
            }
        })

        return listData
    }

    fun removeFromFavorites(item: ItemsModel, uid: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val ref = FirebaseDatabase.getInstance().getReference("Favorites").child(uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedList = mutableListOf<ItemsModel>()

                // Recorremos y excluimos el item que queremos eliminar
                for (child in snapshot.children) {
                    val currentItem = child.getValue(ItemsModel::class.java)
                    if (currentItem != null && currentItem.title != item.title) {
                        updatedList.add(currentItem)
                    }
                }

                // Borramos toda la lista
                ref.removeValue().addOnCompleteListener { clearTask ->
                    if (clearTask.isSuccessful) {
                        // Reescribimos los ítems con claves 0, 1, 2...
                        val newMap = updatedList.mapIndexed { index, i ->
                            index.toString() to i
                        }.toMap()

                        ref.updateChildren(newMap).addOnCompleteListener { writeTask ->
                            result.value = writeTask.isSuccessful
                        }
                    } else {
                        result.value = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                result.value = false
            }
        })

        return result
    }

    fun isInFavorites(item: ItemsModel, uid: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val ref = database.getReference("Favorites").child(uid)

        ref.get().addOnSuccessListener { snapshot ->
            val exists = snapshot.children.any {
                val favoriteItem = it.getValue(ItemsModel::class.java)
                favoriteItem?.title == item.title
            }
            result.value = exists
        }.addOnFailureListener {
            Log.e("FavoriteRepository", "Error al verificar favorito", it)
            result.value = false
        }

        return result
    }
}