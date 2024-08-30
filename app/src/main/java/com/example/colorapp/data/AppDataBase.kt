package com.example.colorapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [ColorEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun colorDao(): ColorDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE new_colors (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        time TEXT NOT NULL,
                        synced INTEGER NOT NULL DEFAULT 0,
                        color TEXT NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO new_colors (time, color)
                    SELECT time, color FROM colors
                """.trimIndent())

                database.execSQL("DROP TABLE colors")
                database.execSQL("ALTER TABLE new_colors RENAME TO colors")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "colors_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
