package com.powidev.coffeshop.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.powidev.coffeshop.Repository.MainRepository
import com.powidev.coffeshop.ViewModel.DetailAdminViewModel

class ViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailAdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}