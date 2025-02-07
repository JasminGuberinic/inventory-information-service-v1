package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.service.BatchRiskAssessment
import com.starter.inventory_information.domain.service.HandlingRequirements
import com.starter.inventory_information.domain.service.RiskAssessment
import com.starter.inventory_information.domain.service.RiskAssessmentService
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import org.springframework.stereotype.Service

@Service
class RiskManagementService(
    private val inventoryRepositoryPort: InventoryRepositoryPort,
    private val riskAssessmentService: RiskAssessmentService
) {
    fun assessItemRisk(itemId: Long): RiskAssessment {
        val item = inventoryRepositoryPort.findById(itemId)
            .orElseThrow { NoSuchElementException("Item with id=$itemId not found") }

        return riskAssessmentService.assessItemRisk(item)
    }

    fun getHandlingRequirements(itemId: Long): HandlingRequirements {
        val item = inventoryRepositoryPort.findById(itemId)
            .orElseThrow { NoSuchElementException("Item with id=$itemId not found") }

        return riskAssessmentService.assessHandlingRequirements(item)
    }

    fun assessBatchRisk(itemIds: List<Long>): BatchRiskAssessment {
        val items = itemIds.mapNotNull { id ->
            inventoryRepositoryPort.findById(id).orElse(null)
        }

        if (items.isEmpty()) {
            throw IllegalArgumentException("No valid items found for batch risk assessment")
        }

        return riskAssessmentService.assessBatchRisk(items)
    }
}