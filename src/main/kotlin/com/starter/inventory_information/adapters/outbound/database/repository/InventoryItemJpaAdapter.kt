package com.starter.inventory_information.adapters.outbound.database.repository

import com.starter.inventory_information.adapters.outbound.database.entities.DimensionsEntity
import com.starter.inventory_information.adapters.outbound.database.entities.InventoryItemEntity
import com.starter.inventory_information.adapters.outbound.database.entities.PackagingEntity
import com.starter.inventory_information.adapters.outbound.database.entities.WeightEntity
import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.ports.outbound.database.InventoryItemEntityRepositoryPort
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

/**
 * JPA adapter (an implementation of the domain-driven InventoryRepository).
 * It depends on the JpaInventoryRepository for DB access,
 * and converts to/from domain InventoryItem objects.
 */
@Component
class InventoryItemJpaAdapter(
    private val jpaRepo: JpaInventoryItemRepository
) : InventoryRepositoryPort, InventoryItemEntityRepositoryPort {

    override fun save(item: InventoryItem): InventoryItem {
        val entity = item.toEntity()
        val savedEntity = jpaRepo.save(entity)
        return savedEntity.toDomain()
    }

    override fun save(item: InventoryItemEntity): InventoryItemEntity =
        jpaRepo.save(item)


    override fun findById(id: Long): Optional<InventoryItem> {
        val entityOpt = jpaRepo.findById(id)
        return entityOpt.map { it?.toDomain() }
    }

    override fun update(id: Long, item: InventoryItemEntity): InventoryItemEntity {
        TODO("Not yet implemented")
    }

    override fun update(id: Long, item: InventoryItem): InventoryItem {
        val existingOpt = jpaRepo.findById(id)
        if (existingOpt.isEmpty) {
            throw NoSuchElementException("InventoryItemEntity with id=$id not found.")
        }
        val existingEntity = existingOpt.get()
        val updatedEntity = existingEntity.copy(
            name = item.name,
            quantity = item.quantity,
            price = BigDecimal.valueOf(item.price)
            // createdAt i updatedAt prepusti DB-u ili ažuriraj ručno, ovisno o potrebama
        )
        val savedEntity = jpaRepo.save(updatedEntity)
        return savedEntity.toDomain()
    }

    override fun delete(id: Long) {
        jpaRepo.deleteById(id)
    }

    private fun InventoryItemEntity.toDomain(): InventoryItem {
        return InventoryItem(
            id = this.id ?: 0L,
            name = this.name,
            quantity = this.quantity,
            price = this.price.toDouble(),
            dimensions = this.dimensions?.let {
                Dimensions(
                    length = it.length,
                    width = it.width,
                    height = it.height
                )
            },
            weight = this.weight?.let {
                Weight(
                    value = it.value,
                    unit = it.unit
                )
            },
            packaging = this.packaging?.let {
                Packaging(
                    isSensitive = it.isSensitive,
                    packagingType = it.packagingType
                )
            }
        )
    }

    /**
     * Pretvorba iz domene u entitet
     */
    private fun InventoryItem.toEntity(): InventoryItemEntity {
        return InventoryItemEntity(
            id = null, // Let the DB generate the ID
            name = this.name,
            quantity = this.quantity,
            price = BigDecimal.valueOf(this.price),
            dimensions = this.dimensions?.let {
                DimensionsEntity(
                    length = it.length,
                    width = it.width,
                    height = it.height
                )
            },
            weight = this.weight?.let {
                WeightEntity(
                    value = it.value,
                    unit = it.unit
                )
            },
            packaging = this.packaging?.let {
                PackagingEntity(
                    isSensitive = it.isSensitive,
                    packagingType = it.packagingType
                )
            },
            createdAt = null,
            updatedAt = null
        )
    }
}