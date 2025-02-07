package com.starter.inventory_information.application.service

import StorageArrangement
import StorageCompatibility
import StorageLayout
import StorageOptimizationService
import StorageZone
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.model.Packaging
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

class StorageManagementServiceTest {

    private lateinit var inventoryRepositoryPort: InventoryRepositoryPort
    private lateinit var storageOptimizationService: StorageOptimizationService
    private lateinit var service: StorageManagementService

    @BeforeEach
    fun setup() {
        inventoryRepositoryPort = mockk()
        storageOptimizationService = mockk()
        service = StorageManagementService(inventoryRepositoryPort, storageOptimizationService)
    }

    @Test
    fun `test optimize storage for items`() {
        // Arrange
        val itemIds = listOf(1L, 2L)
        val items = listOf(
            createInventoryItem(1L, sensitive = true),
            createInventoryItem(2L, sensitive = false)
        )
        val expectedLayout = StorageLayout(
            zones = listOf(
                StorageZone("SENSITIVE", listOf(items[0])),
                StorageZone("REGULAR", listOf(items[1]))
            )
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.of(items[0])
        every { inventoryRepositoryPort.findById(2L) } returns Optional.of(items[1])
        every { storageOptimizationService.optimizeStorageLayout(items) } returns expectedLayout

        // Act
        val result = service.optimizeStorageForItems(itemIds)

        // Assert
        assertEquals(expectedLayout, result)
        verify(exactly = 1) { storageOptimizationService.optimizeStorageLayout(items) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(2L) }
    }

    @Test
    fun `test check items compatibility`() {
        // Arrange
        val firstItem = createInventoryItem(1L, weight = 100.0)
        val secondItem = createInventoryItem(2L, weight = 20.0)
        val expectedCompatibility = StorageCompatibility(
            areCompatible = false,
            incompatibilityReasons = listOf("Weight difference too high for stacking")
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.of(firstItem)
        every { inventoryRepositoryPort.findById(2L) } returns Optional.of(secondItem)
        every {
            storageOptimizationService.checkStorageCompatibility(firstItem, secondItem)
        } returns expectedCompatibility

        // Act
        val result = service.checkItemsCompatibility(1L, 2L)

        // Assert
        assertEquals(expectedCompatibility, result)
        verify(exactly = 1) { storageOptimizationService.checkStorageCompatibility(firstItem, secondItem) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(2L) }
    }

    @Test
    fun `test calculate storage arrangement`() {
        // Arrange
        val itemId = 1L
        val item = createInventoryItem(itemId)
        val paletteCapacity = 100.0
        val expectedArrangement = StorageArrangement(
            itemsPerPalette = 10,
            requiredPalettes = 1,
            totalVolume = 50.0
        )

        every { inventoryRepositoryPort.findById(itemId) } returns Optional.of(item)
        every {
            storageOptimizationService.calculateStorageArrangement(item, paletteCapacity)
        } returns expectedArrangement

        // Act
        val result = service.calculateStorageArrangement(itemId, paletteCapacity)

        // Assert
        assertEquals(expectedArrangement, result)
        verify(exactly = 1) { storageOptimizationService.calculateStorageArrangement(item, paletteCapacity) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(itemId) }
    }

    @Test
    fun `test check items compatibility with non-existent item throws exception`() {
        // Arrange
        val firstItemId = 1L
        val secondItemId = 2L

        every { inventoryRepositoryPort.findById(firstItemId) } returns Optional.empty()

        // Act & Assert
        assertThrows<NoSuchElementException> {
            service.checkItemsCompatibility(firstItemId, secondItemId)
        }
        verify(exactly = 1) { inventoryRepositoryPort.findById(firstItemId) }
    }

    @Test
    fun `test optimize storage for empty items list returns empty layout`() {
        // Arrange
        val itemIds = listOf(1L)
        val expectedLayout = StorageLayout(emptyList())

        every { inventoryRepositoryPort.findById(1L) } returns Optional.empty()
        every { storageOptimizationService.optimizeStorageLayout(emptyList()) } returns expectedLayout

        // Act
        val result = service.optimizeStorageForItems(itemIds)

        // Assert
        assertEquals(expectedLayout, result)
        verify(exactly = 1) { storageOptimizationService.optimizeStorageLayout(emptyList()) }
    }

    private fun createInventoryItem(
        id: Long,
        sensitive: Boolean = false,
        weight: Double = 50.0
    ) = InventoryItem(
        id = id,
        name = "Test Item $id",
        quantity = 1,
        price = 100.0,
        dimensions = Dimensions(length = 2.0, width = 2.0, height = 2.0),
        weight = Weight(value = weight, unit = "KG"),
        packaging = Packaging(isSensitive = sensitive, packagingType = if (sensitive) "FRAGILE" else "STANDARD")
    )
}