package com.starter.inventory_information.adapters.inbound.messaging.listener

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.starter.inventory_information.adapters.outbound.database.entities.InventoryItemEntity
import com.starter.inventory_information.adapters.outbound.database.repository.InventoryItemJpaAdapter
import com.starter.inventory_information.ports.inbound.messaging.InventoryItemEventPort
import java.math.BigDecimal

@Component
class InventoryItemEventConsumer(var inventoryItemJpaAdapterPort : InventoryItemJpaAdapter) : InventoryItemEventPort {

    @KafkaListener(topics = ["inventory-topic-test"], groupId = "inventory-consumer-group")
    override fun listenInventoryItemEvent(message: String) {
        println("Received Kafka message: $message")

//        {
//            "id": 123,
//            "name": "Sample Item",
//            "quantity": 50,
//            "price": 19.99
//        }

        val objectMapper = jacksonObjectMapper()
        val inventoryItemData: Map<String, Any> = objectMapper.readValue(message)

        val inventoryItemEntity = InventoryItemEntity(
            id = inventoryItemData["id"] as Long,
            name = inventoryItemData["name"] as String,
            quantity = inventoryItemData["quantity"] as Int,
            price = inventoryItemData["price"] as BigDecimal
        )

        inventoryItemJpaAdapterPort.save(inventoryItemEntity)
    }
}