package com.starter.inventory_information.domain

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.domain.service.ShippingOptimizationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ShippingOptimizationServiceTest {

    private lateinit var service: ShippingOptimizationService

    @BeforeEach
    fun setup() {
        service = ShippingOptimizationService()
    }

    @Test
    fun `test calculate shipping arrangement`() {
        val items = listOf(
            createInventoryItem(weight = 60.0),
            createInventoryItem(weight = 40.0)
        )
        val maxCapacity = Weight(value = 80.0, unit = "KG")

        val arrangement = service.calculateShippingArrangement(items, maxCapacity)

        assertEquals(2, arrangement.numberOfShipments)
        assertEquals(100.0, arrangement.totalWeight)
    }

    @Test
    fun `test optimize loading sequence`() {
        val items = listOf(
            createInventoryItem(sensitive = true, weight = 30.0),
            createInventoryItem(sensitive = false, weight = 60.0),
            createInventoryItem(sensitive = false, weight = 20.0)
        )

        val loadingSequence = service.optimizeLoadingSequence(items)

        assertEquals(3, loadingSequence.sequence.size)
        assertEquals("HEAVY", loadingSequence.sequence[0].type)
        assertTrue(loadingSequence.estimatedLoadingTime > 0)
    }

    @Test
    fun `test estimate shipping costs`() {
        val items = listOf(
            createInventoryItem(sensitive = true, weight = 500.0),
            createInventoryItem(sensitive = false, weight = 500.0)
        )

        val estimate = service.estimateShippingCosts(
            items = items,
            distanceKm = 100.0,
            baseRatePerKm = 2.0
        )

        assertTrue(estimate.totalCost > estimate.baseCost)
        assertTrue(estimate.sensitivityCost > 0)
        assertTrue(estimate.weightCost > 0)
    }
}