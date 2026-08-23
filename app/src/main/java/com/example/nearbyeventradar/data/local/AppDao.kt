package com.example.nearbyeventradar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Saved Attendees / Contacts
    @Query("SELECT * FROM saved_attendees ORDER BY savedTimestamp DESC")
    fun getAllSavedAttendees(): Flow<List<SavedAttendeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedAttendee(attendee: SavedAttendeeEntity)

    @Query("DELETE FROM saved_attendees WHERE id = :id")
    suspend fun deleteSavedAttendee(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_attendees WHERE id = :id)")
    fun isAttendeeSavedFlow(id: String): Flow<Boolean>

    // Connections
    @Query("SELECT * FROM connections ORDER BY exchangeTimestamp DESC")
    fun getAllConnections(): Flow<List<ConnectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: ConnectionEntity)

    @Query("DELETE FROM connections WHERE id = :id")
    suspend fun deleteConnection(id: String)

    // Saved Sessions
    @Query("SELECT id FROM saved_sessions")
    fun getBookmarkedSessionIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bookmarkSession(session: SavedSessionEntity)

    @Query("DELETE FROM saved_sessions WHERE id = :id")
    suspend fun removeBookmarkedSession(id: String)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE attendeeId = :attendeeId ORDER BY timestamp ASC")
    fun getMessagesForAttendee(attendeeId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)
}
