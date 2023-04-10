package net.rhubarbdev.android.timetablegenerator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item_table")
    fun getItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Query("DELETE FROM item_table WHERE itemID = :item_id")
    suspend fun delete(item_id : Int)

    @Query("SELECT  * FROM item_table WHERE itemID = :item_id")
    suspend fun getItem(item_id: Int): Item

    @Query("DELETE FROM item_table")
    suspend fun deleteAll()
}