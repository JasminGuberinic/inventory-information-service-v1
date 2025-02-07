package com.starter.inventory_information.adapters.outbound.database.repository

import com.starter.inventory_information.adapters.outbound.database.entities.PackagingEntity
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.ports.outbound.database.PackagingRepositoryPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class PackagingJpaAdapter(
    private val inventoryItemRepository: JpaInventoryItemRepository
) : PackagingRepositoryPort {

    override fun save(inventoryItemId: Long, packaging: Packaging): Packaging {
        val inventoryItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = inventoryItem.copy(
            packaging = packaging.toEntity()
        )

        val savedItem = inventoryItemRepository.save(updatedItem)
        return savedItem.packaging?.toDomain()
            ?: throw IllegalStateException("Packaging should not be null after save")
    }

    override fun findByInventoryItemId(inventoryItemId: Long): Optional<Packaging> {
        return inventoryItemRepository.findById(inventoryItemId)
            .map { it.packaging?.toDomain() }
    }

    override fun update(inventoryItemId: Long, packaging: Packaging): Packaging {
        val existingItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = existingItem.copy(
            packaging = packaging.toEntity()
        )

        val savedItem = inventoryItemRepository.save(updatedItem)
        return savedItem.packaging?.toDomain()
            ?: throw IllegalStateException("Packaging should not be null after update")
    }

    override fun delete(inventoryItemId: Long) {
        val existingItem = inventoryItemRepository.findById(inventoryItemId)
            .orElseThrow { NoSuchElementException("InventoryItem with id=$inventoryItemId not found.") }

        val updatedItem = existingItem.copy(packaging = null)
        inventoryItemRepository.save(updatedItem)
    }

    private fun Packaging.toEntity(): PackagingEntity {
        return PackagingEntity(
            isSensitive = this.isSensitive,
            packagingType = this.packagingType
        )
    }

    private fun PackagingEntity.toDomain(): Packaging {
        return Packaging(
            isSensitive = this.isSensitive,
            packagingType = this.packagingType
        )
    }
}