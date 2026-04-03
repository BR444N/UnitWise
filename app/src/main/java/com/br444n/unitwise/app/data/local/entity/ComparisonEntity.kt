package com.br444n.unitwise.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comparisons",
    indices = [Index(value = ["shareId"], unique = true)]
)
data class ComparisonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(defaultValue = "''")
    val shareId: String,
    val timestamp: Long = System.currentTimeMillis(),
    
    // Product A
    val productAName: String,
    val productAContent: String,
    val productAUnit: String,
    val productAPrice: String,
    val productAQuantity: String,
    
    // Product B
    val productBName: String,
    val productBContent: String,
    val productBUnit: String,
    val productBPrice: String,
    val productBQuantity: String
)
