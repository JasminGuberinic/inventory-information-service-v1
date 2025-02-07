package com.starter.inventory_information.application.service

import com.starter.inventory_information.domain.model.Dimensions
import com.starter.inventory_information.domain.model.Packaging
import com.starter.inventory_information.domain.model.Weight
import com.starter.inventory_information.ports.outbound.database.DimensionsRepositoryPort
import com.starter.inventory_information.ports.outbound.database.PackagingRepositoryPort
import com.starter.inventory_information.ports.outbound.database.WeightRepositoryPort
import org.springframework.stereotype.Service

@Service
class InventoryAttributesServiceImpl(
    private val dimensionsRepo: DimensionsRepositoryPort,
    private val packagingRepo: PackagingRepositoryPort,
    private val weightRepo: WeightRepositoryPort
) : inventoryAttributesService {

    override fun addDimensions(id: Long, dimensions: Dimensions): Dimensions {
        return dimensionsRepo.save(id, dimensions)
    }

    override fun updateDimensions(id: Long, dimensions: Dimensions): Dimensions {
        return dimensionsRepo.update(id, dimensions)
    }

    override fun addWeight(id: Long, weight: Weight): Weight {
        return weightRepo.save(id, weight)
    }

    override fun updateWeight(id: Long, weight: Weight): Weight {
        return weightRepo.update(id, weight)
    }

    override fun addPackaging(id: Long, packaging: Packaging): Packaging {
        return packagingRepo.save(id, packaging)
    }

    override fun updatePackaging(id: Long, packaging: Packaging): Packaging {
        return packagingRepo.update(id, packaging)
    }
}