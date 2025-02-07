package com.starter.inventory_information.adapters.outbound.database.repository

import com.starter.inventory_information.adapters.outbound.database.entities.DimensionsEntity
import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.ports.outbound.database.DimensionsRepositoryPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class DimensionsJpaAdapter(
    private val inventoryItemRepository: JpaInventoryItemRepository
) : DimensionsRepositoryPort {

    override fun save(inventoryItemId: Long, dimensions: Dimensions): Dimensions {
        val inventoryItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = inventoryItem.copy(
            dimensions = dimensions.toEntity()
        )

        val savedItem = inventoryItemRepository.save(updatedItem)
        return savedItem.dimensions?.toDomain()
            ?: throw IllegalStateException("Dimensions should not be null after save")
    }

    override fun findByInventoryItemId(inventoryItemId: Long): Optional<Dimensions> {
        return inventoryItemRepository.findById(inventoryItemId)
            .map { it.dimensions?.toDomain() }
    }

    override fun update(inventoryItemId: Long, dimensions: Dimensions): Dimensions {
        val existingItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = existingItem.copy(
            dimensions = dimensions.toEntity()
        )

        val savedItem = inventoryItemRepository.save(updatedItem)
        return savedItem.dimensions?.toDomain()
            ?: throw IllegalStateException("Dimensions should not be null after update")
    }

    override fun delete(inventoryItemId: Long) {
        val existingItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = existingItem.copy(dimensions = null)
        inventoryItemRepository.save(updatedItem)
    }

    private fun Dimensions.toEntity(): DimensionsEntity {
        return DimensionsEntity(
            length = this.length,
            width = this.width,
            height = this.height
        )
    }

    private fun DimensionsEntity.toDomain(): Dimensions {
        return Dimensions(
            length = this.length,
            width = this.width,
            height = this.height
        )
    }
}