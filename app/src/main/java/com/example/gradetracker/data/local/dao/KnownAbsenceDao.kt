package com.example.gradetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gradetracker.data.local.KnownAbsenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnownAbsenceDao {

    @Query("SELECT absenceId FROM known_absences")
    suspend fun getKnownIds(): List<Int>

    @Query(
        """
        SELECT absenceId
        FROM known_absences
        WHERE isUnread = 1
        """
    )
    fun observeUnreadIds(): Flow<List<Int>>

    @Query(
        """
        SELECT absenceId
        FROM known_absences
        WHERE isUnread = 1 AND notificationSent = 0
        """
    )
    suspend fun getPendingNotificationIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(absences: List<KnownAbsenceEntity>)

    @Query(
        """
        UPDATE known_absences
        SET isUnread = 0, notificationSent = 1
        WHERE absenceId = :absenceId
        """
    )
    suspend fun markAsRead(absenceId: Int)

    @Query(
        """
        UPDATE known_absences
        SET isUnread = 0, notificationSent = 1
        WHERE absenceId IN (:absenceIds)
        """
    )
    suspend fun markAsRead(absenceIds: List<Int>)

    @Query(
        """
        UPDATE known_absences
        SET isUnread = 0, notificationSent = 1
        """
    )
    suspend fun markAllAsRead()

    @Query(
        """
        UPDATE known_absences
        SET notificationSent = 1
        WHERE absenceId IN (:absenceIds)
        """
    )
    suspend fun markNotificationsSent(absenceIds: List<Int>)

    @Query("UPDATE known_absences SET notificationSent = 1")
    suspend fun markAllNotificationsSent()

    @Query("DELETE FROM known_absences")
    suspend fun clear()
}
