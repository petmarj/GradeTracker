package com.example.gradetracker.data.preferences

import android.content.Context
import androidx.core.content.edit
import com.example.gradetracker.model.GradeColorMode
import com.example.gradetracker.model.GradeSort
import com.example.gradetracker.model.SubjectSort
import com.example.gradetracker.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppPreferences(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )

    private val legacySortPreferences = context.applicationContext
        .getSharedPreferences(
            LEGACY_SORT_FILE_NAME,
            Context.MODE_PRIVATE
        )

    init {
        migrateLegacyPreferences()
    }

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        preferences.edit {
            putString(KEY_THEME_MODE, mode.name)
        }

        _settings.update {
            it.copy(themeMode = mode)
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        preferences.edit {
            putBoolean(KEY_DYNAMIC_COLORS, enabled)
        }

        _settings.update {
            it.copy(dynamicColors = enabled)
        }
    }

    fun setSubjectSort(sort: SubjectSort) {
        preferences.edit {
            putString(KEY_SUBJECT_SORT, sort.name)
        }

        _settings.update {
            it.copy(subjectSort = sort)
        }
    }

    fun setGradeSort(sort: GradeSort) {
        preferences.edit {
            putString(KEY_GRADE_SORT, sort.name)
        }

        _settings.update {
            it.copy(gradeSort = sort)
        }
    }

    fun setGradeColorMode(mode: GradeColorMode) {
        preferences.edit {
            putString(KEY_GRADE_COLOR_MODE, mode.name)
        }

        _settings.update {
            it.copy(gradeColorMode = mode)
        }
    }

    private fun loadSettings(): AppSettings {
        return AppSettings(
            themeMode = readEnum(
                key = KEY_THEME_MODE,
                default = ThemeMode.SYSTEM
            ),
            dynamicColors = preferences.getBoolean(
                KEY_DYNAMIC_COLORS,
                true
            ),
            subjectSort = readEnum(
                key = KEY_SUBJECT_SORT,
                default = SubjectSort.NEWEST
            ),
            gradeSort = readEnum(
                key = KEY_GRADE_SORT,
                default = GradeSort.NEWEST
            ),
            gradeColorMode = readEnum(
                key = KEY_GRADE_COLOR_MODE,
                default = GradeColorMode.NORMAL
            )
        )
    }

    private fun migrateLegacyPreferences() {
        val legacyThemeMode = preferences.getString(
            LEGACY_KEY_THEME_MODE,
            null
        )
        val legacySubjectSort = legacySortPreferences.getString(
            LEGACY_KEY_SUBJECT_SORT,
            null
        )
        val legacyGradeSort = legacySortPreferences.getString(
            LEGACY_KEY_GRADE_SORT,
            null
        )

        preferences.edit {
            if (!preferences.contains(KEY_THEME_MODE) && legacyThemeMode != null) {
                putString(KEY_THEME_MODE, legacyThemeMode)
            }
            if (!preferences.contains(KEY_SUBJECT_SORT) && legacySubjectSort != null) {
                putString(KEY_SUBJECT_SORT, legacySubjectSort)
            }
            if (!preferences.contains(KEY_GRADE_SORT) && legacyGradeSort != null) {
                putString(KEY_GRADE_SORT, legacyGradeSort)
            }
        }
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

    private companion object {
        const val FILE_NAME = "app_settings"

        const val KEY_THEME_MODE = "appearance.theme_mode"
        const val KEY_DYNAMIC_COLORS = "appearance.dynamic_colors"
        const val KEY_GRADE_COLOR_MODE = "appearance.grade_color_mode"
        const val KEY_SUBJECT_SORT = "sorting.subject_sort"
        const val KEY_GRADE_SORT = "sorting.grade_sort"

        const val LEGACY_SORT_FILE_NAME = "sort_settings"
        const val LEGACY_KEY_THEME_MODE = "theme_mode"
        const val LEGACY_KEY_SUBJECT_SORT = "subject_sort"
        const val LEGACY_KEY_GRADE_SORT = "grade_sort"
    }
}
