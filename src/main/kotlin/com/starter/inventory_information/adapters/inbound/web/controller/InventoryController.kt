package com.starter.inventory_information.adapters.inbound.web.controller

import com.starter.inventory_information.application.service.InventoryItemService
import com.starter.inventory_information.ports.inbound.web.controllers.InventoryPort
import com.starter.inventory_information.ports.inbound.web.dto.InventoryItemDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryItemService: InventoryItemService): InventoryPort<ResponseEntity<*>> {

    @PostMapping
    override fun addItem(@RequestBody itemDTO: InventoryItemDTO): ResponseEntity<InventoryItemDTO> {
        val item = itemDTO.toDomain()
        val addedItem = inventoryItemService.addInventoryItem(item)
        return ResponseEntity.ok(InventoryItemDTO.fromDomain(addedItem))
    }

    @GetMapping("/{id}")
    override fun getItem(@PathVariable id: Long): ResponseEntity<InventoryItemDTO> {
        val item = inventoryItemService.getInventoryItemById(id)
        return if (item != null) {
            ResponseEntity.ok(InventoryItemDTO.fromDomain(item))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    override fun updateItem(@PathVariable id: Long, @RequestBody itemDTO: InventoryItemDTO): ResponseEntity<InventoryItemDTO> {
        val item = itemDTO.toDomain()
        val updatedItem = inventoryItemService.updateInventoryItem(id, item)
        return ResponseEntity.ok(InventoryItemDTO.fromDomain(updatedItem))
    }

    @DeleteMapping("/{id}")
    override fun removeItem(@PathVariable id: Long): ResponseEntity<Void> {
        inventoryItemService.removeInventoryItem(id)
        return ResponseEntity.noContent().build()
    }
}