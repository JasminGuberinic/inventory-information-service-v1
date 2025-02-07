package com.starter.inventory_information.ports.outbound.database

import com.starter.inventory_information.domain.model.Packaging
import java.util.*

interface PackagingRepositoryPort {
    fun save(inventoryItemId: Long, packaging: Packaging): Packaging
    fun findByInventoryItemId(inventoryItemId: Long): Optional<Packaging>
    fun update(id: Long, packaging: Packaging): Packaging
    fun delete(id: Long)
}