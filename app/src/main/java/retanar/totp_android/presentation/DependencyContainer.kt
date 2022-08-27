package retanar.totp_android.presentation

import android.content.Context
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.data.crypto.AndroidKeyStoreRepository
import retanar.totp_android.data.crypto.TotpCodeGeneratorImpl
import retanar.totp_android.data.database.TotpDao
import retanar.totp_android.data.database.TotpDatabase
import retanar.totp_android.data.repository.TotpKeyRepositoryImpl
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.crypto.SecretKeyRepository
import retanar.totp_android.domain.crypto.TotpCodeGenerator
import retanar.totp_android.domain.repository.TotpKeyRepository

object DependencyContainer {
    lateinit var dao: TotpDao
        private set

    lateinit var totpKeyRepository: TotpKeyRepository
        private set

    val secretKeyRepository: SecretKeyRepository = AndroidKeyStoreRepository()

    val secretEncryptor: SecretEncryptor = AesGcmSecretEncryptor(
        secretKeyRepository.getKey("totp_key") ?: secretKeyRepository.generateRandomKey("totp_key")
    )

    val totpCodeGenerator: TotpCodeGenerator = TotpCodeGeneratorImpl()

    fun initialize(context: Context) {
        dao = TotpDatabase.getDatabase(context).totpDao
        totpKeyRepository = TotpKeyRepositoryImpl(dao)
    }
}