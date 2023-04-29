package retanar.totp_android.presentation.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retanar.totp_android.data.database.TotpDao
import retanar.totp_android.data.database.TotpDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideTotpDatabase(@ApplicationContext context: Context): TotpDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TotpDatabase::class.java,
            "totp_database"
        ).build()
    }

    @Provides
    fun provideTotpDao(db: TotpDatabase): TotpDao = db.totpDao
}