package com.example.nearbyeventradar.data.model

import kotlinx.serialization.Serializable

enum class RoleCategory(val displayName: String, val colorHex: Long) {
    DEVELOPER("Galactic Dev", 0xFF00F0FF),     // Stitch Plasma Cyan
    DESIGNER("Hula / UX", 0xFFFF2A85),         // Hibiscus Pink
    FOUNDER("Ohana Founder", 0xFFB388FF),      // Stitch Lavender Lilac
    INVESTOR("Cosmic VC", 0xFF10B981),         // Tropical Palm Green
    SPEAKER("Keynote Speaker", 0xFFFBBF24),    // Hawaiian Sun Gold
    ORGANIZER("Event Organizer", 0xFFFF5252),  // Coral Red
    SPONSOR("Galactic Sponsor", 0xFF2979FF),   // Stitch Royal Blue
    VENUE_BEACON("Aloha Beacon", 0xFF38BDF8)   // Sky Blue
}

enum class VisibilityStatus(val label: String, val description: String) {
    BROADCASTING("🌺 Aloha Mode (Open)", "Broadcasting Experiment 626 BLE badge to nearby friends"),
    BUSY("🛸 In Deep Focus", "Visible on radar but currently hacking or in meeting"),
    IN_TALK("🥥 Attending Keynote", "Listening to talk with Do-Not-Disturb status"),
    STEALTH("🤫 Stealth 626 Mode", "Scanner active, but your badge is hidden in space")
}

enum class ProximityZone(val label: String, val minMeters: Float, val maxMeters: Float) {
    IMMEDIATE("Immediate (<1.5m)", 0f, 1.5f),
    NEAR("Near (1.5 - 4m)", 1.5f, 4.0f),
    FAR("Far (4 - 10m)", 4.0f, 10.0f),
    ZONE("Galactic Zone (10m+)", 10.0f, 50.0f)
}

@Serializable
data class Attendee(
    val id: String,
    val name: String,
    val title: String,
    val company: String,
    val bio: String,
    val role: RoleCategory,
    val interests: List<String>,
    val lookingFor: String,
    val offering: String,
    val rssi: Int = -65,
    val distanceMeters: Float = 3.2f,
    val angleDegrees: Float = 45f,
    val lastSeenEpochMs: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false,
    val isConnected: Boolean = false,
    val waveSent: Boolean = false,
    val waveReceived: Boolean = false,
    val linkedinUrl: String = "",
    val githubUrl: String = "",
    val twitter: String = "",
    val email: String = "",
    val isBeacon: Boolean = false,
    val beaconZone: String? = null,
    val matchScore: Int = 85
) {
    /** RSSI provides a broad proximity band only; it must never be presented as an exact distance. */
    fun proximityText(): String = when {
        rssi >= -58 -> "Muy cerca"
        rssi >= -72 -> "Cerca"
        else -> "A cierta distancia"
    }

    fun getProximityZone(): ProximityZone {
        return when {
            distanceMeters <= 1.5f -> ProximityZone.IMMEDIATE
            distanceMeters <= 4.0f -> ProximityZone.NEAR
            distanceMeters <= 10.0f -> ProximityZone.FAR
            else -> ProximityZone.ZONE
        }
    }
}

data class EventSession(
    val id: String,
    val title: String,
    val speakerName: String,
    val speakerRole: String,
    val locationRoom: String,
    val startTime: String,
    val endTime: String,
    val category: String,
    val description: String,
    val isBookmarked: Boolean = false,
    val nearbyAttendeesCount: Int = 14
)

data class ConnectionExchange(
    val id: String,
    val attendeeId: String,
    val attendeeName: String,
    val company: String,
    val title: String,
    val exchangeTimestamp: Long,
    val locationNote: String,
    val contactEmail: String,
    val socialHandle: String,
    val notes: String = "",
    val role: RoleCategory = RoleCategory.DEVELOPER
)

data class ChatMessage(
    val id: String,
    val attendeeId: String,
    val senderIsMe: Boolean,
    val text: String,
    val timestamp: Long
)

data class UserProfile(
    val name: String = "Stitch (Exp. 626)",
    val title: String = "Lead Galactic Android Engineer",
    val company: String = "Ohana Space Labs",
    val bio: String = "Master of chaos engineering, high-performance Jetpack Compose, BLE mesh, and Kotlin Multiplatform. 'Ohana means family, and family means nobody gets left behind!'",
    val role: RoleCategory = RoleCategory.DEVELOPER,
    val interests: List<String> = listOf("Kotlin", "Jetpack Compose", "BLE Mesh", "Edge AI", "Hula UI", "Space Tech"),
    val lookingFor: String = "Creative Android architects, Edge AI explorers & Ohana builders",
    val offering: String = "Plasma-fast UI tips, BLE proximity code reviews & boundless energy",
    val email: String = "stitch.626@ohanaspace.io",
    val linkedin: String = "linkedin.com/in/stitch-626",
    val github: String = "github.com/stitch-experiment626",
    val twitter: String = "@stitch_626_ai",
    val visibilityStatus: VisibilityStatus = VisibilityStatus.BROADCASTING,
    val beaconId: String = "EXP-626-OHANA"
)

data class RadarFilter(
    val selectedRole: RoleCategory? = null,
    val maxDistance: Float = 30f,
    val selectedTag: String? = null,
    val onlyUnconnected: Boolean = false,
    val onlySaved: Boolean = false,
    val searchQuery: String = ""
)
