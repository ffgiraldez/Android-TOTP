package retanar.totp_android

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import javax.crypto.spec.SecretKeySpec

class AesGcmEncryptorTest {
    private lateinit var encryptor: AesGcmSecretEncryptor
    private lateinit var iv: ByteArray

    @Before
    fun init() {
        val secretKey = SecretKeySpec(ByteArray(16) { it.toByte() }, "AES")
        encryptor = AesGcmSecretEncryptor(secretKey)
        iv = ByteArray(encryptor.ivSize)
    }

    @Test
    fun encryptAndDecrypt() {
        val plainSecret = "0123456789".toByteArray()
        val encrypted = encryptor.encrypt(plainSecret, iv)
        val decrypted = encryptor.decrypt(encrypted, iv)

        assertTrue(plainSecret.contentEquals(decrypted))
        assertFalse(plainSecret.contentEquals(encrypted))
    }
}