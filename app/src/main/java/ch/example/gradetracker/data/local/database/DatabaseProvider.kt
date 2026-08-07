package ch.example.gradetracker.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private val migration7To8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `known_absences` (
                    `absenceId` INTEGER NOT NULL,
                    `firstSeenAt` INTEGER NOT NULL,
                    `isUnread` INTEGER NOT NULL,
                    PRIMARY KEY(`absenceId`)
                )
                """.trimIndent()
            )
        }
    }

    private val migration8To9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE `known_absences`
                ADD COLUMN `notificationSent` INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
            db.execSQL(
                """
                UPDATE `known_absences`
                SET `notificationSent` = 1
                """.trimIndent()
            )
        }
    }

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "grade_tracker_db"
            )
                .addMigrations(
                    migration7To8,
                    migration8To9
                )
                .fallbackToDestructiveMigration(true)
                .build()

            INSTANCE = instance
            instance
        }
    }
}
