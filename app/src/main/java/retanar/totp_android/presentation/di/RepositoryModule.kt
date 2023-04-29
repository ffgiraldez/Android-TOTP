package retanar.totp_android.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retanar.totp_android.data.database.TotpDao
import retanar.totp_android.data.repository.TotpKeyRepositoryImpl
import retanar.totp_android.domain.repository.TotpKeyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideTotpKeyRepository(dao: TotpDao): TotpKeyRepository {
        return TotpKeyRepositoryImpl(dao)
    }
}