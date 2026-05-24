package com.br444n.unitwise.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListItemDao {
    @Query("SELECT * FROM shopping_list_items WHERE listId = :listId ORDER BY id ASC")
    fun getItemsForList(listId: Int): Flow<List<ShoppingListItemEntity>>

    @Query("SELECT * FROM shopping_list_items WHERE id = :itemId")
    fun getItemById(itemId: Int): Flow<ShoppingListItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingListItemEntity): Long

    @Update
    suspend fun updateItem(item: ShoppingListItemEntity)

    @Query("DELETE FROM shopping_list_items WHERE id IN (:ids)")
    suspend fun deleteItems(ids: Set<Int>)
}
