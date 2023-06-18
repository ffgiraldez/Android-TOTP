package retanar.totp_android.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.entities.*
import java.io.InputStream

class ImportKeysUseCase(
    private val addNewTotpUseCase: AddNewTotpUseCase
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun prepare(inputStream: InputStream): ExportEntity {
        return withContext(Dispatchers.IO) {
            Json.decodeFromStream<ExportEntity>(inputStream)
        }
    }

    suspend operator fun invoke(
        exportEntity: ExportEntity,
        getExportEncryptor: (keySalt: ByteArray?) -> SecretEncryptor? = { null }
    ) {
        when (exportEntity) {
            is NoEncryptionExport -> {
                exportEntity.keysList.forEach {
                    addNewTotpUseCase(Base32().decode(it.base32Secret), it.name)
                }
            }

            is KeyEncryptionExport -> {
                val base64 = Base64()
                // apache Base64.decode doesn't work with String on Android
                val exportEncryptor = getExportEncryptor(base64.decode(exportEntity.base64EncryptionKeySalt?.toByteArray()))!!
                exportEntity.keysList.forEach {
                    val plainSecret = exportEncryptor.decrypt(
                        base64.decode(it.base64Secret.toByteArray()),
                        base64.decode(it.base64Iv.toByteArray())
                    )
                    addNewTotpUseCase(plainSecret, it.name)
                }
            }

            is FullEncryptionExport -> {
                val base64 = Base64()
                val exportEncryptor = getExportEncryptor(base64.decode(exportEntity.base64EncryptionKeySalt?.toByteArray()))!!
                val json = exportEncryptor.decrypt(
                    base64.decode(exportEntity.base64Data.toByteArray()),
                    base64.decode(exportEntity.base64Iv.toByteArray())
                )
                val keyList = Json.decodeFromString<List<UnencryptedKey>>(json.decodeToString())
                keyList.forEach {
                    addNewTotpUseCase(Base32().decode(it.base32Secret), it.name)
                }
            }
        }
    }
}