package com.starter.inventory_information.adapters.outbound.database.entities

import com.starter.inventory_information.domain.model.InventoryLevel
import jakarta.persistence.*

@Entity
@Table(name = "inventory_levels")
class InventoryLevelEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val itemId: Long,
    val locationCode: String,
    val availableQuantity: Int,
    val reservedQuantity: Int,
    val lastUpdated: String
) {
    fun toDomain() = InventoryLevel(
        itemId = itemId,
        availableQuantity = availableQuantity,
        reservedQuantity = reservedQuantity,
        locationCode = locationCode,
        lastUpdated = lastUpdated
    )
}