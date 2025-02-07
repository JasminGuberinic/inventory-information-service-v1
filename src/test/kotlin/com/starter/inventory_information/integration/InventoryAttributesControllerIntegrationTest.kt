import com.starter.inventory_information.adapters.inbound.web.controller.InventoryAttributesController
import com.starter.inventory_information.adapters.outbound.database.entities.InventoryItemEntity
import com.starter.inventory_information.adapters.outbound.database.repository.JpaInventoryItemRepository
import com.starter.inventory_information.ports.inbound.web.dto.DimensionsDTO
import com.starter.inventory_information.ports.inbound.web.dto.PackagingDTO
import com.starter.inventory_information.ports.inbound.web.dto.WeightDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal

@SpringBootTest
@Transactional
class InventoryAttributesControllerIntegrationTest {

    @Autowired
    private lateinit var inventoryAttributesController: InventoryAttributesController

    @Autowired
    private lateinit var inventoryItemRepository: JpaInventoryItemRepository

    private lateinit var testInventoryItem: InventoryItemEntity

    @BeforeEach
    fun setup() {
        testInventoryItem = inventoryItemRepository.save(
            InventoryItemEntity(
                name = "Test Item",
                quantity = 1,
                price = BigDecimal.TEN
            )
        )
    }

    @Test
    fun `test update dimensions for inventory item`() {
        val dimensionsDTO = DimensionsDTO(
            inventoryItemId = testInventoryItem.id!!,
            length = 10.0,
            width = 5.0,
            height = 3.0
        )

        val updateResponse = inventoryAttributesController.updateDimensions(
            testInventoryItem.id!!,
            dimensionsDTO
        )

        assertTrue(updateResponse.statusCode.is2xxSuccessful)
        val updatedDimensions = updateResponse.body
        requireNotNull(updatedDimensions) { "Response body should not be null" }
        assertEquals(10.0, updatedDimensions.length)
        assertEquals(5.0, updatedDimensions.width)
        assertEquals(3.0, updatedDimensions.height)

        // Verify in database
        val updatedItem = inventoryItemRepository.findById(testInventoryItem.id!!).get()
        assertEquals(10.0, updatedItem.dimensions?.length)
        assertEquals(5.0, updatedItem.dimensions?.width)
        assertEquals(3.0, updatedItem.dimensions?.height)
    }

    @Test
    fun `test update weight for inventory item`() {
        val weightDTO = WeightDTO(
            inventoryItemId = testInventoryItem.id!!,
            value = 2.5,
            unit = "KG"
        )

        val updateResponse = inventoryAttributesController.updateWeight(
            testInventoryItem.id!!,
            weightDTO
        )

        assertTrue(updateResponse.statusCode.is2xxSuccessful)
        val updatedWeight = updateResponse.body
        requireNotNull(updatedWeight) { "Response body should not be null" }
        assertEquals(2.5, updatedWeight.value)
        assertEquals("KG", updatedWeight.unit)

        // Verify in database
        val updatedItem = inventoryItemRepository.findById(testInventoryItem.id!!).get()
        assertEquals(2.5, updatedItem.weight?.value)
        assertEquals("KG", updatedItem.weight?.unit)
    }

    @Test
    fun `test update packaging for inventory item`() {
        val packagingDTO = PackagingDTO(
            inventoryItemId = testInventoryItem.id!!,
            isSensitive = true,
            packagingType = "FRAGILE"
        )

        val updateResponse = inventoryAttributesController.updatePackaging(
            testInventoryItem.id!!,
            packagingDTO
        )

        assertTrue(updateResponse.statusCode.is2xxSuccessful)
        val updatedPackaging = updateResponse.body
        requireNotNull(updatedPackaging) { "Response body should not be null" }
        assertTrue(updatedPackaging.isSensitive)
        assertEquals("FRAGILE", updatedPackaging.packagingType)

        // Verify in database
        val updatedItem = inventoryItemRepository.findById(testInventoryItem.id!!).get()
        assertTrue(updatedItem.packaging?.isSensitive == true)
        assertEquals("FRAGILE", updatedItem.packaging?.packagingType)
    }

    @Test
    fun `test update dimensions for non-existent inventory item should fail`() {
        val dimensionsDTO = DimensionsDTO(
            inventoryItemId = 999L,
            length = 10.0,
            width = 5.0,
            height = 3.0
        )

        val nonExistentId = 999L
        val updateResponse = inventoryAttributesController.updateDimensions(
            nonExistentId,
            dimensionsDTO
        )

        assertTrue(updateResponse.statusCode.is4xxClientError)
    }
}