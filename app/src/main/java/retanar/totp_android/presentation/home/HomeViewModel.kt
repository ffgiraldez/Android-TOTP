package retanar.totp_android.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
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
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private const val defaultUpdateStepMs = 30_000L

@HiltViewModel
class HomeViewModel @Inject constructor(
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
    private lateinit var oneSecondTimer: Timer

    init {
        viewModelScope.launch {
            autoUpdate()
        }
    }

    // Setup database updates with timer updates
    private suspend fun autoUpdate() {
        startTimer()
        totpKeyFlow.collect { keyList ->
            updateStateList(keyList)
        }
    }

    private fun startTimer() {
        if (!::oneSecondTimer.isInitialized) {
            oneSecondTimer = fixedRateTimer(
                null, true,
                initialDelay = 1000 - (System.currentTimeMillis() % 1000),
                period = 1000,
            ) {
                timerUpdates()
            }
        }
    }

    private fun timerUpdates() {
        val currentSecondsLeft = countSecondsLeft()
        if (currentSecondsLeft == (defaultUpdateStepMs / 1000).toInt()) {
            updateStateList()
        } else {
            homeState.value = homeState.value.copy(
                totpList = homeState.value.totpList.map { it.copy(secondsLeft = currentSecondsLeft) }
            )
        }
    }

    private fun updateStateList(keyList: List<EncryptedTotpKey> = totpKeyFlow.value) {
        val list = keyList.map {
            TotpCardState(
                it.id,
                it.name,
                generateTotpCodeUseCase(it),
                countSecondsLeft()
            )
        }.toList()
        homeState.value = homeState.value.copy(totpList = list)
    }

    private fun countSecondsLeft(
        currentTime: Long = System.currentTimeMillis(),
        timeStep: Long = defaultUpdateStepMs
    ): Int {
        return ((timeStep - currentTime % timeStep).toDouble() / 1000).roundToInt()
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
