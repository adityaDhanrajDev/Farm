package com.example.flip.presentation.viewmodel

import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.AdvisoryItem
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.HotspotZone
import com.example.flip.domain.model.MultimodalDiagnosis
import com.example.flip.domain.model.PredictionForecast
import com.example.flip.domain.model.ProduceBatch
import com.example.flip.domain.model.SensorReading
import com.example.flip.domain.usecase.IrrigationDecision
import com.example.flip.domain.usecase.VoiceResponse

data class FlipUiState(
    val isLoading: Boolean = false,
    val isHindi: Boolean = false, // Language toggle (English / Hindi)
    val fields: List<FieldTwin> = emptyList(),
    val selectedField: FieldTwin? = null,
    val latestTelemetry: SensorReading? = null,
    val telemetryHistory: List<SensorReading> = emptyList(),
    val activeAdvisories: List<AdvisoryItem> = emptyList(),
    val actionHistory: List<ActionRecord> = emptyList(),
    val produceBatches: List<ProduceBatch> = emptyList(),
    val regionalHotspots: List<HotspotZone> = emptyList(),
    val predictionForecasts: List<PredictionForecast> = emptyList(),

    // Multimodal Diagnosis State
    val isDiagnosing: Boolean = false,
    val latestDiagnosis: MultimodalDiagnosis? = null,
    val expertEscalationSuccess: Boolean = false,

    // Smart Irrigation Decision State
    val currentIrrigationDecision: IrrigationDecision? = null,
    val isExecutingIrrigation: Boolean = false,
    val actionVerificationResult: ActionRecord? = null,

    // Voice Assistant State
    val isVoiceModalOpen: Boolean = false,
    val isListeningVoice: Boolean = false,
    val voiceQueryText: String = "",
    val voiceResponse: VoiceResponse? = null,

    // Sync State
    val isSyncing: Boolean = false,
    val lastSyncMessage: String? = null
)
