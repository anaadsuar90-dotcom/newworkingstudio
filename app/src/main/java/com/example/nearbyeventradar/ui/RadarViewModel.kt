package com.example.nearbyeventradar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.ChatMessage
import com.example.nearbyeventradar.data.model.ConnectionExchange
import com.example.nearbyeventradar.data.model.EventSession
import com.example.nearbyeventradar.data.model.RadarFilter
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.UserProfile
import com.example.nearbyeventradar.data.model.VisibilityStatus
import com.example.nearbyeventradar.data.repository.RadarRepository
import com.example.nearbyeventradar.ui.i18n.AppLanguage
import com.example.nearbyeventradar.ui.i18n.LocalizationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    RADAR("Radar"),
    ATTENDEES("Attendees"),
    SESSIONS("Sessions"),
    CONNECTIONS("Connections"),
    MY_BADGE("My Badge")
}

data class RadarUiState(
    val currentTab: AppTab = AppTab.RADAR,
    val selectedAttendee: Attendee? = null,
    val showDetailSheet: Boolean = false,
    val activeChatAttendee: Attendee? = null,
    val showQrPassDialog: Boolean = false,
    val showFilterDialog: Boolean = false,
    val radarZoomMeters: Float = 15f,
    val isSonarView: Boolean = true,
    val statusMessage: String? = null,
    val currentLanguage: AppLanguage = AppLanguage.SPANISH
)

class RadarViewModel(
    private val repository: RadarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.SPANISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val isScanning = repository.isScanning
    val isBroadcasting = repository.isBroadcasting
    val incomingWave = repository.incomingWave
    val radarFilter = repository.radarFilter

    val allLiveAttendees = repository.liveAttendees.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val filteredAttendees = repository.filteredAttendees.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val sessions = repository.eventSessions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val connections = repository.connections.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val userProfile = repository.userProfile.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserProfile()
    )

    private val _selectedChatAttendeeId = MutableStateFlow<String?>(null)
    val activeChatMessages: StateFlow<List<ChatMessage>> = _selectedChatAttendeeId.flatMapLatest { id ->
        if (id == null) {
            MutableStateFlow(emptyList())
        } else {
            repository.getChatMessages(id)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectAttendee(attendee: Attendee?) {
        _uiState.value = _uiState.value.copy(
            selectedAttendee = attendee,
            showDetailSheet = attendee != null
        )
    }

    fun closeDetailSheet() {
        _uiState.value = _uiState.value.copy(
            showDetailSheet = false,
            selectedAttendee = null
        )
    }

    fun setSonarView(isSonar: Boolean) {
        _uiState.value = _uiState.value.copy(isSonarView = isSonar)
    }

    fun setRadarZoom(maxMeters: Float) {
        _uiState.value = _uiState.value.copy(radarZoomMeters = maxMeters)
        repository.updateFilter { it.copy(maxDistance = maxMeters) }
    }

    fun setRoleFilter(role: RoleCategory?) {
        repository.updateFilter { it.copy(selectedRole = role) }
    }

    fun setTagFilter(tag: String?) {
        repository.updateFilter {
            if (it.selectedTag == tag) it.copy(selectedTag = null) else it.copy(selectedTag = tag)
        }
    }

    fun setSearchQuery(query: String) {
        repository.updateFilter { it.copy(searchQuery = query) }
    }

    fun toggleOnlySaved(onlySaved: Boolean) {
        repository.updateFilter { it.copy(onlySaved = onlySaved) }
    }

    fun toggleLanguage() {
        val nextLang = if (_currentLanguage.value == AppLanguage.SPANISH) AppLanguage.ENGLISH else AppLanguage.SPANISH
        setLanguage(nextLang)
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        _uiState.value = _uiState.value.copy(currentLanguage = language)
        val strings = LocalizationProvider.getStrings(language)
        showStatus(strings.snackbarLanguageChanged)
    }

    fun startBleScanner() {
        repository.startScanning()
    }

    fun toggleScanning() {
        repository.toggleScanning()
        val strings = LocalizationProvider.getStrings(_currentLanguage.value)
        val isNowScanning = !isScanning.value
        showStatus(if (isNowScanning) strings.snackbarScannerResumed else strings.snackbarScannerPaused)
    }

    fun toggleBroadcasting() {
        repository.toggleBroadcasting()
    }

    fun sendWave(attendee: Attendee) {
        repository.sendWave(attendee.id)
        val strings = LocalizationProvider.getStrings(_currentLanguage.value)
        showStatus(strings.snackbarWaveSent(attendee.name))
    }

    fun dismissIncomingWave() {
        repository.dismissIncomingWave()
    }

    fun respondToWave(attendee: Attendee) {
        repository.sendWave(attendee.id)
        repository.dismissIncomingWave()
        selectAttendee(attendee)
        val strings = LocalizationProvider.getStrings(_currentLanguage.value)
        showStatus(strings.snackbarWaveReturned(attendee.name))
    }

    fun toggleSaveAttendee(attendee: Attendee) {
        val willBeSaved = !attendee.isSaved
        repository.toggleSaveAttendee(attendee)
        val strings = LocalizationProvider.getStrings(_currentLanguage.value)
        if (willBeSaved) {
            showStatus(strings.snackbarAttendeeSaved(attendee.name))
        } else {
            showStatus(strings.snackbarAttendeeRemoved(attendee.name))
        }
    }

    fun exchangeContact(attendee: Attendee, customNote: String = "") {
        repository.exchangeContact(attendee, customNote = customNote)
        val strings = LocalizationProvider.getStrings(_currentLanguage.value)
        showStatus(strings.snackbarContactExchanged(attendee.name))
    }

    fun toggleBookmarkSession(session: EventSession) {
        repository.toggleBookmarkSession(session.id, session.isBookmarked)
    }

    fun openChat(attendee: Attendee) {
        _selectedChatAttendeeId.value = attendee.id
        _uiState.value = _uiState.value.copy(activeChatAttendee = attendee)
    }

    fun closeChat() {
        _selectedChatAttendeeId.value = null
        _uiState.value = _uiState.value.copy(activeChatAttendee = null)
    }

    fun sendChatMessage(text: String) {
        val attendee = _uiState.value.activeChatAttendee ?: return
        if (text.isBlank()) return
        repository.sendChatMessage(attendee.id, text.trim())
    }

    fun showQrPass(show: Boolean) {
        _uiState.value = _uiState.value.copy(showQrPassDialog = show)
    }

    fun updateUserProfile(profile: UserProfile) {
        repository.updateUserProfile(profile)
        val strings = LocalizationProvider.getStrings(_currentLanguage.value)
        showStatus(strings.snackbarProfileUpdated)
    }

    fun updateVisibilityStatus(status: VisibilityStatus) {
        val current = userProfile.value
        updateUserProfile(current.copy(visibilityStatus = status))
    }

    private fun showStatus(message: String) {
        _uiState.value = _uiState.value.copy(statusMessage = message)
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    companion object {
        fun provideFactory(repository: RadarRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RadarViewModel(repository) as T
                }
            }
    }
}
