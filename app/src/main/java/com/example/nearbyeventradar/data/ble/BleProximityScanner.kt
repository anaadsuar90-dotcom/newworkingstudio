package com.example.nearbyeventradar.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.ConnectionExchange
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random

class BleProximityScanner(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private var simulationJob: Job? = null

    val nearbyConnectionsManager: NearbyConnectionsManager by lazy {
        NearbyConnectionsManager(context)
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter
    private var bleScanner: BluetoothLeScanner? = null

    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isBroadcasting = MutableStateFlow(true)
    val isBroadcasting: StateFlow<Boolean> = _isBroadcasting.asStateFlow()

    // Local & simulated attendees
    private val _localAttendees = MutableStateFlow<List<Attendee>>(emptyList())

    // Merged attendees (Nearby Connections peers + local BLE scanned peers + event attendees)
    private val _nearbyAttendees = MutableStateFlow<List<Attendee>>(emptyList())
    val nearbyAttendees: StateFlow<List<Attendee>> = _nearbyAttendees.asStateFlow()

    private val _lastDiscovered = MutableStateFlow<Attendee?>(null)
    val lastDiscovered: StateFlow<Attendee?> = _lastDiscovered.asStateFlow()

    private val _incomingWave = MutableStateFlow<Attendee?>(null)
    val incomingWave: StateFlow<Attendee?> = _incomingWave.asStateFlow()

    // Callbacks to notify repository of peer events
    var onIncomingChatMessage: ((senderId: String, text: String, timestamp: Long) -> Unit)? = null
    var onIncomingBadgeExchange: ((ConnectionExchange) -> Unit)? = null

    // Base seeded attendees for realistic event proximity simulation with Stitch Hawaiian & Galactic theme
    private val baseEventAttendees = mutableListOf(
        Attendee(
            id = "att_lilo_pelekai",
            name = "Lilo Pelekai",
            title = "Hula UI/UX Motion Designer",
            company = "Ohana Design Studio",
            bio = "Crafting playful fluid micro-interactions in Jetpack Compose, tropical color tokens, and spatial haptics. Big believer in Ohana design principles.",
            role = RoleCategory.DESIGNER,
            interests = listOf("Hula Motion", "Compose", "Design Tokens", "Ohana UX", "Accessibility"),
            lookingFor = "Mobile engineers passionate about delightful micro-animations",
            offering = "Component design critiques, custom vector flower assets, hula energy",
            rssi = -49,
            distanceMeters = 0.9f,
            angleDegrees = 40f,
            linkedinUrl = "linkedin.com/in/lilo-pelekai-design",
            twitter = "@lilo_ohana",
            email = "lilo@ohanadesign.org",
            matchScore = 99
        ),
        Attendee(
            id = "att_jumba_jookiba",
            name = "Dr. Jumba Jookiba",
            title = "Lead Genetic AI & Edge Architect",
            company = "Galactic Federation Labs",
            bio = "Creator of Experiments 001 through 628! Specializing in autonomous edge neural nets, ultra-low power BLE mesh, and chaotic system resiliency.",
            role = RoleCategory.DEVELOPER,
            interests = listOf("Edge AI", "BLE Mesh", "Neural Distillation", "Experiment 626", "Rust"),
            lookingFor = "Evil geniuses and fearless Android systems engineers",
            offering = "Architecture blueprints for distributed micro-agents and NPU compilers",
            rssi = -54,
            distanceMeters = 1.4f,
            angleDegrees = 115f,
            linkedinUrl = "linkedin.com/in/dr-jumba-genius",
            githubUrl = "github.com/jumba-evilgenius",
            email = "jumba@galacticfed.org",
            matchScore = 97
        ),
        Attendee(
            id = "att_pleakley",
            name = "Agent Wendy Pleakley",
            title = "Earth Protocol & Security Lead",
            company = "Intergalactic Safety Council",
            bio = "Ensuring safe BLE transmission, mosquito habitat preservation, and zero-trust protocol encryption for Earth-space conference attendees.",
            role = RoleCategory.ORGANIZER,
            interests = listOf("Security", "Zero Trust", "BLE Privacy", "Earth Protocols", "Event Safety"),
            lookingFor = "Security researchers & privacy engineers",
            offering = "Security audits, intergalactic etiquette guidelines & fashion tips",
            rssi = -68,
            distanceMeters = 3.5f,
            angleDegrees = 210f,
            linkedinUrl = "linkedin.com/in/wendy-pleakley",
            twitter = "@pleakley_earth_expert",
            email = "pleakley@galacticfed.org",
            matchScore = 84
        ),
        Attendee(
            id = "att_sarah_chen",
            name = "Sarah Chen",
            title = "Staff AI Research Engineer",
            company = "Anthropic AI",
            bio = "Working on multimodal agent reasoning, on-device SLMs, and prompt distillation architectures. Looking to meet mobile AI builders.",
            role = RoleCategory.DEVELOPER,
            interests = listOf("On-Device AI", "Kotlin", "LLM Agents", "PyTorch", "Rust"),
            lookingFor = "Android devs exploring local SLMs & real-time inference",
            offering = "Benchmarking data, prompt eval frameworks, research advice",
            rssi = -58,
            distanceMeters = 1.8f,
            angleDegrees = 300f,
            linkedinUrl = "linkedin.com/in/sarahchen-ai",
            githubUrl = "github.com/sarahchen-ml",
            email = "sarah.chen@anthropic.com",
            matchScore = 94
        ),
        Attendee(
            id = "att_david_kawena",
            name = "David Kawena",
            title = "Founder & Fireknife Architect",
            company = "IslandWave Protocol",
            bio = "Surfing big waves and building decentralized peer-to-peer event coordination protocols on Android and KMP. Hiring Senior Kotlin devs.",
            role = RoleCategory.FOUNDER,
            interests = listOf("P2P Mesh", "Kotlin", "Surfing Tech", "BLE Sync", "Decentralization"),
            lookingFor = "Lead Android / BLE protocol architect",
            offering = "Founding equity, surf mentoring & pitch deck reviews",
            rssi = -62,
            distanceMeters = 2.4f,
            angleDegrees = 160f,
            linkedinUrl = "linkedin.com/in/david-kawena-island",
            githubUrl = "github.com/davidkawena",
            email = "david@islandwave.network",
            matchScore = 92
        ),
        Attendee(
            id = "att_marcus_vance",
            name = "Marcus Vance",
            title = "Partner & Cosmic Seed VC",
            company = "Nexus Horizon Ventures",
            bio = "Investing in Seed & Series A deep tech, developer tooling, edge computing, and mobile-first AI startups ($500k - $3M checks).",
            role = RoleCategory.INVESTOR,
            interests = listOf("Seed Stage", "DevTools", "Mobile AI", "Open Source", "Space Tech"),
            lookingFor = "Technical founders building high-retention products",
            offering = "Capital, customer intros, US market go-to-market scaling",
            rssi = -72,
            distanceMeters = 4.8f,
            angleDegrees = 85f,
            linkedinUrl = "linkedin.com/in/marcus-vance-vc",
            twitter = "@marcusvance_vc",
            email = "mvance@nexushrzn.com",
            matchScore = 88
        ),
        Attendee(
            id = "att_grand_councilwoman",
            name = "Grand Councilwoman",
            title = "Keynote Speaker & Leader",
            company = "United Galactic Federation",
            bio = "Keynote speaker on 'Federation Governance in the Age of Decentralized Autonomous Interplanetary Networks'.",
            role = RoleCategory.SPEAKER,
            interests = listOf("Keynote", "Governance", "Interplanetary Tech", "Federation Laws"),
            lookingFor = "Visionary tech leaders & researchers",
            offering = "Galactic alliance partnerships & keynote session materials",
            rssi = -76,
            distanceMeters = 5.8f,
            angleDegrees = 335f,
            linkedinUrl = "linkedin.com/in/grand-councilwoman",
            email = "councilwoman@galacticfed.org",
            matchScore = 89
        ),
        Attendee(
            id = "beacon_hula_lounge",
            name = "🌺 Hula Lounge & Shave Ice",
            title = "Venue Proximity Beacon",
            company = "Tropical Zone C - Shave Ice Bar",
            bio = "Fresh Hawaiian shave ice, coconut cold brew, acoustic ukulele tunes, and charging hubs. Current crowd density: Relaxed (28 people).",
            role = RoleCategory.VENUE_BEACON,
            interests = listOf("Shave Ice", "Ukulele", "Casual Chat", "Charging Hub"),
            lookingFor = "Attendees taking an Aloha break",
            offering = "Free rainbow shave ice & high-speed Wi-Fi",
            rssi = -53,
            distanceMeters = 1.3f,
            angleDegrees = 250f,
            isBeacon = true,
            beaconZone = "Zone C - Hula Lounge",
            matchScore = 100
        ),
        Attendee(
            id = "beacon_starship_stage",
            name = "🚀 Galactic Starship Stage Beacon",
            title = "Venue Proximity Beacon",
            company = "Auditorium Prime",
            bio = "Stream live translated keynote audio and holographic slide notes directly via BLE Auracast. Next talk in 10 mins.",
            role = RoleCategory.VENUE_BEACON,
            interests = listOf("Keynotes", "Space Tech", "Live Auracast"),
            lookingFor = "Keynote audience",
            offering = "Low-latency spatial audio & slide stream",
            rssi = -79,
            distanceMeters = 6.9f,
            angleDegrees = 18f,
            isBeacon = true,
            beaconZone = "Auditorium Prime",
            matchScore = 91
        )
    )

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            handleHardwareScanResult(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { handleHardwareScanResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.w("BleScanner", "BLE Scan failed with code: $errorCode")
        }
    }

    init {
        _localAttendees.value = baseEventAttendees

        // Wire NearbyConnectionsManager callbacks
        nearbyConnectionsManager.onIncomingWaveListener = { attendee ->
            _incomingWave.value = attendee
        }
        nearbyConnectionsManager.onChatMessageListener = { senderId, text, timestamp ->
            onIncomingChatMessage?.invoke(senderId, text, timestamp)
        }
        nearbyConnectionsManager.onBadgeExchangeListener = { connection ->
            onIncomingBadgeExchange?.invoke(connection)
        }

        // Combine local simulated & BLE-scanned attendees with real Nearby Connections peers
        coroutineScope.launch {
            combine(_localAttendees, nearbyConnectionsManager.nearbyPeers) { locals, peersMap ->
                val peersList = peersMap.values.toList()
                val peerIds = peersList.map { it.id }.toSet()
                val filteredLocals = locals.filterNot { peerIds.contains(it.id) }
                peersList + filteredLocals
            }.collect { merged ->
                _nearbyAttendees.value = merged
            }
        }

        startScanning()
        startBroadcasting()
    }

    private fun handleHardwareScanResult(result: ScanResult) {
        val device = result.device ?: return
        @SuppressLint("MissingPermission")
        val deviceName = device.name ?: result.scanRecord?.deviceName ?: return
        val rssi = result.rssi
        val distance = calculateDistance(rssi, -59)

        _localAttendees.update { list ->
            val existing = list.find { it.id == device.address }
            if (existing != null) {
                list.map {
                    if (it.id == device.address) {
                        it.copy(
                            rssi = (it.rssi * 0.7f + rssi * 0.3f).toInt(),
                            distanceMeters = (it.distanceMeters * 0.7f + distance * 0.3f),
                            lastSeenEpochMs = System.currentTimeMillis()
                        )
                    } else it
                }
            } else {
                val newAttendee = Attendee(
                    id = device.address,
                    name = deviceName,
                    title = "Nearby Attendee (BLE)",
                    company = "Event Participant",
                    bio = "Discovered via Bluetooth Low Energy proximity beacon broadcast.",
                    role = RoleCategory.DEVELOPER,
                    interests = listOf("Tech", "Networking", "Event"),
                    lookingFor = "Collaborators & conversations",
                    offering = "Open to connect",
                    rssi = rssi,
                    distanceMeters = distance,
                    angleDegrees = Random.nextFloat() * 360f,
                    lastSeenEpochMs = System.currentTimeMillis(),
                    matchScore = 75
                )
                _lastDiscovered.value = newAttendee
                list + newAttendee
            }
        }
    }

    fun startScanning() {
        _isScanning.value = true

        // 1. Start Google Play Services Nearby Connections BLE discovery
        try {
            nearbyConnectionsManager.startDiscovery()
        } catch (e: Exception) {
            Log.w("BleScanner", "Nearby Connections discovery start error", e)
        }

        // 2. Start hardware Bluetooth LE Scanner
        try {
            bleScanner = bluetoothAdapter?.bluetoothLeScanner
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            bleScanner?.startScan(null, settings, scanCallback)
        } catch (e: SecurityException) {
            Log.w("BleScanner", "Missing BLE scan permission, running smart proximity simulation", e)
        } catch (e: Exception) {
            Log.w("BleScanner", "BLE Scan init exception", e)
        }

        if (simulationJob?.isActive != true) {
            startSimulationEngine()
        }
    }

    fun stopScanning() {
        _isScanning.value = false

        // 1. Stop Nearby Connections BLE discovery
        try {
            nearbyConnectionsManager.stopDiscovery()
        } catch (e: Exception) {
            Log.w("BleScanner", "Nearby Connections discovery stop error", e)
        }

        // 2. Stop hardware BLE scanner
        try {
            bleScanner?.stopScan(scanCallback)
        } catch (e: SecurityException) {
            Log.w("BleScanner", "Missing permission to stop BLE scan", e)
        } catch (e: Exception) {
            Log.w("BleScanner", "Error stopping scan", e)
        }
    }

    fun startBroadcasting(profile: UserProfile? = null) {
        _isBroadcasting.value = true
        try {
            if (profile != null) {
                nearbyConnectionsManager.startAdvertising(profile)
            } else {
                nearbyConnectionsManager.startAdvertising()
            }
        } catch (e: Exception) {
            Log.w("BleScanner", "Nearby Connections advertising start error", e)
        }
    }

    fun stopBroadcasting() {
        _isBroadcasting.value = false
        try {
            nearbyConnectionsManager.stopAdvertising()
        } catch (e: Exception) {
            Log.w("BleScanner", "Nearby Connections advertising stop error", e)
        }
    }

    fun toggleScanning() {
        if (_isScanning.value) stopScanning() else startScanning()
    }

    fun toggleBroadcasting() {
        if (_isBroadcasting.value) stopBroadcasting() else startBroadcasting()
    }

    fun updateUserProfile(profile: UserProfile) {
        nearbyConnectionsManager.updateUserProfile(profile)
    }

    fun dismissIncomingWave() {
        _incomingWave.value = null
    }

    fun sendWave(attendeeId: String) {
        // Send real BLE payload via Nearby Connections
        nearbyConnectionsManager.sendWave(attendeeId)

        _localAttendees.update { list ->
            list.map {
                if (it.id == attendeeId) it.copy(waveSent = true) else it
            }
        }
    }

    fun sendChatMessage(attendeeId: String, text: String) {
        nearbyConnectionsManager.sendChatMessage(attendeeId, text)
    }

    fun sendBadgeExchange(attendeeId: String) {
        nearbyConnectionsManager.sendBadgeExchange(attendeeId)
    }

    fun connectWithAttendee(attendeeId: String) {
        _localAttendees.update { list ->
            list.map {
                if (it.id == attendeeId) it.copy(isConnected = true) else it
            }
        }
    }

    fun toggleSaveAttendee(attendeeId: String) {
        _localAttendees.update { list ->
            list.map {
                if (it.id == attendeeId) it.copy(isSaved = !it.isSaved) else it
            }
        }
    }

    // Realistic Proximity Simulation Engine: attendees move realistically in venue space,
    // signal strength fluctuates naturally, and occasional friendly waves/beacons trigger
    private fun startSimulationEngine() {
        simulationJob?.cancel()
        simulationJob = coroutineScope.launch {
            var cycleCount = 0
            while (isActive) {
                delay(1200)
                if (!_isScanning.value) continue

                cycleCount++

                _localAttendees.update { currentList ->
                    currentList.map { attendee ->
                        if (attendee.isBeacon) {
                            // Beacons have stable position with small RF multipath jitter
                            val rssiJitter = Random.nextInt(-2, 3)
                            val newRssi = (attendee.rssi + rssiJitter).coerceIn(-95, -40)
                            val newDist = calculateDistance(newRssi, -55)
                            attendee.copy(
                                rssi = newRssi,
                                distanceMeters = (attendee.distanceMeters * 0.85f + newDist * 0.15f)
                            )
                        } else {
                            // Attendees naturally stroll around the venue
                            val angleShift = Random.nextFloat() * 3f - 1.5f
                            val newAngle = (attendee.angleDegrees + angleShift + 360f) % 360f
                            val distDrift = Random.nextFloat() * 0.2f - 0.1f
                            val newDist = (attendee.distanceMeters + distDrift).coerceIn(0.6f, 25.0f)
                            val newRssi = calculateRssiFromDistance(newDist)
                            attendee.copy(
                                angleDegrees = newAngle,
                                distanceMeters = newDist,
                                rssi = newRssi,
                                lastSeenEpochMs = System.currentTimeMillis()
                            )
                        }
                    }
                }

                // Periodically trigger a realistic incoming wave from a nearby attendee (e.g. Sarah or David)
                if (cycleCount % 18 == 0 && _incomingWave.value == null) {
                    val candidate = _localAttendees.value.filter { !it.isBeacon && !it.waveReceived }.randomOrNull()
                    if (candidate != null) {
                        _incomingWave.value = candidate
                        _localAttendees.update { list ->
                            list.map { if (it.id == candidate.id) it.copy(waveReceived = true) else it }
                        }
                    }
                }
            }
        }
    }

    // Standard Log-Distance Path Loss Model for 2.4 GHz BLE:
    // RSSI = -10 * n * log10(d) + TxPower
    private fun calculateDistance(rssi: Int, txPower: Int = -59, pathLossExponent: Double = 2.4): Float {
        if (rssi == 0) return -1.0f
        val ratio = (txPower - rssi) / (10 * pathLossExponent)
        return (10.0.pow(ratio)).toFloat().coerceIn(0.3f, 40f)
    }

    private fun calculateRssiFromDistance(distanceMeters: Float, txPower: Int = -59, n: Double = 2.4): Int {
        val dist = distanceMeters.coerceAtLeast(0.3f)
        val rssi = txPower - (10 * n * kotlin.math.log10(dist.toDouble()))
        return rssi.toInt().coerceIn(-95, -42)
    }
}
