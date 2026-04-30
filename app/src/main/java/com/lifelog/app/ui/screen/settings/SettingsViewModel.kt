package com.lifelog.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifelog.app.data.repository.SettingsRepository
import com.lifelog.app.domain.usecase.data.BackupDataUseCase
import com.lifelog.app.domain.usecase.data.ExportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val exportDataUseCase: ExportDataUseCase,
    private val backupDataUseCase: BackupDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.darkTheme.collect { dark ->
                _uiState.update { it.copy(darkTheme = dark) }
            }
        }
        viewModelScope.launch {
            settingsRepository.notificationEnabled.collect { enabled ->
                _uiState.update { it.copy(notificationEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            settingsRepository.notificationHour.collect { hour ->
                _uiState.update { it.copy(notificationHour = hour) }
            }
        }
        viewModelScope.launch {
            settingsRepository.notificationMinute.collect { minute ->
                _uiState.update { it.copy(notificationMinute = minute) }
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationEnabled(enabled)
        }
    }

    fun exportData() {
        viewModelScope.launch {
            val result = exportDataUseCase.exportToJson()
            result.onSuccess { path ->
                _toastMessage.emit("导出成功: $path")
                _uiState.update { it.copy(exportFilePath = path) }
            }.onFailure { e ->
                _toastMessage.emit("导出失败: ${e.message}")
            }
        }
    }

    fun backupData() {
        viewModelScope.launch {
            val result = backupDataUseCase.backup()
            result.onSuccess {
                _toastMessage.emit("备份成功")
            }.onFailure { e ->
                _toastMessage.emit("备份失败: ${e.message}")
            }
        }
    }

    fun restoreData() {
        viewModelScope.launch {
            val backups = backupDataUseCase.getBackupFiles()
            if (backups.isNotEmpty()) {
                val result = backupDataUseCase.restore(backups.first().absolutePath)
                result.onSuccess {
                    _toastMessage.emit("恢复成功，请重启APP")
                }.onFailure { e ->
                    _toastMessage.emit("恢复失败: ${e.message}")
                }
            } else {
                _toastMessage.emit("没有找到备份文件")
            }
        }
    }
}

data class SettingsUiState(
    val darkTheme: Boolean = false,
    val notificationEnabled: Boolean = true,
    val notificationHour: Int = 21,
    val notificationMinute: Int = 0,
    val exportFilePath: String? = null
)
