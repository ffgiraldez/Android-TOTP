package retanar.totp_android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TotpDbEntity::class], version = 1, exportSchema = false)
abstract class TotpDatabase : RoomDatabase() {
    abstract val totpDao: TotpDao

    companion object {
        const val DATABASE_NAME = "totp_database"
    }
}