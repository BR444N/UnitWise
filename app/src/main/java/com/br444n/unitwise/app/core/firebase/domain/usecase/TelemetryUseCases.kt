package com.br444n.unitwise.app.core.firebase.domain.usecase

import com.br444n.unitwise.app.core.firebase.domain.TelemetryLogger
import com.br444n.unitwise.app.core.firebase.domain.UnitWiseEvent

class LogOcrAttemptUseCase(
    private val logger: TelemetryLogger,
) {
    operator fun invoke(result: UnitWiseEvent.OcrAttempt.OcrResult) {
        logger.logEvent(UnitWiseEvent.OcrAttempt(result))
    }
}

class LogParityToggledUseCase(
    private val logger: TelemetryLogger,
) {
    operator fun invoke(isExpanded: Boolean) {
        logger.logEvent(UnitWiseEvent.ParityToggled(isExpanded))
    }
}

class LogListParityTypeUseCase(
    private val logger: TelemetryLogger,
) {
    operator fun invoke(isDual: Boolean) {
        logger.logEvent(UnitWiseEvent.ListParityType(isDual))
    }
}

class LogListSharedUseCase(
    private val logger: TelemetryLogger,
) {
    operator fun invoke(method: String) {
        logger.logEvent(UnitWiseEvent.ListShared)
    }
}
