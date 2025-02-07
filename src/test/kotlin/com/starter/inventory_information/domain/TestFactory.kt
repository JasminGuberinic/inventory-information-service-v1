package com.starter.inventory_information.domain

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.InventoryItem
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight

fun createInventoryItem(
    sensitive: Boolean = false,
    weight: Double = 10.0,
    quantity: Int = 1,
    price: Double = 100.0
) = InventoryItem(
    id = 1L,
    name = "Test Item",
    quantity = quantity,
    price = price,
    dimensions = Dimensions(length = 2.0, width = 2.0, height = 2.0),
    weight = Weight(value = weight, unit = "KG"),
    packaging = Packaging(isSensitive = sensitive, packagingType = if (sensitive) "FRAGILE" else "STANDARD")
)