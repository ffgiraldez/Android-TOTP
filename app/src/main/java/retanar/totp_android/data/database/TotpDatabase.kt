package retanar.totp_android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TotpDbEntity::class], version = 1, exportSchema = false)
abstract class TotpDatabase : RoomDatabase() {
    abstract val totpDao: TotpDao

    companion object {
        @Volatile
        private var INSTANCE: TotpDatabase? = null

        fun getDatabase(context: Context): TotpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TotpDatabase::class.java,
                    "totp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}