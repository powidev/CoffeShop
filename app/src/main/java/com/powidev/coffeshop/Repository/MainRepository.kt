package com.powidev.coffeshop.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.powidev.coffeshop.Domain.BannerModel
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.Domain.ItemsModel

class MainRepository {
    private val firebaseDatabase= FirebaseDatabase.getInstance()

    fun loadBanner():LiveData<MutableList<BannerModel>>{
        val listData=MutableLiveData<MutableList<BannerModel>>()
        val ref=firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list=mutableListOf<BannerModel>()
                    for(childSnapshot in snapshot.children){
                        val item=childSnapshot.getValue(BannerModel::class.java)
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
        val ref = firebaseDatabase.getReference("Items")
        val key = ref.push().key ?: return result.apply { value = false }

        item.id = key
        ref.child(key).setValue(item)
            .addOnSuccessListener { result.value = true }
            .addOnFailureListener {
                Log.e("Repository", "Error creating item", it)
                result.value = false
            }

        return result
    }

    fun updateItem(item: ItemsModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        if (item.id.isNullOrEmpty()) {
            result.value = false
            return result
        }

        firebaseDatabase.getReference("Items").child(item.id!!)
            .setValue(item)
            .addOnSuccessListener { result.value = true }
            .addOnFailureListener {
                Log.e("Repository", "Error updating item", it)
                result.value = false
            }

        return result
    }

    fun deleteItem(itemId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        firebaseDatabase.getReference("Items").child(itemId)
            .removeValue()
            .addOnSuccessListener { result.value = true }
            .addOnFailureListener {
                Log.e("Repository", "Error deleting item", it)
                result.value = false
            }

        return result
    }

}