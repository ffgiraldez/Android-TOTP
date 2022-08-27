package retanar.totp_android.presentation

data class HomeState(
    val totpList: List<TotpCardState> = emptyList(),
)

data class TotpCardState(
    val id: Int,
    val name: String,
    val oneTimeCode: Int,
)