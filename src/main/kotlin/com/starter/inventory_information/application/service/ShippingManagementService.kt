package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.service.LoadingSequence
import com.starter.inventory_information.domain.service.ShippingArrangement
import com.starter.inventory_information.domain.service.ShippingCostEstimate
import com.starter.inventory_information.domain.service.ShippingOptimizationService
import com.starter.inventory_information.ports.outbound.database.InventoryRepositoryPort
import org.springframework.stereotype.Service

@Service
class ShippingManagementService(
    private val inventoryRepositoryPort: InventoryRepositoryPort,
    private val shippingOptimizationService: ShippingOptimizationService
) {
    fun calculateShippingArrangement(itemIds: List<Long>, maxTruckCapacity: Weight): ShippingArrangement {
        val items = itemIds.mapNotNull { id ->
            inventoryRepositoryPort.findById(id).orElse(null)
        }

        if (items.isEmpty()) {
            throw IllegalArgumentException("No valid items found for shipping arrangement")
        }

        return shippingOptimizationService.calculateShippingArrangement(items, maxTruckCapacity)
    }

    fun optimizeLoadingSequence(itemIds: List<Long>): LoadingSequence {
        val items = itemIds.mapNotNull { id ->
            inventoryRepositoryPort.findById(id).orElse(null)
        }

        if (items.isEmpty()) {
            throw IllegalArgumentException("No valid items found for loading sequence optimization")
        }

        return shippingOptimizationService.optimizeLoadingSequence(items)
    }

    fun estimateShippingCosts(
        itemIds: List<Long>,
        distanceKm: Double,
        baseRatePerKm: Double
    ): ShippingCostEstimate {
        val items = itemIds.mapNotNull { id ->
            inventoryRepositoryPort.findById(id).orElse(null)
        }

        if (items.isEmpty()) {
            throw IllegalArgumentException("No valid items found for shipping cost estimation")
        }

        return shippingOptimizationService.estimateShippingCosts(
            items,
            distanceKm,
            baseRatePerKm
        )
    }
}