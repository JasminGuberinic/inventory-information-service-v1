import com.starter.inventory_information.domain.model.InventoryItem
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class StorageOptimizationService {

    /**
     * Izračunava optimalni raspored za skladištenje na osnovu dimenzija i težine
     */
    fun calculateStorageArrangement(item: InventoryItem, paletteCapacity: Double): StorageArrangement {
        if (item.dimensions == null || item.weight == null) {
            throw IllegalArgumentException("Item must have dimensions and weight for storage calculation")
        }

        val itemVolume = item.dimensions!!.length * item.dimensions!!.width * item.dimensions!!.height
        val itemsPerPalette = (paletteCapacity / itemVolume).toInt()
        val requiredPalettes = ceil(item.quantity.toDouble() / itemsPerPalette).toInt()

        return StorageArrangement(
            itemsPerPalette = itemsPerPalette,
            requiredPalettes = requiredPalettes,
            totalVolume = itemVolume * item.quantity
        )
    }

    /**
     * Provjerava kompatibilnost skladištenja između različitih proizvoda
     */
    fun checkStorageCompatibility(item1: InventoryItem, item2: InventoryItem): StorageCompatibility {
        val incompatibilityReasons = mutableListOf<String>()

        checkWeightCompatibility(item1, item2)?.let { incompatibilityReasons.add(it) }
        checkSensitivityCompatibility(item1, item2)?.let { incompatibilityReasons.add(it) }
        checkVolumeCompatibility(item1, item2)?.let { incompatibilityReasons.add(it) }

        return StorageCompatibility(
            areCompatible = incompatibilityReasons.isEmpty(),
            incompatibilityReasons = incompatibilityReasons
        )
    }

    /**
     * Optimizuje raspored skladištenja za grupu proizvoda
     */
    fun optimizeStorageLayout(items: List<InventoryItem>): StorageLayout {
        val sensitiveItems = items.filter { it.packaging?.isSensitive == true }
        val regularItems = items.filter { it.packaging?.isSensitive != true }

        val zones = mutableListOf<StorageZone>()

        if (sensitiveItems.isNotEmpty()) {
            zones.add(StorageZone("SENSITIVE", sensitiveItems))
        }

        // Grupiši regularne items po težini
        val heavyItems = regularItems.filter { it.weight?.value ?: 0.0 > 50.0 }
        val lightItems = regularItems.filter { it.weight?.value ?: 0.0 <= 50.0 }

        if (heavyItems.isNotEmpty()) {
            zones.add(StorageZone("HEAVY", heavyItems))
        }
        if (lightItems.isNotEmpty()) {
            zones.add(StorageZone("LIGHT", lightItems))
        }

        return StorageLayout(zones)
    }

    private fun checkWeightCompatibility(item1: InventoryItem, item2: InventoryItem): String? {
        val weight1 = item1.calculateTotalWeight() ?: 0.0
        val weight2 = item2.calculateTotalWeight() ?: 0.0
        return if (weight1 > weight2 * 2) {
            "Weight difference too high for stacking"
        } else null
    }

    private fun checkSensitivityCompatibility(item1: InventoryItem, item2: InventoryItem): String? {
        return if (item1.packaging?.isSensitive == true && item2.packaging?.isSensitive == true) {
            "Both items are sensitive and require separate storage"
        } else null
    }

    private fun checkVolumeCompatibility(item1: InventoryItem, item2: InventoryItem): String? {
        val volume1 = item1.dimensions?.let { it.length * it.width * it.height } ?: 0.0
        val volume2 = item2.dimensions?.let { it.length * it.width * it.height } ?: 0.0
        return if (volume1 > volume2 * 3) {
            "Volume difference too high for efficient storage"
        } else null
    }
}

data class StorageLayout(
    val zones: List<StorageZone>
)

data class StorageZone(
    val type: String,
    val items: List<InventoryItem>
)

data class StorageArrangement(
    val itemsPerPalette: Int,
    val requiredPalettes: Int,
    val totalVolume: Double
)

data class StorageCompatibility(
    val areCompatible: Boolean,
    val incompatibilityReasons: List<String>
)