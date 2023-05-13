package retanar.totp_android.domain.entities

import kotlinx.serialization.Serializable
import retanar.totp_android.domain.usecases.SavingMode

@Serializable
class NoEncryptionExport(
    val keysList: List<UnencryptedKey>,
    val savingMode: SavingMode = SavingMode.NoEncryption,
)

@Serializable
class UnencryptedKey(
    val name: String,
    val base32Secret: String,
)

@Serializable
class KeyEncryptionExport(
    val keysList: List<EncryptedKey>,
    val savingMode: SavingMode = SavingMode.KeyEncryption,
)

@Serializable
class EncryptedKey(
    val name: String,
    val base64Secret: String,
    val base64Iv: String,
)

@Serializable
class FullEncryptionExport(
    // decodes to List<UnencryptedKey>
    val base64Data: String,
    val base64Iv: String,
    val savingMode: SavingMode = SavingMode.FullEncryption,
)