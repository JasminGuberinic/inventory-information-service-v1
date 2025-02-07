package com.starter.inventory_information.adapters.outbound.database.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * InventoryItemEntity represents the database entity for an InventoryItem.
 * It lives in the adapter/out/persistence package to separate domain model from persistence details.
 */
@Entity
@Table(name = "inventory_item")
data class InventoryItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Embedded
    val dimensions: DimensionsEntity? = null,

    @Embedded
    val weight: WeightEntity? = null,

    @Embedded
    val packaging: PackagingEntity? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    val updatedAt: LocalDateTime? = null
)