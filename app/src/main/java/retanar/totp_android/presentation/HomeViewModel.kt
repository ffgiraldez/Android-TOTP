package retanar.totp_android.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.crypto.TotpCodeGenerator
import retanar.totp_android.domain.repository.TotpKeyRepository
import retanar.totp_android.domain.usecases.AddNewTotpUseCase
import retanar.totp_android.domain.usecases.GenerateTotpCodeUseCase
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    totpKeyRepo: TotpKeyRepository,
    secretEncryptor: SecretEncryptor,
    totpCodeGenerator: TotpCodeGenerator,
) : ViewModel() {
    private val addTotpUseCase = AddNewTotpUseCase(totpKeyRepo, secretEncryptor)
    private val generateTotpCodeUseCase =
        GenerateTotpCodeUseCase(totpCodeGenerator, secretEncryptor, getUnixTime = { Date().time.milliseconds })

    val homeState = mutableStateOf(HomeState())

    init {
        viewModelScope.launch {
            val keyList = totpKeyRepo.getAllKeys()
            updateStateList(keyList.map { TotpCardState(it.id, it.name, generateTotpCodeUseCase.execute(it)) }.toList())
        }
    }

    private fun updateStateList(list: List<TotpCardState>) {
        homeState.value = homeState.value.copy(totpList = list)
    }
}
