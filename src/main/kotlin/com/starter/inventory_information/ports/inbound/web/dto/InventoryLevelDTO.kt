package com.starter.inventory_information.ports.inbound.web.dto

import com.starter.inventory_information.domain.model.InventoryLevel
import java.time.LocalDateTime

data class InventoryLevelDTO(
    val itemId: Long,
    val locationCode: String,
    val availableQuantity: Int,
    val reservedQuantity: Int = 0,
    val lastUpdated: String? = null
) {
    fun toDomain() = InventoryLevel(
        itemId = itemId,
        locationCode = locationCode,
        availableQuantity = availableQuantity,
        reservedQuantity = reservedQuantity,
        lastUpdated = lastUpdated ?: LocalDateTime.now().toString()
    )

    companion object {
        fun fromDomain(domain: InventoryLevel) = InventoryLevelDTO(
            itemId = domain.itemId,
            locationCode = domain.locationCode,
            availableQuantity = domain.availableQuantity,
            reservedQuantity = domain.reservedQuantity,
            lastUpdated = domain.lastUpdated
        )
    }
}