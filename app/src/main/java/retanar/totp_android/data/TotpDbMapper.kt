package retanar.totp_android.data

import retanar.totp_android.data.database.TotpDbEntity
import retanar.totp_android.domain.entities.TotpKey

object TotpDbMapper {
    fun fromTotpKey(totpKey: TotpKey) = TotpDbEntity(
        name = totpKey.name,
        secret = totpKey.secret,
    )

    fun toTotpKey(totpEntity: TotpDbEntity) = TotpKey(
        name = totpEntity.name,
        secret = totpEntity.secret,
    )
}