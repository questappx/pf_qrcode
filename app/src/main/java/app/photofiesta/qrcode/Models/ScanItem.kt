package app.photofiesta.qrcode.Models


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

private const val TABLE_NAME = "scanned_items"
private const val DATABASE_NAME = "my_app_database"

@Entity(tableName = "scanned_items")
data class ScanItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val timestamp: Long,
    val qrType: Int,
    val isFavorite: Boolean,
    val isGenerated: Boolean
)

//object DatabaseProvider {
//
//    private var database: MyAppDatabase? = null
//
//    fun getDatabase(context: Context): MyAppDatabase {
//        return database ?: synchronized(this) {
//            val instance = Room.databaseBuilder(
//                context.applicationContext,
//                MyAppDatabase::class.java,
//                DATABASE_NAME
//            ).build()
//            database = instance
//            instance
//        }
//    }
//}


@Dao
interface ScanItemDao {

    @Insert
    suspend fun insertScanItem(scanItem: ScanItem)

    @Query("SELECT * FROM $TABLE_NAME WHERE isGenerated = 0 ORDER BY timestamp DESC")
    fun getAllScannedItems(): LiveData<List<ScanItem>>

    @Query("SELECT * FROM $TABLE_NAME WHERE isGenerated = 1 ORDER BY timestamp DESC")
    fun getAllGeneratedItems(): LiveData<List<ScanItem>>

    @Query("DELETE FROM $TABLE_NAME WHERE id = :itemId")
    suspend fun deleteScanItem(itemId: Long)

    @Query("UPDATE $TABLE_NAME SET isFavorite = :isFavorite WHERE id = :itemId")
    suspend fun updateFavoriteStatus(itemId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM $TABLE_NAME WHERE isFavorite = 1 AND isGenerated = 0 ORDER BY timestamp DESC")
    fun getAllFavoriteScannedItems(): LiveData<List<ScanItem>>

    @Query("SELECT * FROM $TABLE_NAME WHERE isFavorite = 1 AND isGenerated = 1 ORDER BY timestamp DESC")
    fun getAllFavoriteGeneratedItems(): LiveData<List<ScanItem>>
}

@Database(entities = [ScanItem::class], version = 1)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun scanItemDao(): ScanItemDao
}

