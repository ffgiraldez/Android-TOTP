package retanar.totp_android.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.apache.commons.codec.binary.Base32
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.crypto.TotpCodeGenerator
import retanar.totp_android.domain.repository.TotpKeyRepository
import retanar.totp_android.domain.usecases.AddNewTotpUseCase
import retanar.totp_android.domain.usecases.GenerateTotpCodeUseCase
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val totpKeyRepo: TotpKeyRepository,
    secretEncryptor: SecretEncryptor,
    totpCodeGenerator: TotpCodeGenerator,
) : ViewModel() {
    private val addTotpUseCase = AddNewTotpUseCase(totpKeyRepo, secretEncryptor)
    private val generateTotpCodeUseCase =
        GenerateTotpCodeUseCase(totpCodeGenerator, secretEncryptor, getUnixTime = { Date().time.milliseconds })

    val homeState = mutableStateOf(HomeState())

    init {
        viewModelScope.launch {
            updateStateList()
        }
    }

    private fun updateStateList() {
        viewModelScope.launch {
            val keyList = totpKeyRepo.getAllKeys()
            val list = keyList.map { TotpCardState(it.id, it.name, generateTotpCodeUseCase.execute(it)) }.toList()
            homeState.value = homeState.value.copy(totpList = list)
        }
    }

    fun addTotp(name: String, base32Secret: String) {
        val secret = Base32().decode(base32Secret)
        viewModelScope.launch {
            addTotpUseCase.execute(secret, name)
            updateStateList()
        }
    }
}
