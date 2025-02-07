package com.starter.inventory_information.adapters.outbound.database.repository

import com.starter.inventory_information.adapters.outbound.database.entities.WeightEntity
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.ports.outbound.database.WeightRepositoryPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class WeightJpaAdapter(
    private val inventoryItemRepository: JpaInventoryItemRepository
) : WeightRepositoryPort {

    override fun save(inventoryItemId: Long, weight: Weight): Weight {
        val inventoryItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = inventoryItem.copy(
            weight = weight.toEntity()
        )

        val savedItem = inventoryItemRepository.save(updatedItem)
        return savedItem.weight?.toDomain()
            ?: throw IllegalStateException("Weight should not be null after save")
    }

    override fun findByInventoryItemId(inventoryItemId: Long): Optional<Weight> {
        return inventoryItemRepository.findById(inventoryItemId)
            .map { it.weight?.toDomain() }
    }

    override fun update(inventoryItemId: Long, weight: Weight): Weight {
        val existingItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = existingItem.copy(
            weight = weight.toEntity()
        )

        val savedItem = inventoryItemRepository.save(updatedItem)
        return savedItem.weight?.toDomain()
            ?: throw IllegalStateException("Weight should not be null after update")
    }

    override fun delete(inventoryItemId: Long) {
        val existingItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = existingItem.copy(weight = null)
        inventoryItemRepository.save(updatedItem)
    }

    private fun Weight.toEntity(): WeightEntity {
        return WeightEntity(
            value = this.value,
            unit = this.unit
        )
    }

    private fun WeightEntity.toDomain(): Weight {
        return Weight(
            value = this.value,
            unit = this.unit
        )
    }
}