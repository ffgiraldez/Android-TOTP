package retanar.totp_android.domain.crypto

interface PasswordHasher {
    fun hash(password: ByteArray, salt: ByteArray?): ByteArray
}