package retanar.totp_android.domain.repository

import retanar.totp_android.domain.entities.TotpKey

interface TotpKeyRepository {
    suspend fun getAllKeys(): List<TotpKey>

    suspend fun addKey(key: TotpKey)

    suspend fun removeKey(key: TotpKey)

    /** Edit a key by its id */
    suspend fun editKey(key: TotpKey)
}