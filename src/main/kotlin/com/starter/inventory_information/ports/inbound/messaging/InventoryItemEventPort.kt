package com.starter.inventory_information.ports.inbound.messaging

interface InventoryItemEventPort {
    fun listenInventoryItemEvent(message: String)
}