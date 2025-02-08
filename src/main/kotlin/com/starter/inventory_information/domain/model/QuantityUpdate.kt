package com.starter.inventory_information.domain.model

data class QuantityUpdate(
    val itemId: Long,
    val locationCode: String,
    val newQuantity: Int
)