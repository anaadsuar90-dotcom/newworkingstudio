package com.example.nearbyeventradar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.VisibilityStatus

@Entity(tableName = "saved_attendees")
data class SavedAttendeeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val company: String,
    val bio: String,
    val role: String,
    val interestsCsv: String,
    val lookingFor: String,
    val offering: String,
    val email: String,
    val linkedinUrl: String,
    val githubUrl: String,
    val twitter: String,
    val isSaved: Boolean = true,
    val isConnected: Boolean = false,
    val waveSent: Boolean = false,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "connections")
data class ConnectionEntity(
    @PrimaryKey val id: String,
    val attendeeId: String,
    val attendeeName: String,
    val company: String,
    val title: String,
    val exchangeTimestamp: Long,
    val locationNote: String,
    val contactEmail: String,
    val socialHandle: String,
    val notes: String = "",
    val role: String = "DEVELOPER"
)

@Entity(tableName = "saved_sessions")
data class SavedSessionEntity(
    @PrimaryKey val id: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val attendeeId: String,
    val senderIsMe: Boolean,
    val text: String,
    val timestamp: Long
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val title: String,
    val company: String,
    val bio: String,
    val role: String,
    val interestsCsv: String,
    val lookingFor: String,
    val offering: String,
    val email: String,
    val linkedin: String,
    val github: String,
    val twitter: String,
    val visibilityStatus: String,
    val beaconId: String
)
