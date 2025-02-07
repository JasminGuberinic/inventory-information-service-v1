package com.starter.inventory_information.domain.model

class InventoryItem(
    val id: Long,
    var name: String,
    var quantity: Int,
    var price: Double,
    var dimensions: Dimensions?,
    var weight: Weight?,
    var packaging: Packaging?
) {
    fun isSensitiveItem(): Boolean? {
        return packaging?.isSensitive
    }

    fun calculateTotalWeight(): Double? {
        return weight?.value?.times(quantity)
    }
}