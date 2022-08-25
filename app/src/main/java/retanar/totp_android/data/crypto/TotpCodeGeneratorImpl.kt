package retanar.totp_android.data.crypto

import retanar.totp_android.domain.crypto.TotpCodeGenerator
import kotlin.time.Duration

class TotpCodeGeneratorImpl : TotpCodeGenerator {
    override fun generate(secret: ByteArray, unixTime: Duration): Int {
        TODO("Creating or importing generator logic is needed")
    }
}