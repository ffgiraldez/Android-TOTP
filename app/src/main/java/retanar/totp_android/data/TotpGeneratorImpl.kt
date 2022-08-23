package retanar.totp_android.data

import retanar.totp_android.domain.TotpGenerator
import kotlin.time.Duration

class TotpGeneratorImpl : TotpGenerator {
    override fun generateTotp(secret: ByteArray, unixTime: Duration): Int {
        TODO("Creating or importing generator logic is needed")
    }
}