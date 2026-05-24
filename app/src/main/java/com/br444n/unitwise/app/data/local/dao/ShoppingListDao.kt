package com.br444n.unitwise.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Embedded
import androidx.room.ColumnInfo
import com.br444n.unitwise.app.data.local.entity.ShoppingListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("""
        SELECT l.*, COUNT(i.id) as itemCount 
        FROM shopping_lists l 
        LEFT JOIN shopping_list_items i ON l.id = i.listId 
        GROUP BY l.id 
        ORDER BY l.timestamp DESC
    """)
    fun getAllListsWithCount(): Flow<List<ShoppingListWithItemCount>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    fun getListById(id: Int): Flow<ShoppingListEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: ShoppingListEntity): Long

    @Query("DELETE FROM shopping_lists WHERE id IN (:ids)")
    suspend fun deleteLists(ids: Set<Int>)
}

data class ShoppingListWithItemCount(
    @Embedded val list: ShoppingListEntity,
    @ColumnInfo(name = "itemCount") val itemCount: Int
)
