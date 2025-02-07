package com.starter.inventory_information.ports.inbound.web.dto

data class WeightDTO(
    val inventoryItemId: Long,
    val value: Double,
    val unit: String = "kg"
)