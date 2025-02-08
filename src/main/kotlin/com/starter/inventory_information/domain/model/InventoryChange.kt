package com.starter.inventory_information.domain.model

import java.time.LocalDateTime

data class InventoryChange(
    val itemId: Long,
    val locationCode: String,
    val oldQuantity: Int,
    val newQuantity: Int,
    val reason: String,
    val timestamp: String = LocalDateTime.now().toString()
)