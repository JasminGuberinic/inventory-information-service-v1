package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight

interface inventoryAttributesService {
    fun addDimensions(id: Long, dimensions: Dimensions): Dimensions
    fun updateDimensions(id: Long, dimensions: Dimensions): Dimensions
    fun addWeight(id: Long, weight: Weight): Weight
    fun updateWeight(id: Long, weight: Weight): Weight
    fun addPackaging(id: Long, packaging: Packaging): Packaging
    fun updatePackaging(id: Long, packaging: Packaging): Packaging
}