package com.example.nearbyeventradar

import android.app.Application
import com.example.nearbyeventradar.data.ble.BleProximityScanner
import com.example.nearbyeventradar.data.local.AppDatabase
import com.example.nearbyeventradar.data.repository.RadarRepository

class RadarApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var bleScanner: BleProximityScanner
        private set

    lateinit var repository: RadarRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        bleScanner = BleProximityScanner(this)
        repository = RadarRepository(bleScanner, database.appDao())
    }
}
