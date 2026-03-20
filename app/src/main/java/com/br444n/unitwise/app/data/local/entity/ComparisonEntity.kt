package com.br444n.unitwise.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comparisons")
data class ComparisonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
