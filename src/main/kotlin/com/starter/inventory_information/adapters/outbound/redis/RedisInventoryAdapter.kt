package com.starter.inventory_information.adapters.outbound.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.starter.inventory_information.adapters.outbound.database.repository.InventoryRepository
import com.starter.inventory_information.adapters.outbound.database.repository.toEntity
import com.starter.inventory_information.adapters.outbound.messaging.InventoryItemEventProducer
import com.starter.inventory_information.domain.model.*
import com.starter.inventory_information.ports.outbound.cache.InventoryCachePort
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RedisInventoryAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val inventoryItemEventProducer: InventoryItemEventProducer,
    private val inventoryRepository: InventoryRepository
): InventoryCachePort {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun saveInventoryLevel(level: InventoryLevel): InventoryLevel {
        val key = InventoryLevelKey(level.itemId, level.locationCode).asString()
        val valueAsString = objectMapper.writeValueAsString(level)
        logger.debug("Saving inventory level: {} with key: {}", level, key)
        redisTemplate.opsForValue().set(key, valueAsString)
        return inventoryRepository.save(level.toEntity()).toDomain()
    }

    override fun getInventoryLevel(itemId: Long, locationCode: String): InventoryLevel? {
        val key = InventoryLevelKey(itemId, locationCode).asString()
        logger.debug("Retrieving inventory level for key: $key")
        return redisTemplate.opsForValue().get(key)?.let { value ->
            objectMapper.readValue(value)
        }?: // If not in Redis, try from DB and cache it
        inventoryRepository.findByItemIdAndLocationCode(itemId, locationCode)?.let { entity ->
            val level = entity.toDomain()
            // Cache in Redis
            saveInventoryLevel(level)
            level
        }
    }

    override fun updateAvailableQuantity(itemId: Long, locationCode: String, newQuantity: Int): InventoryLevel? {
        return getInventoryLevel(itemId, locationCode)?.let { currentLevel ->
            val updatedLevel = currentLevel.copy(
                availableQuantity = newQuantity,
                lastUpdated = LocalDateTime.now().toString()
            )

            // Update both Redis and DB
            saveInventoryLevel(updatedLevel)

            // Publish event using existing producer
            val message = createQuantityChangedMessage(
                itemId,
                locationCode,
                currentLevel.availableQuantity,
                newQuantity
            )
            // Publish event
            inventoryItemEventProducer.sendInventoryItemUpdatedEvent(itemId)

            updatedLevel
        }
    }

    private fun createQuantityChangedMessage(
        itemId: Long,
        locationCode: String,
        oldQuantity: Int,
        newQuantity: Int
    ): String {
        val event = InventoryLevelEvent.QuantityChanged(
            itemId = itemId,
            locationCode = locationCode,
            previousQuantity = oldQuantity,
            newQuantity = newQuantity,
            changeType = determineChangeType(oldQuantity, newQuantity)
        )
        return objectMapper.writeValueAsString(event)
    }

    private fun determineChangeType(oldQuantity: Int, newQuantity: Int): InventoryLevelEvent.ChangeType {
        return when {
            newQuantity > oldQuantity -> InventoryLevelEvent.ChangeType.ADDITION
            newQuantity < oldQuantity -> InventoryLevelEvent.ChangeType.REMOVAL
            else -> InventoryLevelEvent.ChangeType.ADJUSTMENT
        }
    }

    override fun updateReservedQuantity(itemId: Long, locationCode: String, newQuantity: Int): InventoryLevel? {
        val key = InventoryLevelKey(itemId, locationCode).asString()
        return getInventoryLevel(itemId, locationCode)?.let { level ->
            val updatedLevel = level.copy(
                reservedQuantity = newQuantity,
                lastUpdated = LocalDateTime.now().toString()
            )
            saveInventoryLevel(updatedLevel)
            updatedLevel
        }
    }

    override fun getInventoryLevelsForLocation(locationCode: String): List<InventoryLevel> =
        findKeysByPattern("inventory:*:$locationCode")
            .mapNotNull { getInventoryLevel(extractItemId(it), locationCode) }

    fun updateMultipleQuantities(updates: List<QuantityUpdate>): List<InventoryLevel> =
        updates.mapNotNull { update ->
            updateAvailableQuantity(
                itemId = update.itemId,
                locationCode = update.locationCode,
                newQuantity = update.newQuantity
            )
        }

    override fun getMultipleInventoryLevels(itemIds: List<Long>, locationCode: String): List<InventoryLevel> =
        itemIds.mapNotNull { itemId ->
            getInventoryLevel(itemId, locationCode)
        }

    private fun findKeysByPattern(pattern: String): Set<String> =
        redisTemplate.keys(pattern) ?: emptySet()

    private fun extractItemId(key: String): Long =
        key.split(":")[1].toLong()

    override fun deleteInventoryLevel(itemId: Long, locationCode: String) {
        val key = InventoryLevelKey(itemId, locationCode).asString()
        redisTemplate.delete(key)
    }

    fun saveInventoryChange(change: InventoryChange) {
        val key = "history:${change.itemId}:${change.locationCode}"
        val valueAsString = objectMapper.writeValueAsString(change)
        redisTemplate.opsForList().rightPush(key, valueAsString)
    }

    fun getInventoryChanges(itemId: Long, locationCode: String): List<InventoryChange> {
        val key = "history:${itemId}:${locationCode}"
        return redisTemplate.opsForList().range(key, 0, -1)
            ?.mapNotNull { objectMapper.readValue(it, InventoryChange::class.java) }
            ?: emptyList()
    }
}