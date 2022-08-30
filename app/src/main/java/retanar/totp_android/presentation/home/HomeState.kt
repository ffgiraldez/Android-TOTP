package retanar.totp_android.presentation.home

data class HomeState(
    val totpList: List<TotpCardState> = emptyList(),
)

data class TotpCardState(
    val id: Int,
    val name: String,
    val oneTimeCode: Int,
)