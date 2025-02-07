package com.starter.inventory_information.application.service

import StorageArrangement
import StorageCompatibility
import StorageLayout
import StorageOptimizationService
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import org.springframework.stereotype.Service

@Service
class StorageManagementService(
    private val inventoryRepositoryPort: InventoryRepositoryPort,
    private val storageOptimizationService: StorageOptimizationService
) {
    fun optimizeStorageForItems(itemIds: List<Long>): StorageLayout {
        val items = itemIds.mapNotNull { id ->
            inventoryRepositoryPort.findById(id).orElse(null)
        }
        return storageOptimizationService.optimizeStorageLayout(items)
    }

    fun checkItemsCompatibility(firstItemId: Long, secondItemId: Long): StorageCompatibility {
        val firstItem = inventoryRepositoryPort.findById(firstItemId)
            .orElseThrow { NoSuchElementException("Item with id=$firstItemId not found") }
        val secondItem = inventoryRepositoryPort.findById(secondItemId)
            .orElseThrow { NoSuchElementException("Item with id=$secondItemId not found") }

        return storageOptimizationService.checkStorageCompatibility(firstItem, secondItem)
    }

    fun calculateStorageArrangement(itemId: Long, paletteCapacity: Double): StorageArrangement {
        val item = inventoryRepositoryPort.findById(itemId)
            .orElseThrow { NoSuchElementException("Item with id=$itemId not found") }

        return storageOptimizationService.calculateStorageArrangement(item, paletteCapacity)
    }
}
