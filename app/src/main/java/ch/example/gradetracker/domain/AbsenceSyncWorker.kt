package ch.example.gradetracker.domain

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.gradetracker.data.local.database.DatabaseProvider
import com.example.gradetracker.data.remote.NetworkClient
import com.example.gradetracker.data.remote.SharedPreferencesTokenStore
import com.example.gradetracker.data.repository.StudentRepository
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

class AbsenceSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(
    appContext,
    workerParameters
) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Absenz-Synchronisation gestartet")

        val tokenStore = SharedPreferencesTokenStore(
            applicationContext
        )

        if (tokenStore.getToken().isNullOrBlank()) {
            Log.d(TAG, "Kein Token vorhanden; Synchronisation beendet")
            return Result.success()
        }

        val database = DatabaseProvider.getDatabase(
            applicationContext
        )

        val knownAbsenceDao = database.knownAbsenceDao()

        val repository = StudentRepository(
            api = NetworkClient.lerbermattApi,
            tokenStore = tokenStore,
            knownAbsenceDao = knownAbsenceDao
        )

        return try {
            val hadKnownAbsencesBeforeSync =
                knownAbsenceDao.getKnownIds().isNotEmpty()

            repository.getAbsences()

            val preferences = applicationContext.getSharedPreferences(
                "absence_sync",
                Context.MODE_PRIVATE
            )
            val initialSyncCompleted = preferences.getBoolean(
                "initial_sync_completed",
                false
            )
            val pendingNotificationIds =
                knownAbsenceDao.getPendingNotificationIds()

            if (
                !initialSyncCompleted &&
                !hadKnownAbsencesBeforeSync
            ) {
                knownAbsenceDao.markAllNotificationsSent()
                Log.d(
                    TAG,
                    "Erste Synchronisation als Ausgangsbestand gespeichert"
                )
            } else if (pendingNotificationIds.isNotEmpty()) {
                val notificationWasShown = AbsenceNotifications.show(
                    context = applicationContext,
                    newAbsenceCount = pendingNotificationIds.size
                )

                if (notificationWasShown) {
                    knownAbsenceDao.markNotificationsSent(
                        pendingNotificationIds
                    )
                    Log.d(
                        TAG,
                        "${pendingNotificationIds.size} neue Absenzen gemeldet"
                    )
                } else {
                    Log.w(
                        TAG,
                        "Mitteilung nicht erlaubt oder Kanal deaktiviert"
                    )
                }
            } else {
                Log.d(TAG, "Keine neuen Absenzen gefunden")
            }

            preferences.edit {
                putBoolean("initial_sync_completed", true)
            }

            Result.success()
        } catch (exception: IOException) {
            Log.w(TAG, "Netzwerkfehler; neuer Versuch folgt", exception)
            Result.retry()
        } catch (exception: HttpException) {
            Log.w(TAG, "HTTP-Fehler ${exception.code()}", exception)
            if (exception.code() >= 500) {
                Result.retry()
            } else {
                Result.failure()
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Absenz-Synchronisation fehlgeschlagen", exception)
            Result.failure()
        }
    }

    private companion object {
        const val TAG = "AbsenceSyncWorker"
    }
}

fun scheduleAbsenceSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request =
        PeriodicWorkRequestBuilder<AbsenceSyncWorker>(
            30,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.MINUTES
            )
            .build()

    WorkManager
        .getInstance(context)
        .enqueueUniquePeriodicWork(
            "absence_sync",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

    scheduleImmediateAbsenceSync(context)
}

fun scheduleImmediateAbsenceSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val request = OneTimeWorkRequestBuilder<AbsenceSyncWorker>()
        .setConstraints(constraints)
        .build()

    WorkManager
        .getInstance(context)
        .enqueueUniqueWork(
            "absence_sync_immediate",
            ExistingWorkPolicy.REPLACE,
            request
        )
}
