package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.InventoryLevel
import com.starter.inventory_information.ports.outbound.cache.InventoryCachePort
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryCachePort: InventoryCachePort
) {
    /**
     * Retrieves inventory level for specific item and location
     */
    fun getInventoryLevel(itemId: Long, locationCode: String): InventoryLevel? =
        inventoryCachePort.getInventoryLevel(itemId, locationCode)

    /**
     * Saves new inventory level
     */
    fun saveInventoryLevel(level: InventoryLevel): InventoryLevel =
        inventoryCachePort.saveInventoryLevel(level)

    /**
     * Updates available quantity for specific item and location
     */
    fun updateInventoryQuantity(itemId: Long, locationCode: String, newQuantity: Int): InventoryLevel? =
        inventoryCachePort.updateAvailableQuantity(itemId, locationCode, newQuantity)

    /**
     * Updates reserved quantity for specific item and location
     */
    fun updateReservedQuantity(itemId: Long, locationCode: String, newQuantity: Int): InventoryLevel? =
        inventoryCachePort.updateReservedQuantity(itemId, locationCode, newQuantity)

    /**
     * Deletes inventory level for specific item and location
     */
    fun deleteInventoryLevel(itemId: Long, locationCode: String) =
        inventoryCachePort.deleteInventoryLevel(itemId, locationCode)

    /**
     * Retrieves all inventory levels for specific location
     */
    fun getInventoryLevelsForLocation(locationCode: String): List<InventoryLevel> =
        inventoryCachePort.getInventoryLevelsForLocation(locationCode)

    /**
     * Retrieves inventory levels for multiple items at specific location
     */
    fun getMultipleInventoryLevels(itemIds: List<Long>, locationCode: String): List<InventoryLevel> =
        inventoryCachePort.getMultipleInventoryLevels(itemIds, locationCode)
}