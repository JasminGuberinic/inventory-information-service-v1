package com.starter.inventory_information.ports.outbound.database

import com.starter.inventory_information.domain.model.Dimensions
import java.util.*

interface DimensionsRepositoryPort {
    fun save(inventoryItemId: Long, dimensions: Dimensions): Dimensions
    fun findByInventoryItemId(inventoryItemId: Long): Optional<Dimensions>
    fun update(id: Long, dimensions: Dimensions): Dimensions
    fun delete(id: Long)
}