package com.starter.inventory_information.ports.outbound.database

import com.starter.inventory_information.domain.model.InventoryItem
import java.util.*

//This is how to handle processed data that exists our system, and it goes in to some database
interface InventoryRepositoryPort {
    fun save(item: InventoryItem): InventoryItem
    fun findById(id: Long): Optional<InventoryItem>
    fun update(id: Long, item: InventoryItem): InventoryItem
    fun delete(id: Long)
}