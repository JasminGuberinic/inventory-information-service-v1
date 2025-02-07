package com.starter.inventory_information.ports.inbound.web.controllers

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.ports.inbound.web.dto.DimensionsDTO
import com.starter.inventory_information.ports.inbound.web.dto.PackagingDTO
import com.starter.inventory_information.ports.inbound.web.dto.WeightDTO

//interface InventoryAttributesPort<T> {
//    fun addDimensions(dimensions: DimensionsDTO): T<Dimensions>
//    fun updateDimensions(id: Long, dimensions: DimensionsDTO): T<Dimensions>
//    fun addWeight(weight: WeightDTO): T<Weight>
//    fun updateWeight(id: Long, weight: WeightDTO): T<Weight>
//    fun addPackaging(packaging: PackagingDTO): T<Packaging>
//    fun updatePackaging(id: Long, packaging: PackagingDTO): T<Packaging>
//}

interface InventoryAttributesPort<out R> {
    fun addDimensions(dimensions: DimensionsDTO): R
    fun updateDimensions(id: Long, dimensions: DimensionsDTO): R
    fun addWeight(weight: WeightDTO): R
    fun updateWeight(id: Long, weight: WeightDTO): R
    fun addPackaging(packaging: PackagingDTO): R
    fun updatePackaging(id: Long, packaging: PackagingDTO): R
}