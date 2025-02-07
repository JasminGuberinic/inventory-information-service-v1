package com.starter.inventory_information.adapters.outbound.database.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class PackagingEntity(
    @Column(name = "is_sensitive", nullable = false)
    val isSensitive: Boolean,

    @Column(name = "packaging_type", nullable = false)
    val packagingType: String
)