package com.starter.inventory_information.domain.model

import java.time.LocalDateTime

data class InventoryLevel(
    val itemId: Long,
    val availableQuantity: Int,
    val reservedQuantity: Int = 0,
    val locationCode: String,
    val lastUpdated: String = LocalDateTime.now().toString()
)