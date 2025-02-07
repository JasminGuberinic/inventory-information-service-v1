package com.starter.inventory_information.ports.inbound.web.controllers

interface ShippingManagementPort<out R> {
    fun calculateShippingArrangement(itemIds: List<Long>, maxCapacityValue: Double, maxCapacityUnit: String): R
    fun optimizeLoadingSequence(itemIds: List<Long>): R
    fun estimateShippingCosts(itemIds: List<Long>, distanceKm: Double, baseRatePerKm: Double): R
}