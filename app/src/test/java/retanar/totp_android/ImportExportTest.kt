package retanar.totp_android

import kotlinx.coroutines.runBlocking
import org.apache.commons.codec.binary.Base32
import org.junit.Assert.assertNotNull
import org.junit.Test
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.domain.entities.EncryptedTotpKey
import retanar.totp_android.domain.entities.UnencryptedKey
import retanar.totp_android.domain.usecases.ExportKeysUseCase
import retanar.totp_android.domain.usecases.ImportKeysUseCase
import retanar.totp_android.domain.usecases.SavingMode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.crypto.spec.SecretKeySpec

class ImportExportTest {
    private val secretKey = SecretKeySpec(ByteArray(16), "AES")
    private val secretEncryptor = AesGcmSecretEncryptor(secretKey)
    private val exportEncryptor = AesGcmSecretEncryptor(secretKey)
    private val keyList = List(4) { id ->
        val iv = ByteArray(secretEncryptor.ivSize) { id.toByte() }
        EncryptedTotpKey(id, id.toString(), secretEncryptor.encrypt(ByteArray(15) { it.toByte() }, iv), iv)
    }
    private val outputStream = ByteArrayOutputStream()
    private var exportUseCase = ExportKeysUseCase(
        keyList,
        outputStream,
        secretEncryptor,
        exportEncryptor,
        encryptionKeySalt = ByteArray(16),
        savingMode = SavingMode.NoEncryption
    )

    @Test
    fun exportImportUnencrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.NoEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
        importBack()
    }

    @Test
    fun exportImportKeyEncrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.KeyEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
        importBack()
    }

    @Test
    fun exportImportFullyEncrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.FullEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
        importBack()
    }

    fun importBack() = runBlocking {
        val iuc = ImportKeysUseCase()
        val entity = iuc.prepare(ByteArrayInputStream(outputStream.toByteArray()))
        val imported = iuc(entity, { exportEncryptor })
        keyList.map { key ->
            UnencryptedKey(key.name, Base32().encode(secretEncryptor.decrypt(key.secret, key.iv)).decodeToString())
        }.forEach { realKey ->
            assertNotNull(imported.find { it.name == realKey.name && it.base32Secret == realKey.base32Secret })
        }
    }
}