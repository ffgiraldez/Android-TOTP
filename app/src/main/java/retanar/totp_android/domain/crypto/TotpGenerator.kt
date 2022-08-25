package retanar.totp_android.domain.crypto

import kotlin.time.Duration

interface TotpGenerator {
    fun generateTotp(secret: ByteArray, unixTime: Duration): Int
}