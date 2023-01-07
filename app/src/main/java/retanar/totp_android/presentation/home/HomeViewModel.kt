package retanar.totp_android.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.apache.commons.codec.binary.Base32
import retanar.totp_android.domain.crypto.SecretEncryptor
import retanar.totp_android.domain.crypto.TotpCodeGenerator
import retanar.totp_android.domain.entities.EncryptedTotpKey
import retanar.totp_android.domain.repository.TotpKeyRepository
import retanar.totp_android.domain.usecases.AddNewTotpUseCase
import retanar.totp_android.domain.usecases.EditTotpUseCase
import retanar.totp_android.domain.usecases.GenerateTotpCodeUseCase
import kotlin.time.Duration.Companion.milliseconds

private const val defaultUpdateStepMs = 30_000L

class HomeViewModel(
    private val totpKeyRepo: TotpKeyRepository,
    private val secretEncryptor: SecretEncryptor,
    totpCodeGenerator: TotpCodeGenerator,
) : ViewModel() {
    private val addTotpUseCase = AddNewTotpUseCase(totpKeyRepo, secretEncryptor)
    private val editTotpUseCase = EditTotpUseCase(totpKeyRepo, secretEncryptor)
    private val generateTotpCodeUseCase = GenerateTotpCodeUseCase(
        totpCodeGenerator,
        secretEncryptor,
        getUnixTime = { System.currentTimeMillis().milliseconds }
    )

    private val totpKeyFlow = totpKeyRepo.getAllKeys().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val homeState = mutableStateOf(HomeState())

    init {
        viewModelScope.launch {
            autoUpdate()
        }
    }

    // Combine database updates with timer updates
    private suspend fun autoUpdate(timeStepMs: Long = defaultUpdateStepMs) {
        flow {
            while (true) {
                emit(Unit)
                val timeCurrent = System.currentTimeMillis() % timeStepMs
                // Sleep right until the next step
                delay(timeStepMs - timeCurrent)
            }
        }.combine(totpKeyFlow) { _, keyList -> keyList }.collect { keyList ->
            updateStateList(keyList)
        }
    }

    private fun updateStateList(keyList: List<EncryptedTotpKey>) {
        val list = keyList.map { TotpCardState(it.id, it.name, generateTotpCodeUseCase(it)) }.toList()
        homeState.value = homeState.value.copy(totpList = list)
    }

    fun addTotp(name: String, base32Secret: String) {
        if (base32Secret.isEmpty()) return
        val secret = Base32().decode(base32Secret)
        viewModelScope.launch {
            addTotpUseCase(secret, name)
        }
    }

    fun removeTotpById(id: Int) {
        viewModelScope.launch {
            val toDelete = totpKeyFlow.value.find { key -> key.id == id }!!
            totpKeyRepo.removeKey(toDelete)
        }
    }

    fun requestEdit(id: Int) {
        val toEdit = totpKeyFlow.value.find { key -> key.id == id }
        homeState.value = homeState.value.copy(
            editingTotp = if (toEdit == null) {
                null
            } else {
                EditTotpState(
                    id, toEdit.name, Base32().encode(
                        secretEncryptor.decrypt(toEdit.secret, toEdit.iv)
                    ).decodeToString()
                )
            }
        )
    }

    fun editTotp(edited: EditTotpState) {
        if (edited.base32Secret.isEmpty()) return
        val secret = Base32().decode(edited.base32Secret)
        viewModelScope.launch {
            editTotpUseCase(edited.id, edited.name, secret)
        }
    }
}
