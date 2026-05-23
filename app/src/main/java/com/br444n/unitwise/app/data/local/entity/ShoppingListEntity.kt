package com.br444n.unitwise.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val colorBadge: Int,
    val timestamp: Long,
    val supermarketAName: String = "Súper A",
    val supermarketBName: String = "Súper B"
)
