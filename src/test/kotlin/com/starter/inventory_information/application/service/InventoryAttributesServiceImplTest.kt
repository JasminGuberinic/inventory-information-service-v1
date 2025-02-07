package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.ports.outbound.database.DimensionsRepositoryPort
import com.starter.inventory_information.ports.outbound.database.WeightRepositoryPort
import com.starter.inventory_information.ports.outbound.database.PackagingRepositoryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class InventoryAttributesServiceImplTest {

    private lateinit var dimensionsRepo: DimensionsRepositoryPort
    private lateinit var weightRepo: WeightRepositoryPort
    private lateinit var packagingRepo: PackagingRepositoryPort
    private lateinit var service: InventoryAttributesServiceImpl

    @BeforeEach
    fun setup() {
        dimensionsRepo = mockk()
        weightRepo = mockk()
        packagingRepo = mockk()
        service = InventoryAttributesServiceImpl(dimensionsRepo, packagingRepo, weightRepo)
    }

    @Test
    fun `test add dimensions`() {
        // Arrange
        val inventoryItemId = 1L
        val dimensions = Dimensions(length = 10.0, width = 5.0, height = 3.0)
        every { dimensionsRepo.save(inventoryItemId, dimensions) } returns dimensions

        // Act
        val result = service.addDimensions(inventoryItemId, dimensions)

        // Assert
        assertEquals(dimensions, result)
        verify(exactly = 1) { dimensionsRepo.save(inventoryItemId, dimensions) }
    }

    @Test
    fun `test update dimensions`() {
        // Arrange
        val inventoryItemId = 1L
        val dimensions = Dimensions(length = 10.0, width = 5.0, height = 3.0)
        every { dimensionsRepo.update(inventoryItemId, dimensions) } returns dimensions

        // Act
        val result = service.updateDimensions(inventoryItemId, dimensions)

        // Assert
        assertEquals(dimensions, result)
        verify(exactly = 1) { dimensionsRepo.update(inventoryItemId, dimensions) }
    }

    @Test
    fun `test add weight`() {
        // Arrange
        val inventoryItemId = 1L
        val weight = Weight(value = 2.5, unit = "KG")
        every { weightRepo.save(inventoryItemId, weight) } returns weight

        // Act
        val result = service.addWeight(inventoryItemId, weight)

        // Assert
        assertEquals(weight, result)
        verify(exactly = 1) { weightRepo.save(inventoryItemId, weight) }
    }

    @Test
    fun `test update weight`() {
        // Arrange
        val inventoryItemId = 1L
        val weight = Weight(value = 2.5, unit = "KG")
        every { weightRepo.update(inventoryItemId, weight) } returns weight

        // Act
        val result = service.updateWeight(inventoryItemId, weight)

        // Assert
        assertEquals(weight, result)
        verify(exactly = 1) { weightRepo.update(inventoryItemId, weight) }
    }

    @Test
    fun `test add packaging`() {
        // Arrange
        val inventoryItemId = 1L
        val packaging = Packaging(isSensitive = true, packagingType = "FRAGILE")
        every { packagingRepo.save(inventoryItemId, packaging) } returns packaging

        // Act
        val result = service.addPackaging(inventoryItemId, packaging)

        // Assert
        assertEquals(packaging, result)
        verify(exactly = 1) { packagingRepo.save(inventoryItemId, packaging) }
    }

    @Test
    fun `test update packaging`() {
        // Arrange
        val inventoryItemId = 1L
        val packaging = Packaging(isSensitive = true, packagingType = "FRAGILE")
        every { packagingRepo.update(inventoryItemId, packaging) } returns packaging

        // Act
        val result = service.updatePackaging(inventoryItemId, packaging)

        // Assert
        assertEquals(packaging, result)
        verify(exactly = 1) { packagingRepo.update(inventoryItemId, packaging) }
    }

    @Test
    fun `test add dimensions throws exception`() {
        // Arrange
        val inventoryItemId = 1L
        val dimensions = Dimensions(length = 10.0, width = 5.0, height = 3.0)
        every { dimensionsRepo.save(inventoryItemId, dimensions) } throws RuntimeException()

        // Act & Assert
        assertThrows<RuntimeException> {
            service.addDimensions(inventoryItemId, dimensions)
        }
        verify(exactly = 1) { dimensionsRepo.save(inventoryItemId, dimensions) }
    }

    @Test
    fun `test update weight throws exception`() {
        // Arrange
        val inventoryItemId = 1L
        val weight = Weight(value = 2.5, unit = "KG")
        every { weightRepo.update(inventoryItemId, weight) } throws RuntimeException()

        // Act & Assert
        assertThrows<RuntimeException> {
            service.updateWeight(inventoryItemId, weight)
        }
        verify(exactly = 1) { weightRepo.update(inventoryItemId, weight) }
    }
}