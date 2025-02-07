package com.starter.inventory_information.ports.inbound.web.dto

import com.starter.inventory_information.adapters.inbound.web.controller.toDomain
import com.starter.inventory_information.domain.model.InventoryItem

data class InventoryItemDTO(
    val id: Long?= null,
    val name: String,
    val quantity: Int,
    val price: Double,
    val dimensions: DimensionsDTO?,
    val weight: WeightDTO?,
    val packaging: PackagingDTO?
) {
    companion object {
        fun fromDomain(inventoryItem: InventoryItem): InventoryItemDTO {
            return InventoryItemDTO(
                id = inventoryItem.id,
                name = inventoryItem.name,
                price = inventoryItem.price,
                quantity = inventoryItem.quantity,
                dimensions = inventoryItem.dimensions?.let {
                    DimensionsDTO(
                        inventoryItemId = inventoryItem.id,
                        length = it.length,
                        width = it.width,
                        height = it.height
                    )
                },
                weight = inventoryItem.weight?.let {
                    WeightDTO(
                        inventoryItemId = inventoryItem.id,
                        value = it.value,
                        unit = it.unit
                    )
                },
                packaging = inventoryItem.packaging?.let {
                    PackagingDTO(
                        inventoryItemId = inventoryItem.id,
                        isSensitive = it.isSensitive,
                        packagingType = it.packagingType
                    )
                }
            )
        }
    }

    fun toDomain(): InventoryItem {
        return InventoryItem(
            id = this.id!!,
            name = this.name,
            price = this.price,
            quantity = this.quantity,
            dimensions = this.dimensions?.toDomain(),
            weight = this.weight?.toDomain(),
            packaging = this.packaging?.toDomain()
        )
    }
}