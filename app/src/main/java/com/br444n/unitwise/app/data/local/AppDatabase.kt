package com.br444n.unitwise.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import com.br444n.unitwise.app.data.local.dao.ComparisonDao
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity

@Database(entities = [ComparisonEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun comparisonDao(): ComparisonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unitwise_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE comparisons ADD COLUMN shareId TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "UPDATE comparisons SET shareId = 'CMP' || id WHERE shareId = ''"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_comparisons_shareId ON comparisons(shareId)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS comparisons_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        shareId TEXT NOT NULL DEFAULT '',
                        timestamp INTEGER NOT NULL,
                        productAName TEXT NOT NULL,
                        productAContent TEXT NOT NULL,
                        productAUnit TEXT NOT NULL,
                        productAPrice TEXT NOT NULL,
                        productAQuantity TEXT NOT NULL,
                        productBName TEXT NOT NULL,
                        productBContent TEXT NOT NULL,
                        productBUnit TEXT NOT NULL,
                        productBPrice TEXT NOT NULL,
                        productBQuantity TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO comparisons_new (
                        id,
                        shareId,
                        timestamp,
                        productAName,
                        productAContent,
                        productAUnit,
                        productAPrice,
                        productAQuantity,
                        productBName,
                        productBContent,
                        productBUnit,
                        productBPrice,
                        productBQuantity
                    )
                    SELECT
                        id,
                        COALESCE(NULLIF(shareId, ''), 'CMP' || id),
                        timestamp,
                        productAName,
                        productAContent,
                        productAUnit,
                        productAPrice,
                        productAQuantity,
                        productBName,
                        productBContent,
                        productBUnit,
                        productBPrice,
                        productBQuantity
                    FROM comparisons
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE comparisons")
                db.execSQL("ALTER TABLE comparisons_new RENAME TO comparisons")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_comparisons_shareId ON comparisons(shareId)"
                )
            }
        }
    }
}
