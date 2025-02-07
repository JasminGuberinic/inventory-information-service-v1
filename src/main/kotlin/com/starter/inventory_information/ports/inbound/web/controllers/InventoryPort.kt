package com.starter.inventory_information.ports.inbound.web.controllers

import com.starter.inventory_information.ports.inbound.web.dto.InventoryItemDTO

interface InventoryPort<out R> {
    fun addItem(itemDTO: InventoryItemDTO): R
    fun getItem(id: Long): R
    fun updateItem(id: Long, itemDTO: InventoryItemDTO): R
    fun removeItem(id: Long): R
}