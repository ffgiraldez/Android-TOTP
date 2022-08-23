package retanar.totp_android.data.database

import retanar.totp_android.domain.entities.TotpKey

object TotpDbMapper {
    fun fromTotpKey(totpKey: TotpKey) = TotpDbEntity(
        id = totpKey.id,
        name = totpKey.name,
        secret = totpKey.secret,
    )

    fun toTotpKey(totpEntity: TotpDbEntity) = TotpKey(
        id = totpEntity.id,
        name = totpEntity.name,
        secret = totpEntity.secret,
    )
}