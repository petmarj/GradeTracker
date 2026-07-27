package com.example.gradetracker.data.preferences

import android.content.Context
import androidx.core.content.edit
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SortPreferences(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(
            "sort_settings",
            Context.MODE_PRIVATE
        )

    private val _subjectSort = MutableStateFlow(
        readEnum("subject_sort", SubjectSort.NEWEST)
    )
    val subjectSort: StateFlow<SubjectSort> =
        _subjectSort.asStateFlow()

    private val _gradeSort = MutableStateFlow(
        readEnum("grade_sort", GradeSort.NEWEST)
    )
    val gradeSort: StateFlow<GradeSort> =
        _gradeSort.asStateFlow()

    fun setSubjectSort(sort: SubjectSort) {
        preferences.edit {
            putString("subject_sort", sort.name)
        }

        _subjectSort.value = sort
    }

    fun setGradeSort(sort: GradeSort) {
        preferences.edit {
            putString("grade_sort", sort.name)
        }

        _gradeSort.value = sort
    }

    private inline fun <reified T : Enum<T>> readEnum(
        key: String,
        default: T
    ): T {
        val savedValue = preferences.getString(key, null)
            ?: return default

        return enumValues<T>()
            .firstOrNull { it.name == savedValue }
            ?: default
    }
}