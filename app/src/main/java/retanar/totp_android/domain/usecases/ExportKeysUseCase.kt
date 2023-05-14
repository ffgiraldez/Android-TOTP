package retanar.totp_android.domain.usecases

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.Base64
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.entities.*
import java.io.OutputStream
import java.security.SecureRandom

enum class SavingMode { NoEncryption, KeyEncryption, FullEncryption }

class ExportKeysUseCase(
    private val keys: List<EncryptedTotpKey>,
    private val encryptor: SecretEncryptor,
    private val outputStream: OutputStream,
    var savingMode: SavingMode,
) {
    @OptIn(ExperimentalSerializationApi::class)
    suspend operator fun invoke() {
        val export: ExportEntity = when (savingMode) {
            SavingMode.NoEncryption -> {
                NoEncryptionExport(keys.map {
                    UnencryptedKey(
                        it.name, Base32().encode(encryptor.decrypt(it.secret, it.iv)).decodeToString()
                    )
                })
            }

            SavingMode.KeyEncryption -> {
                val base64 = Base64()
                KeyEncryptionExport(keys.map {
                    val newIv = ByteArray(encryptor.ivSize)
                    SecureRandom().nextBytes(newIv)
                    val newEncryptedSecret = encryptor.encrypt(encryptor.decrypt(it.secret, it.iv), newIv)
                    EncryptedKey(
                        it.name,
                        base64.encode(newEncryptedSecret).decodeToString(),
                        base64.encode(newIv).decodeToString()
                    )
                })
            }

            SavingMode.FullEncryption -> {
                val base64 = Base64()
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
                FullEncryptionExport(
                    base64.encode(encryptedKeysList).decodeToString(),
                    base64.encode(newIv).decodeToString()
                )
            }
        }
        Json.encodeToStream(export, outputStream)
    }
}

