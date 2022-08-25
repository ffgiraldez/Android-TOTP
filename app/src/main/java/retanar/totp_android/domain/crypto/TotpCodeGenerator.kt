package retanar.totp_android.domain.crypto

import kotlin.time.Duration

interface TotpCodeGenerator {
    fun generate(secret: ByteArray, unixTime: Duration): Int
}