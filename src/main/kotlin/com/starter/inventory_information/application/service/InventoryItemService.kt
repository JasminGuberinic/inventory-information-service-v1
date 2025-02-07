package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.InventoryItem

//This will enter our system (information or commends that will enter the system)
interface InventoryItemService {
    fun addInventoryItem(item: InventoryItem): InventoryItem
    fun getInventoryItemById(id: Long): InventoryItem?
    fun updateInventoryItem(id: Long, item: InventoryItem): InventoryItem
    fun removeInventoryItem(id: Long)
}