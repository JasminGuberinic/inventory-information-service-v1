package com.starter.inventory_information.domain.service

import com.starter.inventory_information.domain.model.InventoryItem

class RiskAssessmentService {

    /**
     * Procjenjuje sveukupni rizik za proizvod
     */
    fun assessItemRisk(item: InventoryItem): RiskAssessment {
        val weightRisk = assessWeightRisk(item)
        val packagingRisk = assessPackagingRisk(item)
        val quantityRisk = assessQuantityRisk(item)
        val valueRisk = assessValueRisk(item)

        return RiskAssessment(
            weightRisk = weightRisk,
            packagingRisk = packagingRisk,
            quantityRisk = quantityRisk,
            valueRisk = valueRisk,
            overallRisk = calculateOverallRisk(weightRisk, packagingRisk, quantityRisk, valueRisk)
        )
    }

    /**
     * Procjenjuje sigurnosne zahtjeve za rukovanje
     */
    fun assessHandlingRequirements(item: InventoryItem): HandlingRequirements {
        val riskAssessment = assessItemRisk(item)

        return HandlingRequirements(
            requiresSpecialEquipment = item.weight?.value ?: 0.0 > 50.0,
            requiresTrainedPersonnel = riskAssessment.overallRisk == RiskLevel.HIGH,
            handlingInstructions = generateHandlingInstructions(item, riskAssessment),
            safetyMeasures = generateSafetyMeasures(riskAssessment)
        )
    }

    /**
     * Procjenjuje rizik za grupu proizvoda
     */
    fun assessBatchRisk(items: List<InventoryItem>): BatchRiskAssessment {
        val individualAssessments = items.map { assessItemRisk(it) }
        val totalValue = items.sumOf { it.price }

        return BatchRiskAssessment(
            numberOfItems = items.size,
            highRiskItems = individualAssessments.count { it.overallRisk == RiskLevel.HIGH },
            totalValue = totalValue,
            requiresInsurance = totalValue > 10000 || individualAssessments.any { it.overallRisk == RiskLevel.HIGH }
        )
    }

    private fun assessWeightRisk(item: InventoryItem): RiskLevel = when {
        item.weight == null -> RiskLevel.UNKNOWN
        item.weight!!.value > 100 -> RiskLevel.HIGH
        item.weight!!.value > 50 -> RiskLevel.MEDIUM
        else -> RiskLevel.LOW
    }

    private fun assessPackagingRisk(item: InventoryItem): RiskLevel = when {
        item.packaging == null -> RiskLevel.UNKNOWN
        item.packaging!!.isSensitive -> RiskLevel.HIGH
        else -> RiskLevel.LOW
    }

    private fun assessQuantityRisk(item: InventoryItem): RiskLevel = when {
        item.quantity > 1000 -> RiskLevel.HIGH
        item.quantity > 100 -> RiskLevel.MEDIUM
        else -> RiskLevel.LOW
    }

    private fun assessValueRisk(item: InventoryItem): RiskLevel = when {
        item.price > 1000 -> RiskLevel.HIGH
        item.price > 100 -> RiskLevel.MEDIUM
        else -> RiskLevel.LOW
    }

    private fun calculateOverallRisk(vararg risks: RiskLevel): RiskLevel = when {
        risks.any { it == RiskLevel.HIGH } -> RiskLevel.HIGH
        risks.any { it == RiskLevel.UNKNOWN } -> RiskLevel.UNKNOWN
        risks.any { it == RiskLevel.MEDIUM } -> RiskLevel.MEDIUM
        else -> RiskLevel.LOW
    }

    private fun generateHandlingInstructions(item: InventoryItem, assessment: RiskAssessment): List<String> {
        val instructions = mutableListOf<String>()

        if (assessment.weightRisk == RiskLevel.HIGH) {
            instructions.add("Use mechanical lifting equipment")
        }
        if (assessment.packagingRisk == RiskLevel.HIGH) {
            instructions.add("Handle with extreme care")
            instructions.add("Keep upright at all times")
        }
        if (assessment.valueRisk == RiskLevel.HIGH) {
            instructions.add("Requires supervisor oversight")
        }

        return instructions
    }

    private fun generateSafetyMeasures(assessment: RiskAssessment): List<String> {
        val measures = mutableListOf<String>()

        if (assessment.overallRisk == RiskLevel.HIGH) {
            measures.add("Wear protective equipment")
            measures.add("Follow two-person handling protocol")
        }
        if (assessment.weightRisk != RiskLevel.LOW) {
            measures.add("Use back support when lifting")
        }

        return measures
    }
}
data class RiskAssessment(
    val weightRisk: RiskLevel,
    val packagingRisk: RiskLevel,
    val quantityRisk: RiskLevel,
    val overallRisk: RiskLevel,
    val valueRisk: RiskLevel
)

data class HandlingRequirements(
    val requiresSpecialEquipment: Boolean,
    val requiresTrainedPersonnel: Boolean,
    val handlingInstructions: List<String>,
    val safetyMeasures: List<String>
)

data class BatchRiskAssessment(
    val numberOfItems: Int,
    val highRiskItems: Int,
    val totalValue: Double,
    val requiresInsurance: Boolean
)

enum class RiskLevel {
    LOW, MEDIUM, HIGH, UNKNOWN
}