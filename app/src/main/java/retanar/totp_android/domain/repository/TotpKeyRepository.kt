package retanar.totp_android.domain.repository

import retanar.totp_android.domain.entities.EncryptedTotpKey

interface TotpKeyRepository {
    suspend fun getAllKeys(): List<EncryptedTotpKey>

    suspend fun addKey(key: EncryptedTotpKey)

    suspend fun removeKey(key: EncryptedTotpKey)

    /** Edit a key by its id */
    suspend fun editKey(key: EncryptedTotpKey)
}