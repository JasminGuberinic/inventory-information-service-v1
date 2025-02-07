package com.starter.inventory_information.adapters.outbound.database.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class WeightEntity(
    @Column(name = "value", nullable = false)
    val value: Double,

    @Column(name = "unit", nullable = false)
    val unit: String = "kg"
)