package com.example.nearbyeventradar.data.repository

import com.example.nearbyeventradar.data.ble.BleProximityScanner
import com.example.nearbyeventradar.data.local.AppDao
import com.example.nearbyeventradar.data.local.ChatMessageEntity
import com.example.nearbyeventradar.data.local.ConnectionEntity
import com.example.nearbyeventradar.data.local.SavedAttendeeEntity
import com.example.nearbyeventradar.data.local.SavedSessionEntity
import com.example.nearbyeventradar.data.local.UserProfileEntity
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.ChatMessage
import com.example.nearbyeventradar.data.model.ConnectionExchange
import com.example.nearbyeventradar.data.model.EventSession
import com.example.nearbyeventradar.data.model.RadarFilter
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.UserProfile
import com.example.nearbyeventradar.data.model.VisibilityStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RadarRepository(
    private val bleScanner: BleProximityScanner,
    private val appDao: AppDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    val isScanning = bleScanner.isScanning
    val isBroadcasting = bleScanner.isBroadcasting
    val incomingWave = bleScanner.incomingWave

    init {
        // Handle incoming peer-to-peer chat messages from Nearby Connections
        bleScanner.onIncomingChatMessage = { senderId, text, timestamp ->
            coroutineScope.launch {
                val msg = ChatMessageEntity(
                    id = "msg_recv_${System.currentTimeMillis()}",
                    attendeeId = senderId,
                    senderIsMe = false,
                    text = text,
                    timestamp = timestamp
                )
                appDao.insertChatMessage(msg)
            }
        }

        // Handle incoming badge exchanges from Nearby Connections
        bleScanner.onIncomingBadgeExchange = { connection ->
            coroutineScope.launch {
                appDao.insertConnection(
                    ConnectionEntity(
                        id = connection.id,
                        attendeeId = connection.attendeeId,
                        attendeeName = connection.attendeeName,
                        company = connection.company,
                        title = connection.title,
                        exchangeTimestamp = connection.exchangeTimestamp,
                        locationNote = connection.locationNote,
                        contactEmail = connection.contactEmail,
                        socialHandle = connection.socialHandle,
                        notes = connection.notes,
                        role = connection.role.name
                    )
                )
                bleScanner.connectWithAttendee(connection.attendeeId)
            }
        }
    }

    private val _radarFilter = MutableStateFlow(RadarFilter())
    val radarFilter: StateFlow<RadarFilter> = _radarFilter.asStateFlow()

    private val initialSessions = listOf(
        EventSession(
            id = "sess_1",
            title = "Keynote: Next-Gen Autonomous AI Agents on Edge Hardware",
            speakerName = "Dr. Kenji Sato",
            speakerRole = "Tokyo Institute of Robotics",
            locationRoom = "Auditorium A (Main Stage)",
            startTime = "10:00 AM",
            endTime = "11:15 AM",
            category = "Keynote",
            description = "Explore real-time decentralized sensor swarms, BLE mesh networks, and local neural network execution on low-power devices.",
            nearbyAttendeesCount = 42
        ),
        EventSession(
            id = "sess_2",
            title = "Building Ultra-Smooth Jetpack Compose & Fluid Layouts",
            speakerName = "Elena Rostova",
            speakerRole = "Head of Product Design, DesignSystematic",
            locationRoom = "Workshop Lab 3",
            startTime = "11:30 AM",
            endTime = "12:45 PM",
            category = "UI & Motion",
            description = "Master Compose custom canvas drawing, hardware acceleration, gesture driven transitions, and M3 design token pipelines.",
            nearbyAttendeesCount = 28
        ),
        EventSession(
            id = "sess_3",
            title = "On-Device SLMs: Fine-Tuning and Distillation for Android",
            speakerName = "Sarah Chen",
            speakerRole = "Staff AI Research Engineer, Anthropic",
            locationRoom = "Developer Stage B",
            startTime = "01:30 PM",
            endTime = "02:45 PM",
            category = "AI / ML",
            description = "Step-by-step architecture breakdown of executing 2B-4B parameter language models on mobile NPU/GPUs with 30+ tokens/sec.",
            nearbyAttendeesCount = 56
        ),
        EventSession(
            id = "sess_4",
            title = "Founder Pitch Arena: $100k Seed Showcase",
            speakerName = "Marcus Vance & Panel",
            speakerRole = "Nexus Horizon Ventures",
            locationRoom = "Innovation Lounge",
            startTime = "03:00 PM",
            endTime = "04:30 PM",
            category = "Startup & VC",
            description = "8 lightning pitches from early-stage founders followed by live investor Q&A and networking mixer.",
            nearbyAttendeesCount = 35
        ),
        EventSession(
            id = "sess_5",
            title = "Offline-First Sync & Resilient Room Architectures",
            speakerName = "David Kim",
            speakerRole = "Lead Android Architect, Spotify",
            locationRoom = "Developer Stage B",
            startTime = "04:45 PM",
            endTime = "05:45 PM",
            category = "Architecture",
            description = "Practical patterns for conflict resolution, SQLite WAL optimizations, and reactive state management under intermittent connectivity.",
            nearbyAttendeesCount = 31
        )
    )

    private val _sessions = MutableStateFlow(initialSessions)

    val savedAttendeeIds: Flow<Set<String>> = appDao.getAllSavedAttendees().map { list ->
        list.map { it.id }.toSet()
    }

    val bookmarkedSessionIds: Flow<Set<String>> = appDao.getBookmarkedSessionIds().map { it.toSet() }

    val eventSessions: Flow<List<EventSession>> = combine(
        _sessions,
        bookmarkedSessionIds
    ) { sessions, savedIds ->
        sessions.map { it.copy(isBookmarked = savedIds.contains(it.id)) }
    }

    val connections: Flow<List<ConnectionExchange>> = appDao.getAllConnections().map { list ->
        list.map { entity ->
            ConnectionExchange(
                id = entity.id,
                attendeeId = entity.attendeeId,
                attendeeName = entity.attendeeName,
                company = entity.company,
                title = entity.title,
                exchangeTimestamp = entity.exchangeTimestamp,
                locationNote = entity.locationNote,
                contactEmail = entity.contactEmail,
                socialHandle = entity.socialHandle,
                notes = entity.notes,
                role = runCatching { RoleCategory.valueOf(entity.role) }.getOrDefault(RoleCategory.DEVELOPER)
            )
        }
    }

    val liveAttendees: Flow<List<Attendee>> = combine(
        bleScanner.nearbyAttendees,
        savedAttendeeIds,
        connections
    ) { attendees, savedIds, conns ->
        val connectedMap = conns.associateBy { it.attendeeId }
        attendees.map { attendee ->
            attendee.copy(
                isSaved = savedIds.contains(attendee.id),
                isConnected = connectedMap.containsKey(attendee.id)
            )
        }
    }

    val filteredAttendees: Flow<List<Attendee>> = combine(
        liveAttendees,
        _radarFilter
    ) { attendees, filter ->
        attendees.filter { attendee ->
            val matchesRole = filter.selectedRole == null || attendee.role == filter.selectedRole
            val matchesDist = attendee.distanceMeters <= filter.maxDistance
            val matchesTag = filter.selectedTag == null || attendee.interests.any { it.equals(filter.selectedTag, ignoreCase = true) }
            val matchesSaved = !filter.onlySaved || attendee.isSaved
            val matchesUnconnected = !filter.onlyUnconnected || !attendee.isConnected
            val matchesQuery = filter.searchQuery.isBlank() ||
                    attendee.name.contains(filter.searchQuery, ignoreCase = true) ||
                    attendee.company.contains(filter.searchQuery, ignoreCase = true) ||
                    attendee.title.contains(filter.searchQuery, ignoreCase = true) ||
                    attendee.interests.any { it.contains(filter.searchQuery, ignoreCase = true) }

            matchesRole && matchesDist && matchesTag && matchesSaved && matchesUnconnected && matchesQuery
        }
    }

    val userProfile: Flow<UserProfile> = appDao.getUserProfile().map { entity ->
        if (entity == null) {
            UserProfile()
        } else {
            UserProfile(
                name = entity.name,
                title = entity.title,
                company = entity.company,
                bio = entity.bio,
                role = runCatching { RoleCategory.valueOf(entity.role) }.getOrDefault(RoleCategory.DEVELOPER),
                interests = entity.interestsCsv.split(",").filter { it.isNotBlank() },
                lookingFor = entity.lookingFor,
                offering = entity.offering,
                email = entity.email,
                linkedin = entity.linkedin,
                github = entity.github,
                twitter = entity.twitter,
                visibilityStatus = runCatching { VisibilityStatus.valueOf(entity.visibilityStatus) }.getOrDefault(VisibilityStatus.BROADCASTING),
                beaconId = entity.beaconId
            )
        }
    }

    fun updateFilter(update: (RadarFilter) -> RadarFilter) {
        _radarFilter.update(update)
    }

    fun startScanning() {
        bleScanner.startScanning()
    }

    fun toggleScanning() {
        bleScanner.toggleScanning()
    }

    fun toggleBroadcasting() {
        bleScanner.toggleBroadcasting()
    }

    fun dismissIncomingWave() {
        bleScanner.dismissIncomingWave()
    }

    fun sendWave(attendeeId: String) {
        bleScanner.sendWave(attendeeId)
    }

    fun toggleSaveAttendee(attendee: Attendee) {
        coroutineScope.launch {
            if (attendee.isSaved) {
                appDao.deleteSavedAttendee(attendee.id)
            } else {
                appDao.insertSavedAttendee(
                    SavedAttendeeEntity(
                        id = attendee.id,
                        name = attendee.name,
                        title = attendee.title,
                        company = attendee.company,
                        bio = attendee.bio,
                        role = attendee.role.name,
                        interestsCsv = attendee.interests.joinToString(","),
                        lookingFor = attendee.lookingFor,
                        offering = attendee.offering,
                        email = attendee.email,
                        linkedinUrl = attendee.linkedinUrl,
                        githubUrl = attendee.githubUrl,
                        twitter = attendee.twitter,
                        isSaved = true
                    )
                )
            }
            bleScanner.toggleSaveAttendee(attendee.id)
        }
    }

    fun exchangeContact(attendee: Attendee, locationNote: String = "Global Tech Summit 2026", customNote: String = "") {
        coroutineScope.launch {
            val connection = ConnectionEntity(
                id = "conn_${attendee.id}",
                attendeeId = attendee.id,
                attendeeName = attendee.name,
                company = attendee.company,
                title = attendee.title,
                exchangeTimestamp = System.currentTimeMillis(),
                locationNote = locationNote,
                contactEmail = attendee.email.ifBlank { "${attendee.name.lowercase().replace(" ", ".")}@event.io" },
                socialHandle = attendee.linkedinUrl.ifBlank { attendee.twitter }.ifBlank { attendee.githubUrl },
                notes = customNote,
                role = attendee.role.name
            )
            appDao.insertConnection(connection)
            bleScanner.connectWithAttendee(attendee.id)
            bleScanner.sendBadgeExchange(attendee.id)
        }
    }

    fun toggleBookmarkSession(sessionId: String, isCurrentlyBookmarked: Boolean) {
        coroutineScope.launch {
            if (isCurrentlyBookmarked) {
                appDao.removeBookmarkedSession(sessionId)
            } else {
                appDao.bookmarkSession(SavedSessionEntity(id = sessionId))
            }
        }
    }

    fun getChatMessages(attendeeId: String): Flow<List<ChatMessage>> {
        return appDao.getMessagesForAttendee(attendeeId).map { list ->
            list.map {
                ChatMessage(
                    id = it.id,
                    attendeeId = it.attendeeId,
                    senderIsMe = it.senderIsMe,
                    text = it.text,
                    timestamp = it.timestamp
                )
            }
        }
    }

    fun sendChatMessage(attendeeId: String, text: String) {
        coroutineScope.launch {
            val msg = ChatMessageEntity(
                id = "msg_${System.currentTimeMillis()}",
                attendeeId = attendeeId,
                senderIsMe = true,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            appDao.insertChatMessage(msg)
            bleScanner.sendChatMessage(attendeeId, text)
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        coroutineScope.launch {
            appDao.saveUserProfile(
                UserProfileEntity(
                    id = 1,
                    name = profile.name,
                    title = profile.title,
                    company = profile.company,
                    bio = profile.bio,
                    role = profile.role.name,
                    interestsCsv = profile.interests.joinToString(","),
                    lookingFor = profile.lookingFor,
                    offering = profile.offering,
                    email = profile.email,
                    linkedin = profile.linkedin,
                    github = profile.github,
                    twitter = profile.twitter,
                    visibilityStatus = profile.visibilityStatus.name,
                    beaconId = profile.beaconId
                )
            )
            bleScanner.updateUserProfile(profile)
        }
    }
}
