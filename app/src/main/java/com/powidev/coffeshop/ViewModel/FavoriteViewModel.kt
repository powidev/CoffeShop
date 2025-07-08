package com.powidev.coffeshop.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.Repository.FavoriteRepository

class FavoriteViewModel : ViewModel() {

    private val repository = FavoriteRepository()

    fun addToFavorites(item: ItemsModel, uid: String): LiveData<Boolean> {
        return repository.addToFavorites(item, uid)
    }

    fun loadFavorites(uid: String): LiveData<MutableList<ItemsModel>> {
        return repository.loadFavorites(uid)
    }

    fun removeFromFavorites(item: ItemsModel, uid: String): LiveData<Boolean> {
        return repository.removeFromFavorites(item, uid)
    }

    fun isInFavorites(item: ItemsModel, uid: String): LiveData<Boolean> {
        return repository.isInFavorites(item, uid)
    }
}
