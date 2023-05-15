package retanar.totp_android.domain.usecases

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
    private val addNewTotpUseCase: AddNewTotpUseCase,
    private val inputStream: InputStream,
    private val exportEncryptor: SecretEncryptor? = null,
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend operator fun invoke() {
        when (val exportEntity = Json.decodeFromStream<ExportEntity>(inputStream)) {
            is NoEncryptionExport -> {
                exportEntity.keysList.forEach {
                    addNewTotpUseCase(Base32().decode(it.base32Secret), it.name)
                }
            }

            is KeyEncryptionExport -> {
                val base64 = Base64()
                exportEntity.keysList.forEach {
                    val plainSecret = exportEncryptor!!.decrypt(
                        base64.decode(it.base64Secret),
                        base64.decode(it.base64Iv)
                    )
                    addNewTotpUseCase(plainSecret, it.name)
                }
            }

            is FullEncryptionExport -> {
                val base64 = Base64()
                val json = exportEncryptor!!.decrypt(
                    base64.decode(exportEntity.base64Data),
                    base64.decode(exportEntity.base64Iv)
                )
                val keyList = Json.decodeFromString<List<UnencryptedKey>>(json.decodeToString())
                keyList.forEach {
                    addNewTotpUseCase(Base32().decode(it.base32Secret), it.name)
                }
            }
        }
    }
}