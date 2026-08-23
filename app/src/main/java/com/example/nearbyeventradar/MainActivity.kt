package com.example.nearbyeventradar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.nearbyeventradar.ui.AppTab
import com.example.nearbyeventradar.ui.RadarViewModel
import com.example.nearbyeventradar.ui.components.AttendeeDetailSheet
import com.example.nearbyeventradar.ui.components.ChatBottomSheet
import com.example.nearbyeventradar.ui.components.DigitalBadgeQrDialog
import com.example.nearbyeventradar.ui.components.IncomingWaveDialog
import com.example.nearbyeventradar.ui.i18n.LocalAppLanguage
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.i18n.LocalizationProvider
import com.example.nearbyeventradar.ui.screens.AttendeesListScreen
import com.example.nearbyeventradar.ui.screens.ConnectionsScreen
import com.example.nearbyeventradar.ui.screens.MyBadgeScreen
import com.example.nearbyeventradar.ui.screens.RadarScreen
import com.example.nearbyeventradar.ui.screens.SessionsScreen
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.NearbyEventRadarTheme
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: RadarViewModel by viewModels {
        val app = application as RadarApplication
        RadarViewModel.provideFactory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NearbyEventRadarTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: RadarViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val strings = LocalizationProvider.getStrings(currentLanguage)

    val isScanning by viewModel.isScanning.collectAsState()
    val isBroadcasting by viewModel.isBroadcasting.collectAsState()
    val radarFilter by viewModel.radarFilter.collectAsState()
    val liveAttendees by viewModel.allLiveAttendees.collectAsState()
    val filteredAttendees by viewModel.filteredAttendees.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val connections by viewModel.connections.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val incomingWave by viewModel.incomingWave.collectAsState()
    val chatMessages by viewModel.activeChatMessages.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Permissions are requested only when the app is opened. Radar stays off until
    // the user explicitly activates it from the UI.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatus()
        }
    }

    CompositionLocalProvider(
        LocalAppLanguage provides currentLanguage,
        LocalAppStrings provides strings
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CreamBg,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .border(
                            1.dp,
                            SoftBorder,
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .testTag("bottom_navigation_bar")
                ) {
                    AppTab.values().forEach { tab ->
                        val isSelected = uiState.currentTab == tab
                        val (icon, label) = when (tab) {
                            AppTab.RADAR -> Icons.Default.Radar to strings.tabRadar
                            AppTab.ATTENDEES -> Icons.Default.People to strings.tabAttendees
                            AppTab.SESSIONS -> Icons.Default.EventNote to strings.tabSessions
                            AppTab.CONNECTIONS -> Icons.Default.Chat to strings.tabConnections
                            AppTab.MY_BADGE -> Icons.Default.Badge to strings.tabBadge
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OnSunnyYellow,
                                selectedTextColor = WarmGrayText,
                                indicatorColor = SunnyYellow,
                                unselectedIconColor = WarmGrayMuted,
                                unselectedTextColor = WarmGrayMuted
                            ),
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.currentTab) {
                    AppTab.RADAR -> {
                        RadarScreen(
                            attendees = filteredAttendees,
                            selectedAttendee = uiState.selectedAttendee,
                            isScanning = isScanning,
                            isBroadcasting = isBroadcasting,
                            radarFilter = radarFilter,
                            zoomMeters = uiState.radarZoomMeters,
                            isSonarView = uiState.isSonarView,
                            currentLanguage = currentLanguage,
                            onToggleLanguage = viewModel::toggleLanguage,
                            onToggleScanning = viewModel::toggleScanning,
                            onToggleBroadcasting = viewModel::toggleBroadcasting,
                            onSelectAttendee = viewModel::selectAttendee,
                            onDismissQuickCard = viewModel::closeDetailSheet,
                            onViewProfile = viewModel::selectAttendee,
                            onWave = viewModel::sendWave,
                            onToggleSave = viewModel::toggleSaveAttendee,
                            onOpenChat = viewModel::openChat,
                            onSetZoom = viewModel::setRadarZoom,
                            onSetRoleFilter = viewModel::setRoleFilter,
                            onSetSonarView = viewModel::setSonarView,
                            onShowQrPass = { viewModel.showQrPass(true) }
                        )
                    }

                    AppTab.ATTENDEES -> {
                        AttendeesListScreen(
                            attendees = filteredAttendees,
                            filter = radarFilter,
                            currentLanguage = currentLanguage,
                            onToggleLanguage = viewModel::toggleLanguage,
                            onSearchQueryChange = viewModel::setSearchQuery,
                            onTagSelect = viewModel::setTagFilter,
                            onSelectAttendee = viewModel::selectAttendee,
                            onWave = viewModel::sendWave,
                            onToggleSave = viewModel::toggleSaveAttendee,
                            onOpenChat = viewModel::openChat
                        )
                    }

                    AppTab.SESSIONS -> {
                        SessionsScreen(
                            sessions = sessions,
                            currentLanguage = currentLanguage,
                            onToggleLanguage = viewModel::toggleLanguage,
                            onToggleBookmark = viewModel::toggleBookmarkSession
                        )
                    }

                    AppTab.CONNECTIONS -> {
                        ConnectionsScreen(
                            connections = connections,
                            liveAttendees = liveAttendees,
                            currentLanguage = currentLanguage,
                            onToggleLanguage = viewModel::toggleLanguage,
                            onOpenChatWithConnection = viewModel::openChat,
                            onSelectAttendee = viewModel::selectAttendee
                        )
                    }

                    AppTab.MY_BADGE -> {
                        MyBadgeScreen(
                            profile = userProfile,
                            isBroadcasting = isBroadcasting,
                            currentLanguage = currentLanguage,
                            onToggleLanguage = viewModel::toggleLanguage,
                            onToggleBroadcasting = viewModel::toggleBroadcasting,
                            onUpdateVisibility = viewModel::updateVisibilityStatus,
                            onSaveProfile = viewModel::updateUserProfile,
                            onShowQrPass = { viewModel.showQrPass(true) }
                        )
                    }
                }
            }
        }

        // Modal Bottom Sheet: Full Attendee Profile & Icebreakers
        if (uiState.showDetailSheet && uiState.selectedAttendee != null) {
            AttendeeDetailSheet(
                attendee = uiState.selectedAttendee!!,
                onDismiss = viewModel::closeDetailSheet,
                onWave = viewModel::sendWave,
                onExchangeBadge = viewModel::exchangeContact,
                onToggleSave = viewModel::toggleSaveAttendee,
                onOpenChat = { attendee ->
                    viewModel.closeDetailSheet()
                    viewModel.openChat(attendee)
                }
            )
        }

        // In-Event Peer Chat Sheet
        if (uiState.activeChatAttendee != null) {
            ChatBottomSheet(
                attendee = uiState.activeChatAttendee!!,
                messages = chatMessages,
                onSendMessage = viewModel::sendChatMessage,
                onDismiss = viewModel::closeChat
            )
        }

        // Incoming Proximity Wave Dialog
        incomingWave?.let { attendee ->
            IncomingWaveDialog(
                attendee = attendee,
                onWaveBack = viewModel::respondToWave,
                onDismiss = viewModel::dismissIncomingWave
            )
        }

        // Digital Badge QR Pass Dialog
        if (uiState.showQrPassDialog) {
            DigitalBadgeQrDialog(
                userProfile = userProfile,
                onDismiss = { viewModel.showQrPass(false) }
            )
        }
    }
}
