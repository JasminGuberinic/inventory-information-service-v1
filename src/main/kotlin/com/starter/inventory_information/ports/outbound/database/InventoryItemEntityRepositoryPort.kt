package com.starter.inventory_information.ports.outbound.database

import com.starter.inventory_information.adapters.outbound.database.entities.InventoryItemEntity
import java.util.*

interface InventoryItemEntityRepositoryPort {
    fun save(item: InventoryItemEntity): InventoryItemEntity
    fun update(id: Long, item: InventoryItemEntity): InventoryItemEntity
}