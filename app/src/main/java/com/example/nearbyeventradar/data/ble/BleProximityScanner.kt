package com.example.nearbyeventradar.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.security.SecureRandom

/** Native BLE MVP: no device name, MAC, profile, pairing or GATT connection is used. */
class BleProximityScanner(context: Context) {
    companion object {
        private const val TAG = "NearbyRadarBle"
        private const val VERSION: Byte = 1
        private const val ID_BYTES = 7
        private const val ROTATION_MS = 15 * 60 * 1000L
        private const val EXPIRY_MS = 25_000L
        val SERVICE_UUID: ParcelUuid = ParcelUuid.fromString("4d6f8d26-51c8-4dbd-9d66-7b473f5d5ca1")
    }

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val manager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val adapter: BluetoothAdapter? get() = manager?.adapter
    private var advertiser: BluetoothLeAdvertiser? = null
    private var scanner: BluetoothLeScanner? = null
    private var rotationJob: Job? = null
    private var cleanupJob: Job? = null
    private var payload = newPayload()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()
    private val _isBroadcasting = MutableStateFlow(false)
    val isBroadcasting: StateFlow<Boolean> = _isBroadcasting.asStateFlow()
    private val _nearbyAttendees = MutableStateFlow<List<Attendee>>(emptyList())
    val nearbyAttendees: StateFlow<List<Attendee>> = _nearbyAttendees.asStateFlow()
    private val _lastDiscovered = MutableStateFlow<Attendee?>(null)
    val lastDiscovered: StateFlow<Attendee?> = _lastDiscovered.asStateFlow()
    private val _incomingWave = MutableStateFlow<Attendee?>(null)
    val incomingWave: StateFlow<Attendee?> = _incomingWave.asStateFlow()

