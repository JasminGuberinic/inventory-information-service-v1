package com.starter.inventory_information.domain.model

data class InventoryLevelKey(
    val itemId: Long,
    val locationCode: String
) {
    fun asString() = "inventory:$itemId:$locationCode"
}