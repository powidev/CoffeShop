package com.powidev.coffeshop.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.powidev.coffeshop.Domain.BannerModel
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>> {
        return repository.loadPopular()
    }
}