    // Kept only for compatibility with the current UI. This MVP cannot send chats or contacts.
    var onIncomingChatMessage: ((String, String, Long) -> Unit)? = null
    var onIncomingBadgeExchange: ((ConnectionExchange) -> Unit)? = null

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) { _isBroadcasting.value = true }
        override fun onStartFailure(errorCode: Int) {
            _isBroadcasting.value = false
            Log.w(TAG, "Advertising BLE failed: $errorCode")
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) = handleScanResult(result)
        override fun onBatchScanResults(results: MutableList<ScanResult>) = results.forEach(::handleScanResult)
        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
            Log.w(TAG, "Scanning BLE failed: $errorCode")
        }
    }

    fun startScanning() {
        if (!canScan() || adapter?.isEnabled != true) return
        scanner = adapter?.bluetoothLeScanner ?: return
        try {
            scanner?.startScan(
                listOf(ScanFilter.Builder().setServiceUuid(SERVICE_UUID).build()),
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
                scanCallback
            )
            _isScanning.value = true
            startCleanup()
        } catch (error: SecurityException) { Log.w(TAG, "Missing BLE_SCAN permission", error) }
    }

    fun stopScanning() {
        try { scanner?.stopScan(scanCallback) } catch (_: SecurityException) { }
        _isScanning.value = false
        cleanupJob?.cancel()
    }

    fun startBroadcasting(profile: UserProfile? = null) {
        // The profile deliberately remains local. Advertising contains only a rotating random ID.
        if (!canAdvertise() || adapter?.isEnabled != true || adapter?.isMultipleAdvertisementSupported != true) return
        advertiser = adapter?.bluetoothLeAdvertiser ?: return
        advertise()
        rotationJob?.cancel()
        rotationJob = scope.launch {
            while (isActive) {
                delay(ROTATION_MS)
                payload = newPayload()
                advertise()
            }
        }
    }

    private fun advertise() {
        try {
            advertiser?.stopAdvertising(advertiseCallback)
            val settings = AdvertiseSettings.Builder().setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM).setConnectable(false).build()
            val data = AdvertiseData.Builder().addServiceUuid(SERVICE_UUID).addServiceData(SERVICE_UUID, payload)
                .setIncludeDeviceName(false).setIncludeTxPowerLevel(false).build()
            advertiser?.startAdvertising(settings, data, advertiseCallback)
        } catch (error: SecurityException) { Log.w(TAG, "Missing BLE_ADVERTISE permission", error) }
    }

    fun stopBroadcasting() {
        try { advertiser?.stopAdvertising(advertiseCallback) } catch (_: SecurityException) { }
        rotationJob?.cancel()
        _isBroadcasting.value = false
    }

    fun toggleScanning() = if (_isScanning.value) stopScanning() else startScanning()
    fun toggleBroadcasting() = if (_isBroadcasting.value) stopBroadcasting() else startBroadcasting()
    fun updateUserProfile(profile: UserProfile) { /* Profile never goes into BLE advertising. */ }
    fun dismissIncomingWave() { _incomingWave.value = null }
    fun sendWave(attendeeId: String) { Log.i(TAG, "Greeting unavailable in anonymous BLE MVP") }
    fun sendChatMessage(attendeeId: String, text: String) { Log.i(TAG, "Chat unavailable in anonymous BLE MVP") }
    fun sendBadgeExchange(attendeeId: String) { }
    fun connectWithAttendee(attendeeId: String) { }
    fun toggleSaveAttendee(attendeeId: String) { }

    @SuppressLint("MissingPermission")
    private fun handleScanResult(result: ScanResult) {
        val bytes = result.scanRecord?.getServiceData(SERVICE_UUID) ?: return
        if (bytes.size != ID_BYTES + 1 || bytes.first() != VERSION) return
        val id = Base64.encodeToString(bytes.copyOfRange(1, bytes.size), Base64.NO_WRAP or Base64.URL_SAFE)
        val previous = _nearbyAttendees.value.firstOrNull { it.id == id }
        val smoothed = previous?.let { (it.rssi * .72f + result.rssi * .28f).toInt() } ?: result.rssi
        val attendee = anonymousAttendee(id, smoothed, System.currentTimeMillis())
        _nearbyAttendees.update { list -> (list.filterNot { it.id == id } + attendee).sortedByDescending { it.rssi } }
        _lastDiscovered.value = attendee
    }

    private fun anonymousAttendee(id: String, rssi: Int, now: Long): Attendee = Attendee(
        id = id, name = "Usuario Nearby", title = proximityLabel(rssi), company = "Identificador temporal",
        bio = "Detectado por Nearby Radar. No se han recibido datos personales.", role = RoleCategory.DEVELOPER,
        interests = emptyList(), lookingFor = "", offering = "", rssi = rssi,
        // Used only to position the dot on this initial radar UI; never displayed as an exact distance.
        distanceMeters = if (rssi >= -58) 1f else if (rssi >= -72) 4f else 9f,
        angleDegrees = (id.hashCode().toUInt().toLong() % 360).toFloat(), lastSeenEpochMs = now, matchScore = 0
    )

    private fun proximityLabel(rssi: Int) = when {
        rssi >= -58 -> "Muy cerca · RSSI $rssi dBm"
        rssi >= -72 -> "Cerca · RSSI $rssi dBm"
        else -> "A cierta distancia · RSSI $rssi dBm"
    }

    private fun startCleanup() {
        cleanupJob?.cancel()
        cleanupJob = scope.launch {
            while (isActive) {
                delay(5_000)
                val limit = System.currentTimeMillis() - EXPIRY_MS
                _nearbyAttendees.update { it.filter { peer -> peer.lastSeenEpochMs >= limit } }
            }
        }
    }

    private fun newPayload() = ByteArray(ID_BYTES + 1).also { output ->
        output[0] = VERSION
        val randomId = ByteArray(ID_BYTES)
        SecureRandom().nextBytes(randomId)
        randomId.copyInto(output, destinationOffset = 1)
    }
    private fun canScan() = hasPermission(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) android.Manifest.permission.BLUETOOTH_SCAN else android.Manifest.permission.ACCESS_FINE_LOCATION)
    private fun canAdvertise() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || hasPermission(android.Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(appContext, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}
