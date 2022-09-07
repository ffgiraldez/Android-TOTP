package retanar.totp_android

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retanar.totp_android.data.database.TotpDao
import retanar.totp_android.data.database.TotpDatabase
import retanar.totp_android.data.database.TotpDbEntity

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {
    private lateinit var db: TotpDatabase
    private lateinit var totpDao: TotpDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TotpDatabase::class.java).build()
        totpDao = db.totpDao
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndQuery() = runBlocking {
        val key = TotpDbEntity(0, "name", "secret".toByteArray(), ByteArray(8))
        totpDao.insert(key)
        totpDao.insert(key)

        val query = totpDao.queryAll().first()
        assertEquals(2, query.size)
        assertTrue(query.none { entity -> entity.id == 0 })
        assertTrue(query.all { entity -> entity.copy(id = 0) == key })
    }

    @Test
    fun insertWithConflict() = runBlocking {
        val key = TotpDbEntity(101, "name", "secret".toByteArray(), ByteArray(8))
        totpDao.insert(key)
        totpDao.insert(key)

        val query1 = totpDao.queryAll().first()
        assertEquals(1, query1.size)

        val changedKey = key.copy(name = "changed", iv = ByteArray(0) { it.toByte() })
        totpDao.insert(changedKey)

        val query2 = totpDao.queryAll().first()
        assertEquals(1, query2.size)
        assertTrue(query2.contains(changedKey))
        assertFalse(query2.contains(key))
    }

    @Test
    fun update() = runBlocking {
        val originalKey = TotpDbEntity(101, "name", "secret".toByteArray(), ByteArray(8))
        totpDao.insert(originalKey)
        val query1 = totpDao.queryAll().first()
        assertTrue(query1.contains(originalKey))

        val changedKey = originalKey.copy(secret = "changed".toByteArray(), iv = "01234567".toByteArray())
        totpDao.update(changedKey)
        val query2 = totpDao.queryAll().first()
        println("Query size ${query2.size}")
        assertTrue(query2.contains(changedKey))
        assertFalse(query2.contains(originalKey))
    }

    @Test
    fun remove() = runBlocking {
        val key = TotpDbEntity(102, "name", "secret".toByteArray(), ByteArray(8))
        totpDao.insert(key)
        totpDao.delete(key)

        val query = totpDao.queryAll().first()
        assertEquals(0, query.size)
    }
}