package com.starter.inventory_information.application.service

import com.starter.inventory_information.adapters.outbound.messaging.InventoryItemEventProducer
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import org.springframework.stereotype.Service

@Service
class InventoryItemServiceImp(
    private val inventoryRepositoryPort: InventoryRepositoryPort,
    private val inventoryItemEventProducer: InventoryItemEventProducer
) : InventoryItemService {

    override fun addInventoryItem(item: InventoryItem): InventoryItem {
        val savedItem = inventoryRepositoryPort.save(item)
        inventoryItemEventProducer.sendInventoryItemCreatedEvent(savedItem.id)
        return savedItem
    }

    override fun getInventoryItemById(id: Long): InventoryItem? {
        return inventoryRepositoryPort.findById(id).orElse(null)
    }

    override fun updateInventoryItem(id: Long, item: InventoryItem): InventoryItem {
        val existingEntityOpt = inventoryRepositoryPort.findById(id)
        if (existingEntityOpt.isEmpty) {
            throw NoSuchElementException("InventoryItem with id $id not found.")
        }

        val existingEntity = existingEntityOpt.get()
        val updatedInventoryItem = existingEntity.apply {
            name = item.name
            quantity = item.quantity
            price = item.price
        }

        val savedEntity = inventoryRepositoryPort.save(updatedInventoryItem)

        inventoryItemEventProducer.sendInventoryItemUpdatedEvent(savedEntity.id)
        return savedEntity;
    }

    override fun removeInventoryItem(id: Long) {
        inventoryRepositoryPort.delete(id)
        inventoryItemEventProducer.sendInventoryItemRemovedEvent(id)
    }
}