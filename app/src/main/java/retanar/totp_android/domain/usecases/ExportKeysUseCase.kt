package retanar.totp_android.domain.usecases

import android.util.Base64
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.apache.commons.codec.binary.Base32
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.entities.*
import java.io.OutputStream
import java.security.SecureRandom

enum class SavingMode { NoEncryption, KeyEncryption, FullEncryption }

class ExportKeysUseCase(
    private val keys: List<EncryptedTotpKey>,
    private val encryptor: SecretEncryptor,
    private val outputStream: OutputStream,
    private val savingMode: SavingMode,
) {
    // TODO: TEST
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun invoke() {
        when (savingMode) {
            SavingMode.NoEncryption -> {
                val exportData = NoEncryptionExport(keys.map {
                    UnencryptedKey(
                        it.name, Base32().encode(encryptor.decrypt(it.secret, it.iv)).decodeToString()
                    )
                })
                Json.encodeToStream(exportData, outputStream)
            }

            SavingMode.KeyEncryption -> {
                val exportData = KeyEncryptionExport(keys.map {
                    val newIv = ByteArray(encryptor.ivSize)
                    SecureRandom().nextBytes(newIv)
                    val newEncryptedSecret = encryptor.encrypt(encryptor.decrypt(it.secret, it.iv), newIv)
                    EncryptedKey(
                        it.name,
                        Base64.encodeToString(newEncryptedSecret, Base64.NO_WRAP),
                        Base64.encodeToString(newIv, Base64.NO_WRAP)
                    )
                })
                Json.encodeToStream(exportData, outputStream)
            }

            SavingMode.FullEncryption -> {
                val unencryptedKeysList = keys.map {
                    UnencryptedKey(
                        it.name, Base32().encode(encryptor.decrypt(it.secret, it.iv)).decodeToString()
                    )
                }
                val newIv = ByteArray(encryptor.ivSize)
                SecureRandom().nextBytes(newIv)
                val encryptedKeysList = encryptor.encrypt(
                    Json.encodeToString(unencryptedKeysList).encodeToByteArray(), newIv
                )
                Json.encodeToStream(
                    FullEncryptionExport(
                        Base64.encodeToString(encryptedKeysList, Base64.NO_WRAP),
                        Base64.encodeToString(newIv, Base64.NO_WRAP)
                    ), outputStream
                )
            }
        }
    }
}

