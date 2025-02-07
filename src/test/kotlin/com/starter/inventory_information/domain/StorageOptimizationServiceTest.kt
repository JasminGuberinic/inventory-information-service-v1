package com.starter.inventory_information.domain

import StorageOptimizationService
import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class StorageOptimizationServiceTest {

    private lateinit var service: StorageOptimizationService

    @BeforeEach
    fun setup() {
        service = StorageOptimizationService()
    }

    @Test
    fun `test optimize storage layout with mixed items`() {
        val items = listOf(
            createInventoryItem(sensitive = true, weight = 30.0),
            createInventoryItem(sensitive = false, weight = 60.0),
            createInventoryItem(sensitive = false, weight = 20.0)
        )

        val layout = service.optimizeStorageLayout(items)

        assertEquals(3, layout.zones.size)
        assertTrue(layout.zones.any { it.type == "SENSITIVE" })
        assertTrue(layout.zones.any { it.type == "HEAVY" })
        assertTrue(layout.zones.any { it.type == "LIGHT" })
    }

    @Test
    fun `test storage compatibility for incompatible items`() {
        val heavyItem = createInventoryItem(sensitive = false, weight = 100.0)
        val lightItem = createInventoryItem(sensitive = false, weight = 20.0)

        val compatibility = service.checkStorageCompatibility(heavyItem, lightItem)

        assertFalse(compatibility.areCompatible)
        assertTrue(compatibility.incompatibilityReasons.isNotEmpty())
    }
}