package com.reneprojects.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `categories` (
                    `slug` TEXT NOT NULL,
                    `name` TEXT NOT NULL,
                    PRIMARY KEY(`slug`)
                )
                """.trimIndent()
            )
        }
    }
}
