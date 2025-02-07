package com.starter.inventory_information.integration

import com.starter.inventory_information.adapters.inbound.web.controller.InventoryController
import com.starter.inventory_information.adapters.outbound.database.entities.InventoryItemEntity
import com.starter.inventory_information.adapters.outbound.database.repository.JpaInventoryItemRepository
import com.starter.inventory_information.ports.inbound.web.dto.DimensionsDTO
import com.starter.inventory_information.ports.inbound.web.dto.InventoryItemDTO
import com.starter.inventory_information.ports.inbound.web.dto.PackagingDTO
import com.starter.inventory_information.ports.inbound.web.dto.WeightDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

@SpringBootTest
@Transactional
class InventoryControllerIntegrationTest {

    @Autowired
    private lateinit var inventoryController: InventoryController

    @Autowired
    private lateinit var inventoryItemRepository: JpaInventoryItemRepository

    private lateinit var testInventoryItem: InventoryItemEntity

    @BeforeEach
    fun setup() {
        testInventoryItem = inventoryItemRepository.save(
            InventoryItemEntity(
                name = "Test Item",
                quantity = 1,
                price = BigDecimal.TEN
            )
        )
    }

    @Test
    fun `test add inventory item`() {
        val itemDTO = InventoryItemDTO(
            name = "New Test Item",
            quantity = 5,
            price = 15.99,
            weight = null,
            packaging = null,
            dimensions = null
        )

        val response = inventoryController.addItem(itemDTO)

        assertTrue(response.statusCode.is2xxSuccessful)
        val savedItem = response.body
        requireNotNull(savedItem) { "Response body should not be null" }
        assertEquals("New Test Item", savedItem.name)
        assertEquals(5, savedItem.quantity)
        assertEquals(BigDecimal("15.99"), savedItem.price)

        // Verify in database
        val itemInDb = inventoryItemRepository.findById(savedItem.id!!).get()
        assertEquals("New Test Item", itemInDb.name)
        assertEquals(5, itemInDb.quantity)
        assertEquals(BigDecimal("15.99"), itemInDb.price)
    }

    @Test
    fun `test get inventory item`() {
        val response = inventoryController.getItem(testInventoryItem.id!!)

        assertTrue(response.statusCode.is2xxSuccessful)
        val retrievedItem = response.body
        requireNotNull(retrievedItem) { "Response body should not be null" }
        assertEquals(testInventoryItem.name, retrievedItem.name)
        assertEquals(testInventoryItem.quantity, retrievedItem.quantity)
        assertEquals(testInventoryItem.price, retrievedItem.price)
    }

    @Test
    fun `test update inventory item`() {
        val updateDTO = InventoryItemDTO(
            id = testInventoryItem.id,
            name = "Updated Test Item",
            quantity = 10,
            price = 25.99,
            weight = null,
            packaging = null,
            dimensions = null
        )

        val response = inventoryController.updateItem(testInventoryItem.id!!, updateDTO)

        assertTrue(response.statusCode.is2xxSuccessful)
        val updatedItem = response.body
        requireNotNull(updatedItem) { "Response body should not be null" }
        assertEquals("Updated Test Item", updatedItem.name)
        assertEquals(10, updatedItem.quantity)
        assertEquals(BigDecimal("25.99"), updatedItem.price)

        // Verify in database
        val itemInDb = inventoryItemRepository.findById(testInventoryItem.id!!).get()
        assertEquals("Updated Test Item", itemInDb.name)
        assertEquals(10, itemInDb.quantity)
        assertEquals(BigDecimal("25.99"), itemInDb.price)
    }

    @Test
    fun `test remove inventory item`() {
        val response = inventoryController.removeItem(testInventoryItem.id!!)

        assertTrue(response.statusCode.is2xxSuccessful)

        // Verify item is removed from database
        val itemExists = inventoryItemRepository.existsById(testInventoryItem.id!!)
        assertFalse(itemExists)
    }

    @Test
    fun `test get non-existent inventory item returns 404`() {
        val nonExistentId = 999L
        val response = inventoryController.getItem(nonExistentId)

        assertTrue(response.statusCode.is4xxClientError)
    }

    @Test
    fun `test update non-existent inventory item returns 404`() {
        val nonExistentId = 999L
        val updateDTO = InventoryItemDTO(
            id = nonExistentId,
            name = "Updated Test Item",
            quantity = 10,
            price = 25.99,
            weight = null,
            packaging = null,
            dimensions = null
        )

        val response = inventoryController.updateItem(nonExistentId, updateDTO)

        assertTrue(response.statusCode.is4xxClientError)
    }

