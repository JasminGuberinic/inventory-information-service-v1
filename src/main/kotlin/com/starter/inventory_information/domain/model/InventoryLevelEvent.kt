package com.starter.inventory_information.domain.model

import java.time.LocalDateTime

sealed class InventoryLevelEvent {
    data class QuantityChanged(
        val itemId: Long,
        val locationCode: String,
        val previousQuantity: Int,
        val newQuantity: Int,
        val changeType: ChangeType,
        val timestamp: String = LocalDateTime.now().toString()
    )

    data class StockReserved(
        val itemId: Long,
        val locationCode: String,
        val quantity: Int,
        val timestamp: String = LocalDateTime.now().toString()
    )

    enum class ChangeType {
        ADDITION, REMOVAL, ADJUSTMENT
    }
}