package com.starter.inventory_information.domain

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.service.RiskAssessmentService
import com.starter.inventory_information.domain.service.RiskLevel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RiskAssessmentServiceTest {

    private lateinit var service: RiskAssessmentService

    @BeforeEach
    fun setup() {
        service = RiskAssessmentService()
    }

    @Test
    fun `test assess high risk item`() {
        val item = createInventoryItem(
            sensitive = true,
            weight = 150.0,
            quantity = 1500,
            price = 1500.0
        )

        val assessment = service.assessItemRisk(item)

        assertEquals(RiskLevel.HIGH, assessment.overallRisk)
        assertEquals(RiskLevel.HIGH, assessment.weightRisk)
        assertEquals(RiskLevel.HIGH, assessment.packagingRisk)
        assertEquals(RiskLevel.HIGH, assessment.quantityRisk)
    }

    @Test
    fun `test handling requirements for high risk item`() {
        val item = createInventoryItem(
            sensitive = true,
            weight = 150.0,
            quantity = 1,
            price = 2000.0
        )

        val requirements = service.assessHandlingRequirements(item)

        assertTrue(requirements.requiresSpecialEquipment)
        assertTrue(requirements.requiresTrainedPersonnel)
        assertTrue(requirements.handlingInstructions.isNotEmpty())
        assertTrue(requirements.safetyMeasures.isNotEmpty())
    }

    @Test
    fun `test batch risk assessment`() {
        val items = listOf(
            createInventoryItem(sensitive = true, weight = 150.0, price = 2000.0),
            createInventoryItem(sensitive = false, weight = 30.0, price = 500.0)
        )

        val batchAssessment = service.assessBatchRisk(items)

        assertEquals(2, batchAssessment.numberOfItems)
        assertEquals(1, batchAssessment.highRiskItems)
        assertTrue(batchAssessment.requiresInsurance)
    }
}