package com.br444n.unitwise.app.core.firebase.data

import android.os.Bundle
import com.br444n.unitwise.app.core.firebase.domain.TelemetryLogger
import com.br444n.unitwise.app.core.firebase.domain.UnitWiseEvent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseTelemetryLogger(
    private val analytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
) : TelemetryLogger {
    override fun logEvent(event: UnitWiseEvent) {
        // Log to Firebase Analytics
        val bundle =
            Bundle().apply {
                event.params.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Long -> putLong(key, value)
                        is Double -> putDouble(key, value)
                        is Float -> putFloat(key, value)
                        is Boolean -> putBoolean(key, value)
                        else -> putString(key, value.toString())
                    }
                }
            }
        analytics.logEvent(event.name, bundle)

        // Log to Crashlytics to provide context for crashes
        val crashlyticsLogMessage =
            buildString {
                append("Event: ${event.name}")
                if (event.params.isNotEmpty()) {
                    append(" | Params: ")
                    append(event.params.entries.joinToString { "${it.key}=${it.value}" })
                }
            }
        crashlytics.log(crashlyticsLogMessage)
    }
}
