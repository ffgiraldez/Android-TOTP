package retanar.totp_android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retanar.totp_android.data.database.TotpDao
import retanar.totp_android.data.database.TotpDbMapper
import retanar.totp_android.domain.entities.EncryptedTotpKey
import retanar.totp_android.domain.repository.TotpKeyRepository

class TotpKeyRepositoryImpl(
    private val dao: TotpDao,
) : TotpKeyRepository {

    override fun getAllKeys(): Flow<List<EncryptedTotpKey>> {
        return dao.queryAll().map { it.map(TotpDbMapper::toTotpKey) }
    }

    override suspend fun addKey(key: EncryptedTotpKey) {
        dao.insert(TotpDbMapper.fromTotpKey(key))
    }

    override suspend fun removeKey(key: EncryptedTotpKey) {
        dao.delete(TotpDbMapper.fromTotpKey(key))
    }

    override suspend fun editKey(key: EncryptedTotpKey) {
        dao.update(TotpDbMapper.fromTotpKey(key))
    }
}