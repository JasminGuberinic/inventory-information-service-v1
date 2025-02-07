package com.starter.inventory_information.adapters.inbound.web.controller

import com.starter.inventory_information.application.service.RiskManagementService
import com.starter.inventory_information.domain.service.BatchRiskAssessment
import com.starter.inventory_information.domain.service.HandlingRequirements
import com.starter.inventory_information.domain.service.RiskAssessment
import com.starter.inventory_information.ports.inbound.web.controllers.RiskManagementPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/risk")
class RiskManagementController(
    private val riskManagementService: RiskManagementService
) : RiskManagementPort<ResponseEntity<*>> {

    @GetMapping("/assess/{itemId}")
    override fun assessItemRisk(
        @PathVariable itemId: Long
    ): ResponseEntity<RiskAssessment> {
        val assessment = riskManagementService.assessItemRisk(itemId)
        return ResponseEntity.ok(assessment)
    }

    @GetMapping("/handling-requirements/{itemId}")
    override fun getHandlingRequirements(
        @PathVariable itemId: Long
    ): ResponseEntity<HandlingRequirements> {
        val requirements = riskManagementService.getHandlingRequirements(itemId)
        return ResponseEntity.ok(requirements)
    }

    @PostMapping("/batch-assessment")
    override fun assessBatchRisk(
        @RequestBody itemIds: List<Long>
    ): ResponseEntity<BatchRiskAssessment> {
        val assessment = riskManagementService.assessBatchRisk(itemIds)
        return ResponseEntity.ok(assessment)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }
}
