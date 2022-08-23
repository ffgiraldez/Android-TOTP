package retanar.totp_android.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Set `id` to 0 for autogeneration*/
@Entity(tableName = totpTableName)
data class TotpDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val secret: ByteArray,
)
