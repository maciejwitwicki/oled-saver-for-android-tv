package com.mwi.oledsaver.config

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.google.protobuf.Timestamp
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.config.SettingsProvider.SettingsSerializer
import com.mwi.oledsaver.proto.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class SettingsRepository(private val context: Context) {

    val Context.settingsDataStore: DataStore<Settings> by dataStore(
        fileName = "settings.pb",
        serializer = SettingsSerializer
    )

    private val settingsFlow: Flow<Settings> = context.settingsDataStore.data

    val started: Flow<Instant> = settingsFlow
        .map { settings ->
            val startedDate = Instant.ofEpochSecond(settings.started.seconds)
            Log.i(
                LOGGING_TAG,
                "SettingsRepository - retrieving started date: $startedDate"
            )
            startedDate
        }


    val maskIndex: Flow<Int> = settingsFlow
        .map { settings ->
            Log.i(
                LOGGING_TAG,
                "SettingsRepository - retrieving masking index: ${settings.maskIndex}"
            )
            settings.maskIndex
        }

    suspend fun setMaskIndex(newIndex: Int) {
        Log.i(LOGGING_TAG, "SettingsRepository - saving masking index: $newIndex")

        context.settingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setMaskIndex(newIndex)
                .build()
        }
    }

    suspend fun setStartedDate(date: Instant?) {
        Log.i(LOGGING_TAG, "SettingsRepository - saving started date: $date")

        context.settingsDataStore.updateData { currentSettings ->
            if (date != null) {
                currentSettings.toBuilder()
                    .setStarted(
                        Timestamp.newBuilder()
                            .setSeconds(date.epochSecond)
                    ).build()
            } else {
                currentSettings.toBuilder()
                    .setStarted(
                        Timestamp.getDefaultInstance()
                    ).build()
            }
        }

    }
}
