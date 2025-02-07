package com.starter.inventory_information.adapters.outbound.messaging

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class InventoryItemEventProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val topic = "inventory-topic-test"

    fun sendInventoryItemCreatedEvent(itemId: Long) {
        val message = "Inventory item with ID $itemId was created"
        kafkaTemplate.send(topic, message)
    }

    fun sendInventoryItemUpdatedEvent(itemId: Long) {
        val message = "Inventory item with ID $itemId was updated"
        kafkaTemplate.send(topic, message)
    }

    fun sendInventoryItemRemovedEvent(itemId: Long) {
        val message = "Inventory item with ID $itemId was removed"
        kafkaTemplate.send(topic, message)
    }
}