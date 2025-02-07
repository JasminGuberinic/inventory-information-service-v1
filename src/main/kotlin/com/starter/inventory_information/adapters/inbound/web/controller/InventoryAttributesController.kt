package com.starter.inventory_information.adapters.inbound.web.controller

import com.starter.inventory_information.application.service.InventoryAttributesServiceImpl
import com.starter.inventory_information.domain.model.*
import com.starter.inventory_information.ports.inbound.web.controllers.InventoryAttributesPort
import com.starter.inventory_information.ports.inbound.web.dto.DimensionsDTO
import com.starter.inventory_information.ports.inbound.web.dto.PackagingDTO
import com.starter.inventory_information.ports.inbound.web.dto.WeightDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/inventory/attributes")
class InventoryAttributesController(private val inventoryAttributesServiceImpl: InventoryAttributesServiceImpl) :
    InventoryAttributesPort<ResponseEntity<*>> {

    @PostMapping("/dimensions")
    override fun addDimensions(@RequestBody dimensions: DimensionsDTO): ResponseEntity<Dimensions> {
        val addedDimensions = inventoryAttributesServiceImpl.addDimensions(dimensions.inventoryItemId, dimensions.toDomain())
        return ResponseEntity.ok(addedDimensions)
    }

    @PutMapping("/dimensions/{id}")
    override fun updateDimensions(@PathVariable id: Long, @RequestBody dimensions: DimensionsDTO): ResponseEntity<Dimensions> {
        val updatedDimensions = inventoryAttributesServiceImpl.updateDimensions(id, dimensions.toDomain())
        return ResponseEntity.ok(updatedDimensions)
    }

    @PostMapping("/weight")
    override fun addWeight(@RequestBody weight: WeightDTO): ResponseEntity<Weight> {
        val addedWeight = inventoryAttributesServiceImpl.addWeight(weight.inventoryItemId!!, weight.toDomain())
        return ResponseEntity.ok(addedWeight)
    }

    @PutMapping("/weight/{id}")
    override fun updateWeight(@PathVariable id: Long, @RequestBody weight: WeightDTO): ResponseEntity<Weight> {
        val updatedWeight = inventoryAttributesServiceImpl.updateWeight(id, weight.toDomain())
        return ResponseEntity.ok(updatedWeight)
    }

    @PostMapping("/packaging")
    override fun addPackaging(@RequestBody packaging: PackagingDTO): ResponseEntity<Packaging> {
        val addedPackaging = inventoryAttributesServiceImpl.addPackaging(packaging.inventoryItemId!!, packaging.toDomain())
        return ResponseEntity.ok(addedPackaging)
    }

    @PutMapping("/packaging/{id}")
    override fun updatePackaging(@PathVariable id: Long, @RequestBody packaging: PackagingDTO): ResponseEntity<Packaging> {
        val updatedPackaging = inventoryAttributesServiceImpl.updatePackaging(id, packaging.toDomain())
        return ResponseEntity.ok(updatedPackaging)
    }
}

// Extension functions to convert DTOs to domain models
fun DimensionsDTO.toDomain() = Dimensions(length, width, height)
fun WeightDTO.toDomain() = Weight(value, unit)
fun PackagingDTO.toDomain() = Packaging(isSensitive, packagingType)