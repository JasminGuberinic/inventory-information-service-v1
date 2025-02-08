package com.starter.inventory_information.adapters.outbound.database.repository

import com.starter.inventory_information.adapters.outbound.database.entities.InventoryLevelEntity
import com.starter.inventory_information.domain.model.InventoryLevel
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryRepository : JpaRepository<InventoryLevelEntity, Long> {
    fun findByItemIdAndLocationCode(itemId: Long, locationCode: String): InventoryLevelEntity?
}

fun InventoryLevel.toEntity() = InventoryLevelEntity(
    itemId = itemId,
    locationCode = locationCode,
    availableQuantity = availableQuantity,
    reservedQuantity = reservedQuantity,
    lastUpdated = lastUpdated
)