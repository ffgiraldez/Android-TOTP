package retanar.totp_android.data

import retanar.totp_android.data.database.TotpDao
import retanar.totp_android.data.database.TotpDbMapper
import retanar.totp_android.domain.entities.TotpKey
import retanar.totp_android.domain.repository.TotpKeyRepository

class TotpKeyRepositoryImpl(
    private val dao: TotpDao,
) : TotpKeyRepository {

    override suspend fun getAllKeys(): List<TotpKey> {
        return dao.queryAll().map(TotpDbMapper::toTotpKey)
    }

    override suspend fun addKey(key: TotpKey) {
        dao.insert(TotpDbMapper.fromTotpKey(key))
    }

    override suspend fun removeKey(key: TotpKey) {
        dao.delete(TotpDbMapper.fromTotpKey(key))
    }

    override suspend fun editKey(key: TotpKey) {
        dao.update(TotpDbMapper.fromTotpKey(key))
    }
}