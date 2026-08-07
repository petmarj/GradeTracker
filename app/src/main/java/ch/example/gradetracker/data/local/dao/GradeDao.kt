package ch.example.gradetracker.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gradetracker.model.Grade
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {

    @Query("SELECT * FROM Grade")
    fun getAll(): Flow<List<Grade>>

    @Insert
    suspend fun insert(grade: Grade)

    @Update
    suspend fun update(grade: Grade)

    @Query("DELETE FROM Grade WHERE id = :id")
    suspend fun deleteById(id: String?)

    @Query("SELECT * FROM Grade WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): Grade?

    @Query("SELECT * FROM Grade WHERE subjectId = :id")
    fun getGradesBySubject(id: String?): Flow<List<Grade>>


}