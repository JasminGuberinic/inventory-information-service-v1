package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.InventoryLevel
import com.starter.inventory_information.ports.outbound.cache.InventoryCachePort
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class InventoryServiceTest {

    @MockK
    private lateinit var inventoryCachePort: InventoryCachePort

    @Autowired
    private lateinit var inventoryService: InventoryService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `should get inventory level`() {
        // given
        val inventoryLevel = InventoryLevel(
            itemId = 1L,
            availableQuantity = 10,
            locationCode = "WH1"
        )
        every { inventoryCachePort.getInventoryLevel(1L, "WH1") } returns inventoryLevel

        // when
        val result = inventoryService.getInventoryLevel(1L, "WH1")

        // then
        assertEquals(inventoryLevel, result)
        verify(exactly = 1) { inventoryCachePort.getInventoryLevel(1L, "WH1") }
    }

    @Test
    fun `should save inventory level`() {
        // given
        val inventoryLevel = InventoryLevel(
            itemId = 1L,
            availableQuantity = 10,
            locationCode = "WH1"
        )
        every { inventoryCachePort.saveInventoryLevel(inventoryLevel) } returns inventoryLevel

        // when
        val result = inventoryService.saveInventoryLevel(inventoryLevel)

        // then
        assertEquals(inventoryLevel, result)
        verify(exactly = 1) { inventoryCachePort.saveInventoryLevel(inventoryLevel) }
    }

    @Test
    fun `should update inventory quantity`() {
        // given
        val updatedLevel = InventoryLevel(
            itemId = 1L,
            availableQuantity = 15,
            locationCode = "WH1"
        )
        every { inventoryCachePort.updateAvailableQuantity(1L, "WH1", 15) } returns updatedLevel

        // when
        val result = inventoryService.updateInventoryQuantity(1L, "WH1", 15)

        // then
        assertEquals(updatedLevel, result)
        verify(exactly = 1) { inventoryCachePort.updateAvailableQuantity(1L, "WH1", 15) }
    }

    @Test
    fun `should update reserved quantity`() {
        // given
        val updatedLevel = InventoryLevel(
            itemId = 1L,
            availableQuantity = 10,
            reservedQuantity = 5,
            locationCode = "WH1"
        )
        every { inventoryCachePort.updateReservedQuantity(1L, "WH1", 5) } returns updatedLevel

        // when
        val result = inventoryService.updateReservedQuantity(1L, "WH1", 5)

        // then
        assertEquals(updatedLevel, result)
        verify(exactly = 1) { inventoryCachePort.updateReservedQuantity(1L, "WH1", 5) }
    }

    @Test
    fun `should delete inventory level`() {
        // given
        every { inventoryCachePort.deleteInventoryLevel(1L, "WH1") } just runs

        // when
        inventoryService.deleteInventoryLevel(1L, "WH1")

        // then
        verify(exactly = 1) { inventoryCachePort.deleteInventoryLevel(1L, "WH1") }
    }

    @Test
    fun `should get inventory levels for location`() {
        // given
        val levels = listOf(
            InventoryLevel(itemId = 1L, availableQuantity = 10, locationCode = "WH1"),
            InventoryLevel(itemId = 2L, availableQuantity = 20, locationCode = "WH1")
        )
        every { inventoryCachePort.getInventoryLevelsForLocation("WH1") } returns levels

        // when
        val result = inventoryService.getInventoryLevelsForLocation("WH1")

        // then
        assertEquals(levels, result)
        verify(exactly = 1) { inventoryCachePort.getInventoryLevelsForLocation("WH1") }
    }

    @Test
    fun `should get multiple inventory levels`() {
        // given
        val itemIds = listOf(1L, 2L)
        val levels = listOf(
            InventoryLevel(itemId = 1L, availableQuantity = 10, locationCode = "WH1"),
            InventoryLevel(itemId = 2L, availableQuantity = 20, locationCode = "WH1")
        )
        every { inventoryCachePort.getMultipleInventoryLevels(itemIds, "WH1") } returns levels

        // when
        val result = inventoryService.getMultipleInventoryLevels(itemIds, "WH1")

        // then
        assertEquals(levels, result)
        verify(exactly = 1) { inventoryCachePort.getMultipleInventoryLevels(itemIds, "WH1") }
    }
}