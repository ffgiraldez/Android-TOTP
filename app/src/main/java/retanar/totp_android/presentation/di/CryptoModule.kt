package retanar.totp_android.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.data.crypto.AndroidKeyStoreRepository
import retanar.totp_android.data.crypto.Argon2PasswordHasher
import retanar.totp_android.data.crypto.TotpCodeGeneratorImpl
import retanar.totp_android.domain.crypto.PasswordHasher
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.crypto.SecretKeyRepository
import retanar.totp_android.domain.crypto.TotpCodeGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Singleton
    @Provides
    fun provideSecretKeyRepository(): SecretKeyRepository = AndroidKeyStoreRepository()

    @Provides
    fun provideAesSecretEncryptor(keyRepository: SecretKeyRepository): SecretEncryptor {
        val key = keyRepository.getKey("totp_key") ?: keyRepository.generateRandomKey("totp_key")
        return AesGcmSecretEncryptor(key)
    }

    @Provides
    fun provideTotpCodeGenerator(): TotpCodeGenerator = TotpCodeGeneratorImpl()

    @Provides
    fun provideArgon2PasswordHasher(): PasswordHasher = Argon2PasswordHasher()
}