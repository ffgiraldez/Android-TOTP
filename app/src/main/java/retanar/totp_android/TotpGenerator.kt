package retanar.totp_android

import kotlin.time.Duration

interface TotpGenerator {
    fun generateTotp(secret: ByteArray, unixTimeSeconds: Duration): Int
}