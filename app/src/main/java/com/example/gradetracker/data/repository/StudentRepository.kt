package com.example.gradetracker.data.repository

import com.example.gradetracker.data.local.KnownAbsenceEntity
import com.example.gradetracker.data.local.dao.KnownAbsenceDao
import com.example.gradetracker.data.remote.LerbermattApi
import com.example.gradetracker.data.remote.TokenStore
import com.example.gradetracker.data.remote.model.AbsencesRequest
import com.example.gradetracker.data.remote.model.AbsencesResponse
import com.example.gradetracker.data.remote.model.MaxHalfdayResponse
import com.example.gradetracker.data.remote.model.StudentResponse
import com.example.gradetracker.model.Absence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class StudentRepository(
    private val api: LerbermattApi,
    private val tokenStore: TokenStore,
    private val knownAbsenceDao: KnownAbsenceDao
) {
    suspend fun getStudentData(): StudentResponse{
        val token = tokenStore.getToken()
        val authorization = if (token?.startsWith("Bearer ") ?: false) {
            token
        } else {
            "Bearer $token"
        }
        val response = try {
            api.getStudent(
                authorization = authorization
            )
        } catch (exception: IOException) {
            throw IOException(
                "Die Verbindung zur API ist fehlgeschlagen.",
                exception
            )
        }
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val body = response.body()
            ?: throw IllegalStateException(
                "Die API hat keine Antwortdaten geliefert."
            )

        if (body.error != "NoError") {
            throw IllegalStateException(
                "API-Fehler: ${body.error}"
            )
        }

        return body
    }

    suspend fun getMaxHalfdayAmount(): MaxHalfdayResponse{
        val token = tokenStore.getToken()
        val authorization = if (token?.startsWith("Bearer ") ?: false) {
            token
        } else {
            "Bearer $token"
        }
        val response = try {
            api.getMaxHalfdayAmount(
                authorization = authorization,
                request = emptyMap()
            )
        } catch (exception: IOException) {
            throw IOException(
                "Die Verbindung zur API ist fehlgeschlagen.",
                exception
            )
        }
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val body = response.body()
            ?: throw IllegalStateException(
                "Die API hat keine Antwortdaten geliefert."
            )

        if (body.error != "NoError") {
            throw IllegalStateException(
                "API-Fehler: ${body.error}"
            )
        }

        return body
    }
    suspend fun getAbsences(): AbsencesResponse {
        val token = tokenStore.getToken()
        val authorization = if (token?.startsWith("Bearer ") ?: false) {
            token
        } else {
            "Bearer $token"
        }
        val response = try {
            api.getAbsences(
                authorization = authorization,
                request = AbsencesRequest(
                    types = listOf(0,1,2,3)
                )
            )
        } catch (exception: IOException) {
            throw IOException(
                "Die Verbindung zur API ist fehlgeschlagen.",
                exception
            )
        }
        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        val body = response.body()
            ?: throw IllegalStateException(
                "Die API hat keine Antwortdaten geliefert."
            )

        if (body.error != "NoError") {
            throw IllegalStateException(
                "API-Fehler: ${body.error}"
            )
        }

        registerAbsences(body.data.absences)

        return body
    }

    fun observeUnreadAbsenceIds(): Flow<Set<Int>> =
        knownAbsenceDao.observeUnreadIds().map { ids ->
            ids.toSet()
        }

    suspend fun markAbsenceAsRead(absenceId: Int) {
        knownAbsenceDao.markAsRead(absenceId)
    }

    suspend fun markAbsencesAsRead(absenceIds: List<Int>) {
        if (absenceIds.isEmpty()) return
        knownAbsenceDao.markAsRead(absenceIds)
    }

    suspend fun markAllAbsencesAsRead() {
        knownAbsenceDao.markAllAsRead()
    }

    private suspend fun registerAbsences(absences: List<Absence>) {
        val knownIds = knownAbsenceDao.getKnownIds().toSet()

        val newAbsences = absences
            .distinctBy { absence -> absence.id }
            .filter { absence -> absence.id !in knownIds }

        if (newAbsences.isEmpty()) return

        knownAbsenceDao.insertAll(
            newAbsences.map { absence ->
                KnownAbsenceEntity(
                    absenceId = absence.id,
                    isUnread = true
                )
            }
        )
    }
}
