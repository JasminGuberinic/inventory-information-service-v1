package com.starter.inventory_information.domain.service

import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Weight

class ShippingOptimizationService {

    /**
     * Izračunava optimalni raspored za transport
     */
    fun calculateShippingArrangement(
        items: List<InventoryItem>,
        maxTruckCapacity: Weight
    ): ShippingArrangement {
        val sortedItems = items.sortedByDescending { it.calculateTotalWeight() ?: 0.0 }
        val shipmentGroups = createShipmentGroups(sortedItems, maxTruckCapacity)

        return ShippingArrangement(
            numberOfShipments = shipmentGroups.size,
            shipmentGroups = shipmentGroups,
            totalWeight = items.sumOf { it.calculateTotalWeight() ?: 0.0 }
        )
    }

    /**
     * Optimizuje raspored utovara za transport
     */
    fun optimizeLoadingSequence(
        items: List<InventoryItem>
    ): LoadingSequence {
        val sensitiveItems = items.filter { it.packaging?.isSensitive == true }
        val heavyItems = items.filter {
            !sensitiveItems.contains(it) &&
                    (it.weight?.value ?: 0.0) > 50.0
        }
        val regularItems = items.filter {
            !sensitiveItems.contains(it) &&
                    !heavyItems.contains(it)
        }

        return LoadingSequence(
            sequence = listOf(
                LoadingPhase("HEAVY", heavyItems),
                LoadingPhase("REGULAR", regularItems),
                LoadingPhase("SENSITIVE", sensitiveItems)
            ),
            estimatedLoadingTime = calculateEstimatedLoadingTime(items)
        )
    }

    /**
     * Procjenjuje transportne troškove
     */
    fun estimateShippingCosts(
        items: List<InventoryItem>,
        distanceKm: Double,
        baseRatePerKm: Double
    ): ShippingCostEstimate {
        val totalWeight = items.sumOf { it.calculateTotalWeight() ?: 0.0 }
        val weightFactor = totalWeight / 1000 // per ton
        val sensitivityFactor = if (items.any { it.packaging?.isSensitive == true }) 1.5 else 1.0

        val baseCost = distanceKm * baseRatePerKm
        val weightCost = weightFactor * baseCost
        val sensitivityCost = (baseCost + weightCost) * (sensitivityFactor - 1)

        return ShippingCostEstimate(
            baseCost = baseCost,
            weightCost = weightCost,
            sensitivityCost = sensitivityCost,
            totalCost = baseCost + weightCost + sensitivityCost
        )
    }

    private fun createShipmentGroups(
        sortedItems: List<InventoryItem>,
        maxTruckCapacity: Weight
    ): List<List<InventoryItem>> {
        var currentWeight = 0.0
        val shipmentGroups = mutableListOf<List<InventoryItem>>()
        var currentGroup = mutableListOf<InventoryItem>()

        for (item in sortedItems) {
            val itemTotalWeight = item.calculateTotalWeight() ?: 0.0

            if (currentWeight + itemTotalWeight > maxTruckCapacity.value) {
                if (currentGroup.isNotEmpty()) {
                    shipmentGroups.add(currentGroup.toList())
                }
                currentGroup = mutableListOf()
                currentWeight = 0.0
            }

            currentGroup.add(item)
            currentWeight += itemTotalWeight
        }

        if (currentGroup.isNotEmpty()) {
            shipmentGroups.add(currentGroup)
        }

        return shipmentGroups
    }

    private fun calculateEstimatedLoadingTime(items: List<InventoryItem>): Int {
        val baseTimePerItem = 5 // minutes
        return items.sumOf { item ->
            when {
                item.packaging?.isSensitive == true -> baseTimePerItem * 3
                item.weight?.value ?: 0.0 > 50.0 -> baseTimePerItem * 2
                else -> baseTimePerItem
            }
        }
    }
}

data class LoadingSequence(
    val sequence: List<LoadingPhase>,
    val estimatedLoadingTime: Int // in minutes
)

data class LoadingPhase(
    val type: String,
    val items: List<InventoryItem>
)

data class ShippingCostEstimate(
    val baseCost: Double,
    val weightCost: Double,
    val sensitivityCost: Double,
    val totalCost: Double
)

data class ShippingArrangement(
    val numberOfShipments: Int,
    val shipmentGroups: List<List<InventoryItem>>,
    val totalWeight: Double
)