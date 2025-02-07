package com.starter.inventory_information.adapters.inbound.web.controller

import StorageArrangement
import StorageCompatibility
import StorageLayout
import com.starter.inventory_information.application.service.StorageManagementService
import com.starter.inventory_information.ports.inbound.web.controllers.StorageManagementPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/storage")
class StorageManagementController(
    private val storageManagementService: StorageManagementService
) : StorageManagementPort<ResponseEntity<*>> {

    @PostMapping("/optimize")
    override fun optimizeStorageForItems(
        @RequestBody itemIds: List<Long>
    ): ResponseEntity<StorageLayout> {
        val layout = storageManagementService.optimizeStorageForItems(itemIds)
        return ResponseEntity.ok(layout)
    }

    @GetMapping("/compatibility")
    override fun checkItemsCompatibility(
        @RequestParam firstItemId: Long,
        @RequestParam secondItemId: Long
    ): ResponseEntity<StorageCompatibility> {
        val compatibility = storageManagementService.checkItemsCompatibility(firstItemId, secondItemId)
        return ResponseEntity.ok(compatibility)
    }

    @GetMapping("/arrangement/{itemId}")
    override fun calculateStorageArrangement(
        @PathVariable itemId: Long,
        @RequestParam paletteCapacity: Double
    ): ResponseEntity<StorageArrangement> {
        val arrangement = storageManagementService.calculateStorageArrangement(itemId, paletteCapacity)
        return ResponseEntity.ok(arrangement)
    }
}