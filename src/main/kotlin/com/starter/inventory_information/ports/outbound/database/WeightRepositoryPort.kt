package com.starter.inventory_information.ports.outbound.database

import com.starter.inventory_information.domain.model.Weight
import java.util.*

interface WeightRepositoryPort {
    fun save(inventoryItemId: Long, weight: Weight): Weight
    fun findByInventoryItemId(inventoryItemId: Long): Optional<Weight>
    fun update(id: Long, weight: Weight): Weight
    fun delete(id: Long)
}