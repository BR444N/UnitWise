package com.br444n.unitwise.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.br444n.unitwise.app.data.local.dao.ComparisonDao
import com.br444n.unitwise.app.data.local.dao.ShoppingListDao
import com.br444n.unitwise.app.data.local.dao.ShoppingListItemDao
import com.br444n.unitwise.app.data.local.entity.ComparisonEntity
import com.br444n.unitwise.app.data.local.entity.ShoppingListEntity
import com.br444n.unitwise.app.data.local.entity.ShoppingListItemEntity

@Database(
    entities = [
        ComparisonEntity::class,
        ShoppingListEntity::class,
        ShoppingListItemEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun comparisonDao(): ComparisonDao

    abstract fun shoppingListDao(): ShoppingListDao

    abstract fun shoppingListItemDao(): ShoppingListItemDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                val newInstance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "unitwise_database",
                        ).addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3,
                            MIGRATION_3_4,
                            MIGRATION_4_5,
                            MIGRATION_5_6,
                        ).build()
                instance = newInstance
                newInstance
            }

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "ALTER TABLE comparisons ADD COLUMN shareId TEXT NOT NULL DEFAULT ''",
                    )
                    db.execSQL(
                        "UPDATE comparisons SET shareId = 'CMP' || id WHERE shareId = ''",
                    )
                    db.execSQL(
                        "CREATE UNIQUE INDEX IF NOT EXISTS index_comparisons_shareId ON comparisons(shareId)",
                    )
                }
            }

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {
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
                        """.trimIndent(),
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
                        """.trimIndent(),
                    )
                    db.execSQL("DROP TABLE comparisons")
                    db.execSQL("ALTER TABLE comparisons_new RENAME TO comparisons")
                    db.execSQL(
                        "CREATE UNIQUE INDEX IF NOT EXISTS index_comparisons_shareId ON comparisons(shareId)",
                    )
                }
            }

        private val MIGRATION_3_4 =
            object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS shopping_lists (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            colorBadge INTEGER NOT NULL,
                            timestamp INTEGER NOT NULL,
                            supermarketAName TEXT NOT NULL,
                            supermarketBName TEXT NOT NULL
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS shopping_list_items (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            listId INTEGER NOT NULL,
                            categoryName TEXT NOT NULL,
                            productAName TEXT NOT NULL,
                            productAPrice TEXT NOT NULL,
                            productBName TEXT NOT NULL,
                            productBPrice TEXT NOT NULL,
                            FOREIGN KEY(listId) REFERENCES shopping_lists(id) ON UPDATE NO ACTION ON DELETE CASCADE
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        "CREATE INDEX IF NOT EXISTS index_shopping_list_items_listId ON shopping_list_items(listId)",
                    )
                }
            }

        private val MIGRATION_4_5 =
            object : Migration(4, 5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN isProductAWinner INTEGER",
                    )
                    db.execSQL("ALTER TABLE shopping_list_items ADD COLUMN isTie INTEGER")
                }
            }

        private val MIGRATION_5_6 =
            object : Migration(5, 6) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN productAContent TEXT NOT NULL DEFAULT ''",
                    )
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN productAUnit TEXT NOT NULL DEFAULT ''",
                    )
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN productAQuantity TEXT NOT NULL DEFAULT ''",
                    )
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN productBContent TEXT NOT NULL DEFAULT ''",
                    )
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN productBUnit TEXT NOT NULL DEFAULT ''",
                    )
                    db.execSQL(
                        "ALTER TABLE shopping_list_items ADD COLUMN productBQuantity TEXT NOT NULL DEFAULT ''",
                    )
                }
            }
    }
}
