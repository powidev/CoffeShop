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

    fun deleteItemByTitle(title: String): LiveData<Boolean> {
        return repository.deleteItemByTitle(title)
    }

    fun addToPopular(item: ItemsModel): LiveData<Boolean> {
        return repository.addToPopular(item)
    }

    fun isProductInPopular(item: ItemsModel): LiveData<Boolean> {
        return repository.isProductInPopular(item)
    }

}