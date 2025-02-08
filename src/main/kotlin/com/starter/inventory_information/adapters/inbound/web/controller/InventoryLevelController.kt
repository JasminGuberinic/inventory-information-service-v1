package com.starter.inventory_information.adapters.inbound.web.controller

import com.starter.inventory_information.application.service.InventoryService
import com.starter.inventory_information.domain.model.UpdateQuantityRequest
import com.starter.inventory_information.ports.inbound.web.controllers.InventoryLevelPort
import com.starter.inventory_information.ports.inbound.web.dto.InventoryLevelDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory-levels")
class InventoryLevelController(
    private val inventoryService: InventoryService
) : InventoryLevelPort<ResponseEntity<*>> {

    @GetMapping("/{itemId}")
    override fun getInventoryLevel(
        @PathVariable itemId: Long,
        @RequestParam locationCode: String
    ): ResponseEntity<InventoryLevelDTO> {
        return inventoryService.getInventoryLevel(itemId, locationCode)?.let {
            ResponseEntity.ok(InventoryLevelDTO.fromDomain(it))
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping
    override fun saveInventoryLevel(
        @RequestBody request: InventoryLevelDTO
    ): ResponseEntity<InventoryLevelDTO> {
        val savedLevel = inventoryService.saveInventoryLevel(request.toDomain())
        return ResponseEntity.ok(InventoryLevelDTO.fromDomain(savedLevel))
    }

    @PutMapping("/{itemId}/quantity")
    override fun updateInventoryQuantity(
        @PathVariable itemId: Long,
        @RequestParam locationCode: String,
        @RequestBody request: UpdateQuantityRequest
    ): ResponseEntity<InventoryLevelDTO> {
        return inventoryService.updateInventoryQuantity(itemId, locationCode, request.newQuantity)?.let {
            ResponseEntity.ok(InventoryLevelDTO.fromDomain(it))
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{itemId}")
    override fun deleteInventoryLevel(
        @PathVariable itemId: Long,
        @RequestParam locationCode: String
    ): ResponseEntity<Void> {
        inventoryService.deleteInventoryLevel(itemId, locationCode)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/location/{locationCode}")
    override fun getInventoryLevelsForLocation(
        @PathVariable locationCode: String
    ): ResponseEntity<List<InventoryLevelDTO>> {
        val levels = inventoryService.getInventoryLevelsForLocation(locationCode)
            .map { InventoryLevelDTO.fromDomain(it) }
        return ResponseEntity.ok(levels)
    }

    @GetMapping("/batch")
    override fun getMultipleInventoryLevels(
        @RequestParam itemIds: List<Long>,
        @RequestParam locationCode: String
    ): ResponseEntity<List<InventoryLevelDTO>> {
        val levels = inventoryService.getMultipleInventoryLevels(itemIds, locationCode)
            .map { InventoryLevelDTO.fromDomain(it) }
        return ResponseEntity.ok(levels)
    }
}