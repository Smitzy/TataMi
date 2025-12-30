package at.tatami.settings.presentation.system

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.common.domain.manager.ThemeManager
import at.tatami.domain.model.settings.DateFormat
import at.tatami.domain.model.settings.DateTimeFormatSettings
import at.tatami.domain.model.settings.ThemeMode
import at.tatami.domain.model.settings.TimeFormat
import at.tatami.settings.domain.usecase.GetDateTimeFormatSettingsUseCase
import at.tatami.settings.domain.usecase.SaveDateTimeFormatSettingsUseCase
import at.tatami.settings.domain.usecase.SaveThemeSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for System Settings screen.
 *
 * Manages theme mode and date/time format preferences.
 * Extracted from the main SettingsViewModel for better separation of concerns.
 */
class SystemSettingsViewModel(
    private val saveThemeSettings: SaveThemeSettingsUseCase,
    private val saveDateTimeFormatSettings: SaveDateTimeFormatSettingsUseCase,
    private val getDateTimeFormatSettings: GetDateTimeFormatSettingsUseCase,
    themeManager: ThemeManager
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themeManager.themeSettings
        .map { it.mode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeMode.SYSTEM
        )

    private val _timeFormat = MutableStateFlow(TimeFormat.TWELVE_HOUR)
    val timeFormat: StateFlow<TimeFormat> = _timeFormat.asStateFlow()

    private val _dateFormat = MutableStateFlow(DateFormat.MM_DD_YYYY)
    val dateFormat: StateFlow<DateFormat> = _dateFormat.asStateFlow()

    init {
        loadDateTimeFormats()
    }

    private fun loadDateTimeFormats() {
        viewModelScope.launch {
            try {
                val settings = getDateTimeFormatSettings()
                _timeFormat.value = settings.timeFormat
                _dateFormat.value = settings.dateFormat
            } catch (e: Exception) {
                // Use defaults on error
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            saveThemeSettings.setThemeMode(mode)
        }
    }

    fun setTimeFormat(format: TimeFormat) {
        viewModelScope.launch {
            _timeFormat.value = format
            saveDateTimeFormatSettings(
                DateTimeFormatSettings(
                    timeFormat = format,
                    dateFormat = _dateFormat.value
                )
            )
        }
    }

    fun setDateFormat(format: DateFormat) {
        viewModelScope.launch {
            _dateFormat.value = format
            saveDateTimeFormatSettings(
                DateTimeFormatSettings(
                    timeFormat = _timeFormat.value,
                    dateFormat = format
                )
            )
        }
    }
}
