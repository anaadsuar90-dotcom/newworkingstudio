package com.example.nearbyeventradar.data.ble

import android.content.Context
import android.util.Log
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.ChatMessage
import com.example.nearbyeventradar.data.model.ConnectionExchange
import com.example.nearbyeventradar.data.model.NearbyPayload
import com.example.nearbyeventradar.data.model.NearbyPayloadType
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.UserProfile
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Manages peer-to-peer Bluetooth Low Energy (BLE) discovery and communication
 * using Google Play Services Nearby Connections API (Strategy.P2P_CLUSTER).
 */
class NearbyConnectionsManager(private val context: Context) {

    companion object {
        private const val TAG = "NearbyConnManager"
        const val SERVICE_ID = "com.example.nearbyeventradar.NEARBY_SERVICE"
        val STRATEGY: Strategy = Strategy.P2P_CLUSTER
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private val connectionsClient: ConnectionsClient by lazy {
        Nearby.getConnectionsClient(context.applicationContext)
    }

    private val _isAdvertising = MutableStateFlow(false)
    val isAdvertising: StateFlow<Boolean> = _isAdvertising.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    // Discovered peer endpoints mapped to Attendee records
    private val _nearbyPeers = MutableStateFlow<Map<String, Attendee>>(emptyMap())
    val nearbyPeers: StateFlow<Map<String, Attendee>> = _nearbyPeers.asStateFlow()

    // Set of currently connected endpoint IDs
    private val connectedEndpoints = mutableSetOf<String>()

    // Callbacks for repository & UI listeners
    var onIncomingWaveListener: ((Attendee) -> Unit)? = null
    var onChatMessageListener: ((senderId: String, text: String, timestamp: Long) -> Unit)? = null
    var onBadgeExchangeListener: ((ConnectionExchange) -> Unit)? = null

    private var currentUserProfile: UserProfile = UserProfile()

    // Endpoint Discovery Callback: Detects nearby devices over BLE
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Nearby BLE Endpoint found: $endpointId, name: ${info.endpointName}")
            val attendee = parseEndpointNameToAttendee(endpointId, info.endpointName)
            _nearbyPeers.update { current ->
                current + (endpointId to attendee)
            }

            // Automatically request connection to enable peer payload exchange (Waves, Chat, Badges)
            tryInitiateConnection(endpointId)
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "Nearby BLE Endpoint lost: $endpointId")
            _nearbyPeers.update { current ->
                current - endpointId
            }
            connectedEndpoints.remove(endpointId)
        }
    }

    // Connection Lifecycle Callback: Handles handshakes and authentication
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d(TAG, "Nearby Connection initiated from $endpointId (${connectionInfo.endpointName})")
            // Auto-accept connection for spontaneous in-event proximity interactions
            connectionsClient.acceptConnection(endpointId, payloadCallback)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully accepted connection for $endpointId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to accept connection for $endpointId", e)
                }
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                Log.d(TAG, "Nearby Connection established successfully with $endpointId")
                connectedEndpoints.add(endpointId)

                // Sync our profile with the connected peer
                sendProfileSync(endpointId)
            } else {
                Log.w(TAG, "Nearby Connection rejected/failed with $endpointId: ${result.status.statusCode}")
                connectedEndpoints.remove(endpointId)
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Nearby Connection disconnected from $endpointId")
            connectedEndpoints.remove(endpointId)
        }
    }

    // Payload Callback: Handles Waves, Chat Messages, and Digital Badges over BLE
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val bytes = payload.asBytes() ?: return
                val nearbyPayload = NearbyPayload.fromByteArray(bytes) ?: return
                handleReceivedPayload(endpointId, nearbyPayload)
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Optional progress tracking for large transfers
        }
    }

    /**
     * Starts BLE advertising so other radar attendees can discover this device.
     */
    fun startAdvertising(profile: UserProfile = currentUserProfile) {
        currentUserProfile = profile
        val endpointName = encodeProfileToEndpointName(profile)

        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(STRATEGY)
            .setDisruptiveUpgrade(false)
            .build()

        connectionsClient.startAdvertising(
            endpointName,
            SERVICE_ID,
            connectionLifecycleCallback,
            advertisingOptions
        ).addOnSuccessListener {
            _isAdvertising.value = true
            Log.i(TAG, "Nearby Connections BLE Advertising started with name: $endpointName")
        }.addOnFailureListener { e ->
            _isAdvertising.value = false
            Log.w(TAG, "Failed to start Nearby Connections BLE Advertising", e)
        }
    }

    /**
     * Stops BLE advertising.
     */
    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
        _isAdvertising.value = false
        Log.i(TAG, "Nearby Connections BLE Advertising stopped")
    }

    /**
     * Starts BLE discovery (radar scan) to find nearby event attendees.
     */
    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(STRATEGY)
            .build()

        connectionsClient.startDiscovery(
            SERVICE_ID,
            endpointDiscoveryCallback,
            discoveryOptions
        ).addOnSuccessListener {
            _isDiscovering.value = true
            Log.i(TAG, "Nearby Connections BLE Discovery started")
        }.addOnFailureListener { e ->
            _isDiscovering.value = false
            Log.w(TAG, "Failed to start Nearby Connections BLE Discovery", e)
        }
    }

    /**
     * Stops BLE discovery.
     */
    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        _isDiscovering.value = false
        Log.i(TAG, "Nearby Connections BLE Discovery stopped")
    }

    /**
     * Updates user profile and restarts advertising if currently active.
     */
    fun updateUserProfile(profile: UserProfile) {
        currentUserProfile = profile
        if (_isAdvertising.value) {
            stopAdvertising()
            startAdvertising(profile)
        }
    }

    /**
     * Sends a friendly wave (👋) to a discovered peer.
     */
    fun sendWave(endpointIdOrAttendeeId: String) {
        val targetEndpoint = resolveEndpointId(endpointIdOrAttendeeId)
        val payload = NearbyPayload(
            type = NearbyPayloadType.WAVE,
            senderId = currentUserProfile.beaconId,
            senderName = currentUserProfile.name,
            senderRole = currentUserProfile.role.name,
            senderCompany = currentUserProfile.company,
            senderTitle = currentUserProfile.title,
            senderEmail = currentUserProfile.email,
            senderLinkedin = currentUserProfile.linkedin,
            senderTwitter = currentUserProfile.twitter,
            senderGithub = currentUserProfile.github,
            beaconId = currentUserProfile.beaconId
        )

        sendPayloadToTarget(targetEndpoint, payload)
    }

    /**
     * Exchanges digital badge information with a peer.
     */
    fun sendBadgeExchange(endpointIdOrAttendeeId: String) {
        val targetEndpoint = resolveEndpointId(endpointIdOrAttendeeId)
        val payload = NearbyPayload(
            type = NearbyPayloadType.BADGE_EXCHANGE,
            senderId = currentUserProfile.beaconId,
            senderName = currentUserProfile.name,
            senderRole = currentUserProfile.role.name,
            senderCompany = currentUserProfile.company,
            senderTitle = currentUserProfile.title,
            senderEmail = currentUserProfile.email,
            senderLinkedin = currentUserProfile.linkedin,
            senderTwitter = currentUserProfile.twitter,
            senderGithub = currentUserProfile.github,
            beaconId = currentUserProfile.beaconId
        )

        sendPayloadToTarget(targetEndpoint, payload)
    }

    /**
     * Sends a real-time peer-to-peer chat message over BLE.
     */
    fun sendChatMessage(endpointIdOrAttendeeId: String, text: String) {
        val targetEndpoint = resolveEndpointId(endpointIdOrAttendeeId)
        val payload = NearbyPayload(
            type = NearbyPayloadType.CHAT_MESSAGE,
            senderId = currentUserProfile.beaconId,
            senderName = currentUserProfile.name,
            senderRole = currentUserProfile.role.name,
            senderCompany = currentUserProfile.company,
            senderTitle = currentUserProfile.title,
            textMessage = text,
            beaconId = currentUserProfile.beaconId
        )

        sendPayloadToTarget(targetEndpoint, payload)
    }

    private fun sendProfileSync(endpointId: String) {
        val payload = NearbyPayload(
            type = NearbyPayloadType.PROFILE_SYNC,
            senderId = currentUserProfile.beaconId,
            senderName = currentUserProfile.name,
            senderRole = currentUserProfile.role.name,
            senderCompany = currentUserProfile.company,
            senderTitle = currentUserProfile.title,
            senderEmail = currentUserProfile.email,
            senderLinkedin = currentUserProfile.linkedin,
            senderTwitter = currentUserProfile.twitter,
            senderGithub = currentUserProfile.github,
            beaconId = currentUserProfile.beaconId
        )
        sendPayloadToTarget(endpointId, payload)
    }

    private fun sendPayloadToTarget(targetEndpoint: String?, payload: NearbyPayload) {
        if (targetEndpoint == null) return
        val bytes = payload.toByteArray()
        val nearbyPayload = Payload.fromBytes(bytes)

        if (connectedEndpoints.contains(targetEndpoint)) {
            connectionsClient.sendPayload(targetEndpoint, nearbyPayload)
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error sending payload to $targetEndpoint", e)
                }
        } else {
            // Attempt to connect and send
            connectionsClient.requestConnection(
                encodeProfileToEndpointName(currentUserProfile),
                targetEndpoint,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "Connection request sent to $targetEndpoint for payload delivery")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Could not request connection to $targetEndpoint", e)
            }
        }
    }

    private fun tryInitiateConnection(endpointId: String) {
        if (!connectedEndpoints.contains(endpointId)) {
            connectionsClient.requestConnection(
                encodeProfileToEndpointName(currentUserProfile),
                endpointId,
                connectionLifecycleCallback
            ).addOnSuccessListener {
                Log.d(TAG, "Initiated Nearby connection to $endpointId")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Connection initiation failed for $endpointId (will retry on demand)", e)
            }
        }
    }

    private fun handleReceivedPayload(endpointId: String, payload: NearbyPayload) {
        coroutineScope.launch {
            when (payload.type) {
                NearbyPayloadType.WAVE -> {
                    Log.d(TAG, "Received WAVE from ${payload.senderName}")
                    val attendee = _nearbyPeers.value[endpointId] ?: Attendee(
                        id = endpointId,
                        name = payload.senderName,
                        title = payload.senderTitle.ifBlank { "Nearby Attendee" },
                        company = payload.senderCompany.ifBlank { "Event Participant" },
                        bio = "Discovered via Nearby Connections BLE",
                        role = runCatching { RoleCategory.valueOf(payload.senderRole) }.getOrDefault(RoleCategory.DEVELOPER),
                        interests = listOf("BLE", "Tech", "Networking"),
                        lookingFor = "Connections",
                        offering = "Collaboration",
                        distanceMeters = 1.2f,
                        angleDegrees = Random.nextFloat() * 360f,
                        waveReceived = true
                    )
                    onIncomingWaveListener?.invoke(attendee)
                }

                NearbyPayloadType.CHAT_MESSAGE -> {
                    Log.d(TAG, "Received CHAT message from ${payload.senderName}: ${payload.textMessage}")
                    onChatMessageListener?.invoke(
                        endpointId,
                        payload.textMessage,
                        payload.timestamp
                    )
                }

                NearbyPayloadType.BADGE_EXCHANGE -> {
                    Log.d(TAG, "Received BADGE EXCHANGE from ${payload.senderName}")
                    val connection = ConnectionExchange(
                        id = "conn_${endpointId}",
                        attendeeId = endpointId,
                        attendeeName = payload.senderName,
                        company = payload.senderCompany,
                        title = payload.senderTitle,
                        exchangeTimestamp = payload.timestamp,
                        locationNote = "Nearby Connections BLE Direct",
                        contactEmail = payload.senderEmail,
                        socialHandle = payload.senderLinkedin.ifBlank { payload.senderTwitter }.ifBlank { payload.senderGithub },
                        notes = "Exchanged over Nearby Connections",
                        role = runCatching { RoleCategory.valueOf(payload.senderRole) }.getOrDefault(RoleCategory.DEVELOPER)
                    )
                    onBadgeExchangeListener?.invoke(connection)
                }

                NearbyPayloadType.PROFILE_SYNC -> {
                    Log.d(TAG, "Received PROFILE SYNC from ${payload.senderName}")
                    _nearbyPeers.update { current ->
                        val existing = current[endpointId]
                        val updated = (existing ?: Attendee(
                            id = endpointId,
                            name = payload.senderName,
                            title = payload.senderTitle,
                            company = payload.senderCompany,
                            bio = "Discovered via Nearby Connections BLE",
                            role = runCatching { RoleCategory.valueOf(payload.senderRole) }.getOrDefault(RoleCategory.DEVELOPER),
                            interests = listOf("BLE", "Tech"),
                            lookingFor = "Networking",
                            offering = "Open to connect",
                            distanceMeters = 1.5f,
                            angleDegrees = Random.nextFloat() * 360f
                        )).copy(
                            name = payload.senderName,
                            title = payload.senderTitle,
                            company = payload.senderCompany,
                            email = payload.senderEmail,
                            linkedinUrl = payload.senderLinkedin,
                            twitter = payload.senderTwitter,
                            githubUrl = payload.senderGithub
                        )
                        current + (endpointId to updated)
                    }
                }

                NearbyPayloadType.PING -> {
                    // Handshake ping
                }
            }
        }
    }

    private fun resolveEndpointId(endpointIdOrAttendeeId: String): String? {
        if (_nearbyPeers.value.containsKey(endpointIdOrAttendeeId)) {
            return endpointIdOrAttendeeId
        }
        return _nearbyPeers.value.entries.find { it.value.id == endpointIdOrAttendeeId }?.key
    }

    /**
     * Compactly encodes the user's profile for the Nearby Connections endpoint name.
     * Google Nearby allows up to 131 bytes for endpoint name.
     */
    private fun encodeProfileToEndpointName(profile: UserProfile): String {
        val safeName = profile.name.take(24)
        val safeRole = profile.role.name
        val safeCompany = profile.company.take(20)
        val safeTitle = profile.title.take(20)
        val safeBeacon = profile.beaconId.take(12)
        return "$safeName|$safeRole|$safeCompany|$safeTitle|$safeBeacon"
    }

    /**
     * Parses the endpoint name back into an Attendee entity.
     */
    private fun parseEndpointNameToAttendee(endpointId: String, endpointName: String): Attendee {
        val parts = endpointName.split("|")
        val name = parts.getOrNull(0)?.ifBlank { "Nearby Guest" } ?: "Nearby Guest"
        val roleStr = parts.getOrNull(1) ?: RoleCategory.DEVELOPER.name
        val company = parts.getOrNull(2)?.ifBlank { "Event Attendee" } ?: "Event Attendee"
        val title = parts.getOrNull(3)?.ifBlank { "Participant" } ?: "Participant"
        val beaconId = parts.getOrNull(4) ?: "NEARBY-$endpointId"

        val role = runCatching { RoleCategory.valueOf(roleStr) }.getOrDefault(RoleCategory.DEVELOPER)
        val randomDist = Random.nextFloat() * 3.5f + 0.8f // 0.8m to 4.3m
        val randomAngle = Random.nextFloat() * 360f

        return Attendee(
            id = endpointId,
            name = name,
            title = title,
            company = company,
            bio = "Discovered live over Bluetooth Low Energy via Nearby Connections.",
            role = role,
            interests = listOf("Nearby BLE", "Tech", "Event Radar"),
            lookingFor = "Event networking & collaborating",
            offering = "Open to connect",
            rssi = -55,
            distanceMeters = randomDist,
            angleDegrees = randomAngle,
            lastSeenEpochMs = System.currentTimeMillis(),
            matchScore = 88
        )
    }

    /**
     * Disconnects and resets all Nearby Connections state.
     */
    fun stopAll() {
        try {
            stopAdvertising()
            stopDiscovery()
            connectionsClient.stopAllEndpoints()
            connectedEndpoints.clear()
            _nearbyPeers.value = emptyMap()
        } catch (e: Exception) {
            Log.w(TAG, "Error in stopAll", e)
        }
    }
}
