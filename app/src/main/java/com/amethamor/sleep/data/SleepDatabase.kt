package com.amethamor.sleep.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SleepRecord::class],
    version = 3,
    exportSchema = false
)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepRecordDao(): SleepRecordDao

    companion object {
        @Volatile
        private var INSTANCE: SleepDatabase? = null
        const val DATABASE_NAME = "sleep_database"

        fun getDatabase(context: Context): SleepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SleepDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun closeDatabase() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE sleep_records ADD COLUMN sleepType TEXT NOT NULL DEFAULT 'NIGHT'")
                db.execSQL("ALTER TABLE sleep_records ADD COLUMN sleepTypeManuallySet INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
