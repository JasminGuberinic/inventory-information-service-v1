package com.starter.inventory_information.ports.inbound.web.dto

data class PackagingDTO(
    val inventoryItemId: Long,
    val isSensitive: Boolean,
    val packagingType: String
)