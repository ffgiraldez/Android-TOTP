package retanar.totp_android

import org.junit.Assert.assertEquals
import org.junit.Test
import retanar.totp_android.data.crypto.TotpCodeGeneratorImpl
import kotlin.time.Duration.Companion.seconds

class TotpCodeGeneratorTest {
    private val generator = TotpCodeGeneratorImpl()

    @Test
    fun rfcSha1Tests() {
        val timesSec = listOf(59, 1111111109, 1111111111, 1234567890, 2000000000, 20000000000)
        val secret = "12345678901234567890".toByteArray(Charsets.US_ASCII)
        val expected = listOf(287082, 81804, 50471, 5924, 279037, 353130)

        timesSec.zip(expected).forEach { (duration, expectedCode) ->
            val actual = generator.generate(secret, duration.seconds)
            assertEquals(expectedCode, actual)
        }
    }
}