package com.starter.inventory_information.application.service

import com.starter.inventory_information.adapters.outbound.messaging.InventoryItemEventProducer
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class InventoryItemServiceImpTest {

    private lateinit var inventoryRepositoryPort: InventoryRepositoryPort
    private lateinit var inventoryItemEventProducer: InventoryItemEventProducer
    private lateinit var service: InventoryItemServiceImp

    @BeforeEach
    fun setup() {
        inventoryRepositoryPort = mockk()
        inventoryItemEventProducer = mockk()
        service = InventoryItemServiceImp(inventoryRepositoryPort, inventoryItemEventProducer)
    }

    @Test
    fun `test add inventory item`() {
        // Arrange
        val item = InventoryItem(
            id = 1L,
            name = "Test Item",
            quantity = 5,
            price = 29.99,
            dimensions = null,
            weight = null,
            packaging = null
        )

        every { inventoryRepositoryPort.save(item) } returns item
        every { inventoryItemEventProducer.sendInventoryItemCreatedEvent(1L) } just runs

        // Act
        val result = service.addInventoryItem(item)

        // Assert
        assertEquals(item, result)
        verify(exactly = 1) { inventoryRepositoryPort.save(item) }
        verify(exactly = 1) { inventoryItemEventProducer.sendInventoryItemCreatedEvent(1L) }
    }

    @Test
    fun `test get inventory item by id`() {
        // Arrange
        val item = InventoryItem(
            id = 1L,
            name = "Test Item",
            quantity = 5,
            price = 29.99,
            dimensions = null,
            weight = null,
            packaging = null
        )

        val optional: Optional<InventoryItem> = Optional.of(item)
        every { inventoryRepositoryPort.findById(1L) } returns optional

        // Act
        val result = service.getInventoryItemById(1L)

        // Assert
        assertNotNull(result)
        assertEquals(item.id, result?.id)
        assertEquals(item.name, result?.name)
        assertEquals(item.quantity, result?.quantity)
        assertEquals(item.price, result?.price)
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
    }

    @Test
    fun `test update inventory item`() {
        // Arrange
        val existingItem = InventoryItem(
            id = 1L,
            name = "Test Item",
            quantity = 5,
            price = 29.99,
            dimensions = null,
            weight = null,
            packaging = null
        )

        val updatedItem = InventoryItem(
            id = existingItem.id,
            name = "Updated Item",
            quantity = 10,
            price = 39.99,
            dimensions = null,
            weight = null,
            packaging = null
        )

        val optional: Optional<InventoryItem> = Optional.of(existingItem)
        every { inventoryRepositoryPort.findById(1L) } returns optional
        every { inventoryRepositoryPort.save(any()) } returns updatedItem
        every { inventoryItemEventProducer.sendInventoryItemUpdatedEvent(1L) } just runs

        // Act
        val result = service.updateInventoryItem(1L, updatedItem)

        // Assert
        assertNotNull(result)
        assertEquals(updatedItem.id, result.id)
        assertEquals(updatedItem.name, result.name)
        assertEquals(updatedItem.quantity, result.quantity)
        assertEquals(updatedItem.price, result.price)
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.save(any()) }
        verify(exactly = 1) { inventoryItemEventProducer.sendInventoryItemUpdatedEvent(1L) }
    }

    @Test
    fun `test remove inventory item`() {
        // Arrange
        every { inventoryRepositoryPort.delete(1L) } just runs
        every { inventoryItemEventProducer.sendInventoryItemRemovedEvent(1L) } just runs

        // Act
        service.removeInventoryItem(1L)

        // Assert
        verify(exactly = 1) { inventoryRepositoryPort.delete(1L) }
        verify(exactly = 1) { inventoryItemEventProducer.sendInventoryItemRemovedEvent(1L) }
    }

    @Test
    fun `test update inventory item throws exception when item not found`() {
        // Arrange
        val updatedItem = InventoryItem(
            id = 1L,
            name = "Updated Item",
            quantity = 10,
            price = 39.99,
            dimensions = null,
            weight = null,
            packaging = null
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.empty()

        // Act & Assert
        assertThrows<NoSuchElementException> {
            service.updateInventoryItem(1L, updatedItem)
        }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
    }

    @Test
    fun `test get inventory item returns null when item not found`() {
        // Arrange
        every { inventoryRepositoryPort.findById(1L) } returns Optional.empty()

        // Act
        val result = service.getInventoryItemById(1L)

        // Assert
        assertNull(result)
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
    }
}