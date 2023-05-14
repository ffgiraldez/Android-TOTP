package retanar.totp_android

import kotlinx.coroutines.runBlocking
import org.junit.Test
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.domain.entities.EncryptedTotpKey
import retanar.totp_android.domain.usecases.ExportKeysUseCase
import retanar.totp_android.domain.usecases.SavingMode
import java.io.ByteArrayOutputStream
import javax.crypto.spec.SecretKeySpec

class ImportExportTest {
    private val secretKey = SecretKeySpec(ByteArray(16), "AES")
    private val secretEncryptor = AesGcmSecretEncryptor(secretKey)
    private val keyList = List(4) {id ->
        val iv = ByteArray(secretEncryptor.ivSize) { id.toByte() }
        EncryptedTotpKey(id, id.toString(), secretEncryptor.encrypt(ByteArray(15) { it.toByte() }, iv), iv)
}
    private val outputStream = ByteArrayOutputStream()
    private var exportUseCase = ExportKeysUseCase(keyList, secretEncryptor, outputStream, SavingMode.NoEncryption)

    @Test
    fun exportUnencrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.NoEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
    }

    @Test
    fun exportKeyEncrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.KeyEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
    }

    @Test
    fun exportFullyEncrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.FullEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
    }
}