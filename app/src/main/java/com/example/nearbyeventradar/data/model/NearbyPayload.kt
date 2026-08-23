package com.example.nearbyeventradar.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
enum class NearbyPayloadType {
    WAVE,
    BADGE_EXCHANGE,
    CHAT_MESSAGE,
    PROFILE_SYNC,
    PING
}

@Serializable
data class NearbyPayload(
    val type: NearbyPayloadType,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val senderCompany: String = "",
    val senderTitle: String = "",
    val senderEmail: String = "",
    val senderLinkedin: String = "",
    val senderTwitter: String = "",
    val senderGithub: String = "",
    val beaconId: String = "",
    val textMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toByteArray(): ByteArray {
        val jsonString = Json.encodeToString(this)
        return jsonString.toByteArray(Charsets.UTF_8)
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromByteArray(bytes: ByteArray): NearbyPayload? {
            return runCatching {
                val jsonString = String(bytes, Charsets.UTF_8)
                json.decodeFromString<NearbyPayload>(jsonString)
            }.getOrNull()
        }
    }
}
