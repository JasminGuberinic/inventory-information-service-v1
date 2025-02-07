package com.starter.inventory_information.ports.inbound.web.dto

data class DimensionsDTO(
    val inventoryItemId: Long,
    val length: Double,
    val width: Double,
    val height: Double
)