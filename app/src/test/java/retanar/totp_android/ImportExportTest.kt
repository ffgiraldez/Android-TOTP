package retanar.totp_android

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.domain.entities.EncryptedTotpKey
import retanar.totp_android.domain.repository.TotpKeyRepository
import retanar.totp_android.domain.usecases.AddNewTotpUseCase
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
        SavingMode.NoEncryption
    )
    private val addTotpUseCase = AddNewTotpUseCase(object : TotpKeyRepository {
        override suspend fun addKey(key: EncryptedTotpKey) {
            val stored = keyList.find { it.name == key.name }!!
            assertArrayEquals(
                secretEncryptor.decrypt(stored.secret, stored.iv),
                secretEncryptor.decrypt(key.secret, key.iv)
            )
        }

        override fun getAllKeys() = throw Exception()
        override suspend fun removeKey(key: EncryptedTotpKey) {}
        override suspend fun editKey(key: EncryptedTotpKey) {}
    }, secretEncryptor)

    @Test
    fun exportImportUnencrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.NoEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
        importBack()
    }

    @Test
    fun exportKeyEncrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.KeyEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
        importBack()
    }

    @Test
    fun exportFullyEncrypted() = runBlocking {
        exportUseCase.savingMode = SavingMode.FullEncryption
        exportUseCase()
        println(outputStream.toString(Charsets.UTF_8.name()))
        importBack()
    }

    fun importBack() = runBlocking {
        ImportKeysUseCase(
            addTotpUseCase,
            ByteArrayInputStream(outputStream.toByteArray()),
            exportEncryptor,
        )()
    }
}