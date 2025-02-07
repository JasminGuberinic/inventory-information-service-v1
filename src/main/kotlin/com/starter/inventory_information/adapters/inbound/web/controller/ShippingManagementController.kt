package com.starter.inventory_information.adapters.inbound.web.controller

import com.starter.inventory_information.application.service.ShippingManagementService
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.service.LoadingSequence
import com.starter.inventory_information.domain.service.ShippingArrangement
import com.starter.inventory_information.domain.service.ShippingCostEstimate
import com.starter.inventory_information.ports.inbound.web.controllers.ShippingManagementPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shipping")
class ShippingManagementController(
    private val shippingManagementService: ShippingManagementService
) : ShippingManagementPort<ResponseEntity<*>> {

    @PostMapping("/arrangement")
    override fun calculateShippingArrangement(
        @RequestBody itemIds: List<Long>,
        @RequestParam maxCapacityValue: Double,
        @RequestParam maxCapacityUnit: String
    ): ResponseEntity<ShippingArrangement> {
        val maxTruckCapacity = Weight(maxCapacityValue, maxCapacityUnit)
        val arrangement = shippingManagementService.calculateShippingArrangement(itemIds, maxTruckCapacity)
        return ResponseEntity.ok(arrangement)
    }

    @PostMapping("/loading-sequence")
    override fun optimizeLoadingSequence(
        @RequestBody itemIds: List<Long>
    ): ResponseEntity<LoadingSequence> {
        val sequence = shippingManagementService.optimizeLoadingSequence(itemIds)
        return ResponseEntity.ok(sequence)
    }

    @PostMapping("/cost-estimate")
    override fun estimateShippingCosts(
        @RequestBody itemIds: List<Long>,
        @RequestParam distanceKm: Double,
        @RequestParam baseRatePerKm: Double
    ): ResponseEntity<ShippingCostEstimate> {
        val estimate = shippingManagementService.estimateShippingCosts(
            itemIds,
            distanceKm,
            baseRatePerKm
        )
        return ResponseEntity.ok(estimate)
    }
}