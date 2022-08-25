package retanar.totp_android.data.crypto

import retanar.totp_android.domain.crypto.TotpGenerator
import kotlin.time.Duration

class TotpGeneratorImpl : TotpGenerator {
    override fun generateTotp(secret: ByteArray, unixTime: Duration): Int {
        TODO("Creating or importing generator logic is needed")
    }
}