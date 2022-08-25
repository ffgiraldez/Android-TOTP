package retanar.totp_android.domain.usecases

import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.crypto.TotpCodeGenerator
import retanar.totp_android.domain.entities.EncryptedTotpKey
import kotlin.time.Duration

class GenerateTotpCodeUseCase(
    private val generator: TotpCodeGenerator,
    private val encryptor: SecretEncryptor,
    private val getUnixTime: () -> Duration,
) {
    // suspend keyword in case encryptor or generator is slow
    suspend fun execute(totpKey: EncryptedTotpKey): Int {
        val secret = encryptor.decrypt(totpKey.secret, totpKey.iv)
        return generator.generate(secret, getUnixTime())
    }
}