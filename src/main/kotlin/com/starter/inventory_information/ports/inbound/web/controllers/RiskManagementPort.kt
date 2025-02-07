package com.starter.inventory_information.ports.inbound.web.controllers

interface RiskManagementPort<out R> {
    fun assessItemRisk(itemId: Long): R
    fun getHandlingRequirements(itemId: Long): R
    fun assessBatchRisk(itemIds: List<Long>): R
}