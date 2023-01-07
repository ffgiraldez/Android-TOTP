package retanar.totp_android.domain.usecases

import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.entities.EncryptedTotpKey
import retanar.totp_android.domain.repository.TotpKeyRepository
import java.security.SecureRandom

class EditTotpUseCase(
    private val repository: TotpKeyRepository,
    private val encryptor: SecretEncryptor,
) {
    suspend operator fun invoke(id: Int, name: String, plainSecret: ByteArray) {
        val random = SecureRandom()
        val iv = ByteArray(encryptor.ivSize)
        random.nextBytes(iv)
        repository.editKey(EncryptedTotpKey(id, name, encryptor.encrypt(plainSecret, iv), iv))
    }
}