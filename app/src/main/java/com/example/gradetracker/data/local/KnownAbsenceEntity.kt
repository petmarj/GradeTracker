package com.example.gradetracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "known_absences")
data class KnownAbsenceEntity(
    @PrimaryKey
    val absenceId: Int,
    val firstSeenAt: Long = System.currentTimeMillis(),
    val isUnread: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val notificationSent: Boolean = false
)
