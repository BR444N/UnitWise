package com.br444n.unitwise.app.core.firebase.domain

fun interface TelemetryLogger {
    fun logEvent(event: UnitWiseEvent)
}
