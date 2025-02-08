package com.starter.inventory_information.integration

import com.starter.inventory_information.adapters.outbound.messaging.InventoryItemEventProducer
import com.starter.inventory_information.adapters.outbound.redis.RedisInventoryAdapter
import com.starter.inventory_information.config.TestRedisConfiguration
import com.starter.inventory_information.domain.model.InventoryChange
import com.starter.inventory_information.domain.model.InventoryLevel
import com.starter.inventory_information.domain.model.QuantityUpdate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootTest(
    classes = [
        TestRedisConfiguration::class,
        RedisInventoryAdapter::class
    ]
)
@ActiveProfiles("test")
@EnableRedisRepositories
class RedisInventoryAdapterIntegrationTest {

    @Autowired
    private lateinit var redisInventoryAdapter: RedisInventoryAdapter

    @MockBean
    private lateinit var inventoryItemEventProducer: InventoryItemEventProducer

    @BeforeEach
    fun setup() {
        redisInventoryAdapter.deleteInventoryLevel(TEST_ITEM_ID, TEST_LOCATION)
    }

    @Test
    fun `test save and get inventory level`() {
        // Arrange
        val inventoryLevel = InventoryLevel(
            itemId = TEST_ITEM_ID,
            availableQuantity = 10,
            reservedQuantity = 0,
            locationCode = TEST_LOCATION
        )

        // Act & Assert - First verify save operation
        redisInventoryAdapter.saveInventoryLevel(inventoryLevel)

        // Then verify retrieval
        val retrieved = redisInventoryAdapter.getInventoryLevel(TEST_ITEM_ID, TEST_LOCATION)

        // Verify all fields
        assertNotNull(retrieved, "Retrieved inventory level should not be null")
        retrieved?.let {
            assertEquals(TEST_ITEM_ID, it.itemId, "Item ID should match")
            assertEquals(10, it.availableQuantity, "Available quantity should match")
            assertEquals(0, it.reservedQuantity, "Reserved quantity should match")
            assertEquals(TEST_LOCATION, it.locationCode, "Location code should match")
            assertNotNull(it.lastUpdated, "Last updated timestamp should not be null")
        }
    }

    @Test
    fun `test delete inventory level`() {
        // Arrange
        val inventoryLevel = InventoryLevel(
            itemId = TEST_ITEM_ID,
            availableQuantity = 10,
            reservedQuantity = 0,
            locationCode = TEST_LOCATION
        )
        redisInventoryAdapter.saveInventoryLevel(inventoryLevel)

        // Act
        redisInventoryAdapter.deleteInventoryLevel(TEST_ITEM_ID, TEST_LOCATION)

        // Assert
        val retrieved = redisInventoryAdapter.getInventoryLevel(TEST_ITEM_ID, TEST_LOCATION)
        assertNull(retrieved, "Inventory level should be null after deletion")
    }

    @Test
    fun `test update available quantity triggers kafka event`() {
        // Arrange
        val inventoryLevel = InventoryLevel(
            itemId = TEST_ITEM_ID,
            availableQuantity = 10,
            locationCode = TEST_LOCATION
        )
        redisInventoryAdapter.saveInventoryLevel(inventoryLevel)

        // Act
        redisInventoryAdapter.updateAvailableQuantity(TEST_ITEM_ID, TEST_LOCATION, 15)

        // Assert
        verify(inventoryItemEventProducer, times(1)).sendInventoryItemUpdatedEvent(TEST_ITEM_ID)
    }

    @Test
    fun `test get inventory levels for location`() {
        // Arrange - save multiple items for same location
        val item1 = InventoryLevel(
            itemId = TEST_ITEM_ID,
            availableQuantity = 10,
            locationCode = TEST_LOCATION
        )
        val item2 = InventoryLevel(
            itemId = TEST_ITEM_ID + 1,
            availableQuantity = 20,
            locationCode = TEST_LOCATION
        )

        redisInventoryAdapter.saveInventoryLevel(item1)
        redisInventoryAdapter.saveInventoryLevel(item2)

        // Act
        val result = redisInventoryAdapter.getInventoryLevelsForLocation(TEST_LOCATION)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.any { it.itemId == TEST_ITEM_ID })
        assertTrue(result.any { it.itemId == TEST_ITEM_ID + 1 })
    }

    @Test
    fun `test update multiple quantities`() {
        // Arrange
        val item1 = InventoryLevel(
            itemId = TEST_ITEM_ID,
            availableQuantity = 10,
            locationCode = TEST_LOCATION
        )
        redisInventoryAdapter.saveInventoryLevel(item1)

        val updates = listOf(
            QuantityUpdate(
                itemId = TEST_ITEM_ID,
                locationCode = TEST_LOCATION,
                newQuantity = 15
            )
        )

        // Act
        val results = redisInventoryAdapter.updateMultipleQuantities(updates)

        // Assert
        assertEquals(1, results.size)
        assertEquals(15, results.first().availableQuantity)
        verify(inventoryItemEventProducer).sendInventoryItemUpdatedEvent(TEST_ITEM_ID)
    }

    @Test
    fun `test get multiple inventory levels`() {
        // Arrange
        val item1 = InventoryLevel(
            itemId = TEST_ITEM_ID,
            availableQuantity = 10,
            locationCode = TEST_LOCATION
        )
        val item2 = InventoryLevel(
            itemId = TEST_ITEM_ID + 1,
            availableQuantity = 20,
            locationCode = TEST_LOCATION
        )

        redisInventoryAdapter.saveInventoryLevel(item1)
        redisInventoryAdapter.saveInventoryLevel(item2)

        // Act
        val results = redisInventoryAdapter.getMultipleInventoryLevels(
            listOf(TEST_ITEM_ID, TEST_ITEM_ID + 1),
            TEST_LOCATION
        )

        // Assert
        assertEquals(2, results.size)
        assertTrue(results.any { it.itemId == TEST_ITEM_ID })
        assertTrue(results.any { it.itemId == TEST_ITEM_ID + 1 })
    }

    @Test
    fun `test inventory change history`() {
        // Arrange
        val change = InventoryChange(
            itemId = TEST_ITEM_ID,
            locationCode = TEST_LOCATION,
            oldQuantity = 10,
            newQuantity = 15,
            reason = "Stock adjustment"
        )

        // Act
        redisInventoryAdapter.saveInventoryChange(change)
        val changes = redisInventoryAdapter.getInventoryChanges(TEST_ITEM_ID, TEST_LOCATION)

        // Assert
        assertEquals(1, changes.size)
        with(changes.first()) {
            assertEquals(TEST_ITEM_ID, itemId)
            assertEquals(TEST_LOCATION, locationCode)
            assertEquals(10, oldQuantity)
            assertEquals(15, newQuantity)
            assertEquals("Stock adjustment", reason)
        }
    }

    companion object {
        private const val TEST_ITEM_ID = 1L
        private const val TEST_LOCATION = "TEST_WH"
    }
}