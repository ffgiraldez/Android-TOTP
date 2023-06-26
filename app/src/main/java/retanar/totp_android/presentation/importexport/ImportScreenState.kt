package retanar.totp_android.presentation.importexport

data class ImportScreenState(
    val errorText: String? = null,
    val importedKeys: List<ImportedItemState>? = null,
)

data class ImportedItemState(
    val name: String,
    val secretKey: String,
    val nameSimilarity: String? = null,
    val secretSimilarity: String? = null,
    val checked: Boolean = nameSimilarity == null && secretSimilarity == null
)