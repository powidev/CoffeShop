package com.powidev.coffeshop.Domain

import java.io.Serializable

data class ItemsModel(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    val picUrl: List<String> = emptyList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    var extra: String = "",
    var categoryId: String? = null
) : Serializable {
    fun isNewItem(): Boolean = id.isNullOrEmpty()

    fun isValid(): Boolean {
        return title.isNotBlank() && price >= 0 && description.isNotBlank()
    }
}
