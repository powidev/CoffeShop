package com.powidev.coffeshop.Domain

import java.io.Serializable

data class ItemsModel(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var picUrl: List<String> = emptyList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    // Campos no editables (mantienen su valor original)
    var numberInCart: Int = 0,
    var extra: String = "",
    var categoryId: String = ""
) : Serializable {
    // Método para actualización parcial
    fun getUpdatableFields(): Map<String, Any?> = mapOf(
        "title" to title.ifBlank { null },
        "rating" to if (rating >= 0) rating else null,
        "description" to description.ifBlank { null },
        "picUrl" to if (picUrl.isEmpty()) null else picUrl,
        "price" to if (price > 0) price else null
    )
}
