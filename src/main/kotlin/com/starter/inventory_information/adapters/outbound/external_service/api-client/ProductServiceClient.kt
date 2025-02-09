package com.starter.inventory_information.adapters.outbound.external_service.`api-client`

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.slf4j.LoggerFactory
import org.springframework.web.client.getForObject

data class ProductDTO(
    val id: Long,
    val name: String,
    val sku: String
)

class ProductServiceException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

@Component
class ProductServiceClient(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${services.product.base-url}") private val baseUrl: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val restTemplate = restTemplateBuilder.build()

    fun validateProductExists(itemId: Long): Boolean = runCatching {
        restTemplate.getForObject<ProductDTO>("$baseUrl/products/$itemId")
        true
    }.getOrElse { exception ->
        when (exception) {
            is HttpClientErrorException -> handleHttpError(itemId, exception)
            else -> handleUnexpectedError(itemId, exception)
        }
    }

    private fun handleHttpError(itemId: Long, error: HttpClientErrorException): Boolean =
        when (error.statusCode) {
            HttpStatus.NOT_FOUND -> {
                logger.warn("Product $itemId not found")
                false
            }
            else -> throw ProductServiceException(
                "Failed to validate product $itemId. Status: ${error.statusCode}"
            )
        }

    private fun handleUnexpectedError(itemId: Long, error: Throwable): Boolean {
        logger.error("Unexpected error validating product $itemId", error)
        throw ProductServiceException("Unexpected error validating product", error)
    }
}