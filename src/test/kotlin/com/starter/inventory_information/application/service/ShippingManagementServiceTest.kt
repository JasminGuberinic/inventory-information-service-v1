package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.service.*
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class ShippingManagementServiceTest {

    private lateinit var inventoryRepositoryPort: InventoryRepositoryPort
    private lateinit var shippingOptimizationService: ShippingOptimizationService
    private lateinit var service: ShippingManagementService

    @BeforeEach
    fun setup() {
        inventoryRepositoryPort = mockk()
        shippingOptimizationService = mockk()
        service = ShippingManagementService(inventoryRepositoryPort, shippingOptimizationService)
    }

    @Test
    fun `test calculate shipping arrangement`() {
        // Arrange
        val itemIds = listOf(1L, 2L)
        val items = listOf(
            createInventoryItem(1L),
            createInventoryItem(2L)
        )
        val maxTruckCapacity = Weight(value = 1000.0, unit = "KG")
        val expectedArrangement = ShippingArrangement(
            numberOfShipments = 1,
            shipmentGroups = listOf(items),
            totalWeight = 200.0
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.of(items[0])
        every { inventoryRepositoryPort.findById(2L) } returns Optional.of(items[1])
        every {
            shippingOptimizationService.calculateShippingArrangement(items, maxTruckCapacity)
        } returns expectedArrangement

        // Act
        val result = service.calculateShippingArrangement(itemIds, maxTruckCapacity)

        // Assert
        assertEquals(expectedArrangement, result)
        verify(exactly = 1) { shippingOptimizationService.calculateShippingArrangement(items, maxTruckCapacity) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(2L) }
    }

    @Test
    fun `test optimize loading sequence`() {
        // Arrange
        val itemIds = listOf(1L, 2L)
        val items = listOf(
            createInventoryItem(1L),
            createInventoryItem(2L)
        )
        val expectedSequence = LoadingSequence(
            sequence = listOf(
                LoadingPhase("REGULAR", items)
            ),
            estimatedLoadingTime = 10
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.of(items[0])
        every { inventoryRepositoryPort.findById(2L) } returns Optional.of(items[1])
        every { shippingOptimizationService.optimizeLoadingSequence(items) } returns expectedSequence

        // Act
        val result = service.optimizeLoadingSequence(itemIds)

        // Assert
        assertEquals(expectedSequence, result)
        verify(exactly = 1) { shippingOptimizationService.optimizeLoadingSequence(items) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(2L) }
    }

    @Test
    fun `test estimate shipping costs`() {
        // Arrange
        val itemIds = listOf(1L, 2L)
        val items = listOf(
            createInventoryItem(1L),
            createInventoryItem(2L)
        )
        val distanceKm = 100.0
        val baseRatePerKm = 2.0
        val expectedEstimate = ShippingCostEstimate(
            baseCost = 200.0,
            weightCost = 50.0,
            sensitivityCost = 0.0,
            totalCost = 250.0
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.of(items[0])
        every { inventoryRepositoryPort.findById(2L) } returns Optional.of(items[1])
        every {
            shippingOptimizationService.estimateShippingCosts(items, distanceKm, baseRatePerKm)
        } returns expectedEstimate

        // Act
        val result = service.estimateShippingCosts(itemIds, distanceKm, baseRatePerKm)

        // Assert
        assertEquals(expectedEstimate, result)
        verify(exactly = 1) {
            shippingOptimizationService.estimateShippingCosts(items, distanceKm, baseRatePerKm)
        }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(2L) }
    }

    @Test
    fun `test calculate shipping arrangement with empty items throws exception`() {
        // Arrange
        val itemIds = listOf(1L)
        val maxTruckCapacity = Weight(value = 1000.0, unit = "KG")

        every { inventoryRepositoryPort.findById(1L) } returns Optional.empty()

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            service.calculateShippingArrangement(itemIds, maxTruckCapacity)
        }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
    }

    private fun createInventoryItem(id: Long) = InventoryItem(
        id = id,
        name = "Test Item $id",
        quantity = 1,
        price = 100.0,
        dimensions = null,
        weight = Weight(value = 100.0, unit = "KG"),
        packaging = null
    )
}