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
    operator fun invoke(totpKey: EncryptedTotpKey): Int {
        val secret = encryptor.decrypt(totpKey.secret, totpKey.iv)
        return generator.generate(secret, getUnixTime())
    }
}