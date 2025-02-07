package com.starter.inventory_information.domain.model

data class Dimensions(
    val length: Double,
    val width: Double,
    val height: Double
) {
    fun calculateVolume(): Double {
        return length * width * height
    }
}