package com.powidev.coffeshop.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.Repository.MainRepository

class DetailAdminViewModel(private val repository: MainRepository) : ViewModel() {

    fun createItem(item: ItemsModel): LiveData<Boolean> {
        return repository.createItem(item)
    }

    fun updateItem(item: ItemsModel): LiveData<Boolean> {
        return repository.updateItem(item)
    }

    fun deleteItem(itemId: String): LiveData<Boolean> {
        return repository.deleteItem(itemId)
    }
}