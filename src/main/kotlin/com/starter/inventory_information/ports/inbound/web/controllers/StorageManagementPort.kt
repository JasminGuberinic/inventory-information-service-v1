package com.starter.inventory_information.ports.inbound.web.controllers

interface StorageManagementPort<out R> {
    fun optimizeStorageForItems(itemIds: List<Long>): R
    fun checkItemsCompatibility(firstItemId: Long, secondItemId: Long): R
    fun calculateStorageArrangement(itemId: Long, paletteCapacity: Double): R
}