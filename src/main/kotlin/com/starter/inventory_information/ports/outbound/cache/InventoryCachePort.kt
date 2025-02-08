package com.starter.inventory_information.ports.outbound.cache

import com.starter.inventory_information.domain.model.InventoryLevel

interface InventoryCachePort {
    fun saveInventoryLevel(level: InventoryLevel): InventoryLevel
    fun getInventoryLevel(itemId: Long, locationCode: String): InventoryLevel?
    fun updateAvailableQuantity(itemId: Long, locationCode: String, newQuantity: Int): InventoryLevel?
    fun updateReservedQuantity(itemId: Long, locationCode: String, newQuantity: Int): InventoryLevel?
    fun deleteInventoryLevel(itemId: Long, locationCode: String)
    fun getInventoryLevelsForLocation(locationCode: String): List<InventoryLevel>
    fun getMultipleInventoryLevels(itemIds: List<Long>, locationCode: String): List<InventoryLevel>
}