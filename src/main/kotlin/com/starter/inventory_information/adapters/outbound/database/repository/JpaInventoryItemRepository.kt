package com.starter.inventory_information.adapters.outbound.database.repository

import com.starter.inventory_information.adapters.outbound.database.entities.InventoryItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaInventoryItemRepository : JpaRepository<InventoryItemEntity, Long> {
    // Could add custom query methods here if needed, e.g. findByName, etc.
}