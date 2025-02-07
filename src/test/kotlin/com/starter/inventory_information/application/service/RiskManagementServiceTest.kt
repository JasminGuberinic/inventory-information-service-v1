package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.InventoryItem
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

class RiskManagementServiceTest {

    private lateinit var inventoryRepositoryPort: InventoryRepositoryPort
    private lateinit var riskAssessmentService: RiskAssessmentService
    private lateinit var service: RiskManagementService

    @BeforeEach
    fun setup() {
        inventoryRepositoryPort = mockk()
        riskAssessmentService = mockk()
        service = RiskManagementService(inventoryRepositoryPort, riskAssessmentService)
    }

    @Test
    fun `test assess item risk`() {
        // Arrange
        val itemId = 1L
        val item = createInventoryItem(itemId, sensitive = true, weight = 150.0)
        val expectedAssessment = RiskAssessment(
            weightRisk = RiskLevel.HIGH,
            packagingRisk = RiskLevel.HIGH,
            quantityRisk = RiskLevel.LOW,
            valueRisk = RiskLevel.LOW,
            overallRisk = RiskLevel.HIGH
        )

        every { inventoryRepositoryPort.findById(itemId) } returns Optional.of(item)
        every { riskAssessmentService.assessItemRisk(item) } returns expectedAssessment

        // Act
        val result = service.assessItemRisk(itemId)

        // Assert
        assertEquals(expectedAssessment, result)
        verify(exactly = 1) { riskAssessmentService.assessItemRisk(item) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(itemId) }
    }

    @Test
    fun `test get handling requirements`() {
        // Arrange
        val itemId = 1L
        val item = createInventoryItem(itemId, sensitive = true, weight = 150.0)
        val expectedRequirements = HandlingRequirements(
            requiresSpecialEquipment = true,
            requiresTrainedPersonnel = true,
            handlingInstructions = listOf("Use mechanical lifting equipment", "Handle with extreme care"),
            safetyMeasures = listOf("Wear protective equipment", "Follow two-person handling protocol")
        )

        every { inventoryRepositoryPort.findById(itemId) } returns Optional.of(item)
        every { riskAssessmentService.assessHandlingRequirements(item) } returns expectedRequirements

        // Act
        val result = service.getHandlingRequirements(itemId)

        // Assert
        assertEquals(expectedRequirements, result)
        verify(exactly = 1) { riskAssessmentService.assessHandlingRequirements(item) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(itemId) }
    }

    @Test
    fun `test assess batch risk`() {
        // Arrange
        val itemIds = listOf(1L, 2L)
        val items = listOf(
            createInventoryItem(1L, sensitive = true, weight = 150.0, price = 2000.0),
            createInventoryItem(2L, sensitive = false, weight = 30.0, price = 500.0)
        )
        val expectedAssessment = BatchRiskAssessment(
            numberOfItems = 2,
            highRiskItems = 1,
            totalValue = 2500.0,
            requiresInsurance = true
        )

        every { inventoryRepositoryPort.findById(1L) } returns Optional.of(items[0])
        every { inventoryRepositoryPort.findById(2L) } returns Optional.of(items[1])
        every { riskAssessmentService.assessBatchRisk(items) } returns expectedAssessment

        // Act
        val result = service.assessBatchRisk(itemIds)

        // Assert
        assertEquals(expectedAssessment, result)
        verify(exactly = 1) { riskAssessmentService.assessBatchRisk(items) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
        verify(exactly = 1) { inventoryRepositoryPort.findById(2L) }
    }

    @Test
    fun `test assess item risk for non-existent item throws exception`() {
        // Arrange
        val itemId = 1L
        every { inventoryRepositoryPort.findById(itemId) } returns Optional.empty()

        // Act & Assert
        assertThrows<NoSuchElementException> {
            service.assessItemRisk(itemId)
        }
        verify(exactly = 1) { inventoryRepositoryPort.findById(itemId) }
    }

    @Test
    fun `test assess batch risk with empty items throws exception`() {
        // Arrange
        val itemIds = listOf(1L)
        every { inventoryRepositoryPort.findById(1L) } returns Optional.empty()

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            service.assessBatchRisk(itemIds)
        }
        verify(exactly = 1) { inventoryRepositoryPort.findById(1L) }
    }

    private fun createInventoryItem(
        id: Long,
        sensitive: Boolean = false,
        weight: Double = 50.0,
        price: Double = 100.0
    ) = InventoryItem(
        id = id,
        name = "Test Item $id",
        quantity = 1,
        price = price,
        dimensions = null,
        weight = Weight(value = weight, unit = "KG"),
        packaging = Packaging(isSensitive = sensitive, packagingType = if (sensitive) "FRAGILE" else "STANDARD")
    )
}