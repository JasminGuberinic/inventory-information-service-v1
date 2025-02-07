package com.starter.inventory_information.adapters.outbound.database.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class DimensionsEntity(
    @Column(name = "length", nullable = false)
    val length: Double,

    @Column(name = "width", nullable = false)
    val width: Double,

    @Column(name = "height", nullable = false)
    val height: Double
)