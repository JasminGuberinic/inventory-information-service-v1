package com.starter.inventory_information.ports.inbound.web.controllers

import com.starter.inventory_information.domain.model.UpdateQuantityRequest
import com.starter.inventory_information.ports.inbound.web.dto.InventoryLevelDTO

interface InventoryLevelPort<R> {
    fun getInventoryLevel(itemId: Long, locationCode: String): R
    fun saveInventoryLevel(request: InventoryLevelDTO): R
    fun updateInventoryQuantity(itemId: Long, locationCode: String, request: UpdateQuantityRequest): R
    fun deleteInventoryLevel(itemId: Long, locationCode: String): R
    fun getInventoryLevelsForLocation(locationCode: String): R
    fun getMultipleInventoryLevels(itemIds: List<Long>, locationCode: String): R
}