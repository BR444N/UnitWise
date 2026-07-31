package com.br444n.unitwise.app.core.firebase.domain

/**
 * Representa de forma segura y tipada los eventos del negocio
 * sin acoplamiento a ningún SDK externo.
 */
sealed class UnitWiseEvent(
    val name: String,
    val params: Map<String, Any> = emptyMap(),
) {
    class OcrAttempt(
        result: OcrResult,
    ) : UnitWiseEvent(
            name = "ocr_scan_attempt",
            params = mapOf("result" to result.value),
        ) {
        enum class OcrResult(
            val value: String,
        ) {
            SUCCESS("success"),
            RETRY("retry"),
            MANUAL_FALLBACK("manual_fallback"),
        }
    }

    class ParityToggled(
        isExpanded: Boolean,
    ) : UnitWiseEvent(
            name = "asymmetric_parity_viewed",
            params = mapOf("action" to if (isExpanded) "expand" else "collapse"),
        )

    class ListParityType(
        isDual: Boolean,
    ) : UnitWiseEvent(
            name = "product_parity_type",
            params = mapOf("type" to if (isDual) "dual_comparison" else "single_establishment"),
        )

    data object ListShared : UnitWiseEvent(name = "share_list_clicked")
}
