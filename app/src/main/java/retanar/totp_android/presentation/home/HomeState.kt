package retanar.totp_android.presentation.home

data class HomeState(
    val totpList: List<TotpCardState> = emptyList(),
    val editingTotp: EditTotpState? = null,
)

data class TotpCardState(
    val id: Int,
    val name: String,
    val oneTimeCode: Int,
    val secondsLeft: Int,
)

data class EditTotpState(
    val id: Int,
    val name: String,
    val base32Secret: String,
)