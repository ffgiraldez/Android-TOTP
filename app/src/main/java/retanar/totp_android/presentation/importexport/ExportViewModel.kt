package retanar.totp_android.presentation.importexport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.domain.crypto.PasswordHasher
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.repository.TotpKeyRepository
import retanar.totp_android.domain.usecases.ExportKeysUseCase
import retanar.totp_android.domain.usecases.SavingMode
import java.io.OutputStream
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val repository: TotpKeyRepository,
    private val repositoryEncryptor: SecretEncryptor,
    private val passwordHasher: PasswordHasher,
) : ViewModel() {
    suspend fun export(savingMode: SavingMode, plainPassword: String, outputStream: OutputStream) {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val exportEncryptor = if (plainPassword.isNotBlank()) {
            val hash = passwordHasher.hash(plainPassword.encodeToByteArray(), salt)
            val secretKey = SecretKeySpec(hash, "AES")
            AesGcmSecretEncryptor(secretKey)
        } else null

        ExportKeysUseCase(
            repository.getAllKeys().stateIn(viewModelScope).value,
            outputStream,
            repositoryEncryptor,
            exportEncryptor,
            salt,
            savingMode
        )()
    }
}