    @Test
    fun `test add and update inventory item with all attributes`() {
        val itemDTO = InventoryItemDTO(
            name = "Complete Test Item",
            quantity = 3,
            price = 29.99,
            dimensions = DimensionsDTO(
                inventoryItemId = 0,
                length = 10.0,
                width = 5.0,
                height = 3.0
            ),
            weight = WeightDTO(
                inventoryItemId = 0,
                value = 2.5,
                unit = "KG"
            ),
            packaging = PackagingDTO(
                inventoryItemId = 0,
                isSensitive = true,
                packagingType = "FRAGILE"
            )
        )

        // Test adding item with all attributes
        val addResponse = inventoryController.addItem(itemDTO)

        assertTrue(addResponse.statusCode.is2xxSuccessful)
        val savedItem = addResponse.body
        requireNotNull(savedItem) { "Response body should not be null" }

        // Verify main attributes
        assertEquals("Complete Test Item", savedItem.name)
        assertEquals(3, savedItem.quantity)
        assertEquals(BigDecimal("29.99"), savedItem.price)

        // Verify dimensions
        assertNotNull(savedItem.dimensions)
        assertEquals(10.0, savedItem.dimensions?.length)
        assertEquals(5.0, savedItem.dimensions?.width)
        assertEquals(3.0, savedItem.dimensions?.height)

        // Verify weight
        assertNotNull(savedItem.weight)
        assertEquals(2.5, savedItem.weight?.value)
        assertEquals("KG", savedItem.weight?.unit)

        // Verify packaging
        assertNotNull(savedItem.packaging)
        assertTrue(savedItem.packaging?.isSensitive ?: false)
        assertEquals("FRAGILE", savedItem.packaging?.packagingType)

        // Test updating the item
        val updateDTO = InventoryItemDTO(
            id = savedItem.id,
            name = "Updated Complete Item",
            quantity = 5,
            price = 39.99,
            dimensions = DimensionsDTO(
                inventoryItemId = savedItem.id!!,
                length = 15.0,
                width = 7.0,
                height = 4.0
            ),
            weight = WeightDTO(
                inventoryItemId = savedItem.id!!,
                value = 3.0,
                unit = "KG"
            ),
            packaging = PackagingDTO(
                inventoryItemId = savedItem.id!!,
                isSensitive = false,
                packagingType = "STANDARD"
            )
        )

        val updateResponse = inventoryController.updateItem(savedItem.id!!, updateDTO)

        assertTrue(updateResponse.statusCode.is2xxSuccessful)
        val updatedItem = updateResponse.body
        requireNotNull(updatedItem) { "Response body should not be null" }

        // Verify updated main attributes
        assertEquals("Updated Complete Item", updatedItem.name)
        assertEquals(5, updatedItem.quantity)
        assertEquals(BigDecimal("39.99"), updatedItem.price)

        // Verify updated dimensions
        assertNotNull(updatedItem.dimensions)
        assertEquals(15.0, updatedItem.dimensions?.length)
        assertEquals(7.0, updatedItem.dimensions?.width)
        assertEquals(4.0, updatedItem.dimensions?.height)

        // Verify updated weight
        assertNotNull(updatedItem.weight)
        assertEquals(3.0, updatedItem.weight?.value)
        assertEquals("KG", updatedItem.weight?.unit)

        // Verify updated packaging
        assertNotNull(updatedItem.packaging)
        assertFalse(updatedItem.packaging?.isSensitive ?: true)
        assertEquals("STANDARD", updatedItem.packaging?.packagingType)

        // Verify in database
        val itemInDb = inventoryItemRepository.findById(savedItem.id!!).get()
        assertEquals("Updated Complete Item", itemInDb.name)
        assertEquals(5, itemInDb.quantity)
        assertEquals(BigDecimal("39.99"), itemInDb.price)
        assertEquals(15.0, itemInDb.dimensions?.length)
        assertEquals(7.0, itemInDb.dimensions?.width)
        assertEquals(4.0, itemInDb.dimensions?.height)
        assertEquals(3.0, itemInDb.weight?.value)
        assertEquals("KG", itemInDb.weight?.unit)
        assertFalse(itemInDb.packaging?.isSensitive ?: true)
        assertEquals("STANDARD", itemInDb.packaging?.packagingType)
    }
}