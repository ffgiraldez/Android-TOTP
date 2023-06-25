package retanar.totp_android.presentation.importexport

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retanar.totp_android.data.crypto.AesGcmSecretEncryptor
import retanar.totp_android.domain.crypto.PasswordHasher
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.entities.ExportEntity
import retanar.totp_android.domain.entities.NoEncryptionExport
import retanar.totp_android.domain.repository.TotpKeyRepository
import retanar.totp_android.domain.usecases.AddNewTotpUseCase
import retanar.totp_android.domain.usecases.ImportKeysUseCase
import java.io.InputStream
import javax.crypto.AEADBadTagException
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    repository: TotpKeyRepository,
    repositoryEncryptor: SecretEncryptor,
    private val passwordHasher: PasswordHasher,
) : ViewModel() {
    private val addNewTotpUseCase = AddNewTotpUseCase(repository, repositoryEncryptor)
    private val importUseCase = ImportKeysUseCase(addNewTotpUseCase)
    private lateinit var exportEntity: ExportEntity

    var errorText by mutableStateOf("")
        private set

    suspend fun prepareAndCheckPassword(inputStream: InputStream): Boolean {
        exportEntity = importUseCase.prepare(inputStream)
        return exportEntity !is NoEncryptionExport
    }

    suspend fun import(password: String? = null) {
        try {
            errorText = ""
            importUseCase(exportEntity) { salt ->
                password?.let {
                    val secretKey = SecretKeySpec(passwordHasher.hash(password.toByteArray(), salt), "AES")
                    AesGcmSecretEncryptor(secretKey)
                }
            }
        } catch (e: AEADBadTagException) {
            errorText = "Error: wrong key or broken file"
        } catch (e: Exception) {
            errorText = "Unexpected ${e.message}, while importing file"
        }
    }
}