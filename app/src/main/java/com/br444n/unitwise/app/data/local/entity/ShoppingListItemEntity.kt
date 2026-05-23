package com.br444n.unitwise.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_list_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["listId"])]
)
data class ShoppingListItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val listId: Int,
    val categoryName: String,
    val productAName: String = "",
    val productAPrice: String = "",
    val productAContent: String = "",
    val productAUnit: String = "",
    val productAQuantity: String = "",
    val productBName: String = "",
    val productBPrice: String = "",
    val productBContent: String = "",
    val productBUnit: String = "",
    val productBQuantity: String = "",
    val isProductAWinner: Boolean? = null,
    val isTie: Boolean? = null
)
