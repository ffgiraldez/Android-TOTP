package retanar.totp_android.data.database

import androidx.room.*

const val totpTableName = "totp"

@Dao
interface TotpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(totp: TotpDbEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(totp: TotpDbEntity)

    @Delete
    suspend fun delete(totp: TotpDbEntity)

    @Query("SELECT * FROM $totpTableName")
    suspend fun queryAll(): List<TotpDbEntity>
}