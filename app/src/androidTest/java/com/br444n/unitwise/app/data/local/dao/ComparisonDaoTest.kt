package com.br444n.unitwise.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.br444n.unitwise.app.data.local.AppDatabase
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumentation tests for [ComparisonDao].
 */
@RunWith(AndroidJUnit4::class)
class ComparisonDaoTest {

    private lateinit var dao: ComparisonDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.comparisonDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeComparisonAndReadInList() = runBlocking {
        // Given
        val comparison = ComparisonEntity(
            productAName = "Product A",
            productBName = "Product B",
            productAContent = "1",
            productBContent = "2",
            productAUnit = "kg",
            productBUnit = "kg",
            productAPrice = "10",
            productBPrice = "15",
            productAQuantity = "1",
            productBQuantity = "1"
        )
        
        // When
        dao.insertComparison(comparison)
        
        // Then
        val allComparisons = dao.getAllComparisons().first()
        assertThat(allComparisons).hasSize(1)
        assertThat(allComparisons[0].productAName).isEqualTo("Product A")
    }

    @Test
    fun getComparisonById() = runBlocking {
        // Given
        val comparison = ComparisonEntity(
            id = 42,
            productAName = "Test",
            productBName = "Test",
            productAContent = "1",
            productBContent = "1",
            productAUnit = "g",
            productBUnit = "g",
            productAPrice = "1",
            productBPrice = "1",
            productAQuantity = "1",
            productBQuantity = "1"
        )
        
        // When
        dao.insertComparison(comparison)
        val loaded = dao.getComparisonById(42)
        
        // Then
        assertThat(loaded).isNotNull()
        assertThat(loaded?.productAName).isEqualTo("Test")
    }

    @Test
    fun deleteAllComparisons_emptiesTheTable() = runBlocking {
        // Given
        dao.insertComparison(ComparisonEntity(productAName = "A", productBName = "B", productAContent = "1", productBContent = "2", productAUnit = "g", productBUnit = "g", productAPrice = "1", productBPrice = "1", productAQuantity = "1", productBQuantity = "1"))
        
        // When
        dao.deleteAllComparisons()
        
        // Then
        val allComparisons = dao.getAllComparisons().first()
        assertThat(allComparisons).isEmpty()
    }
}
