package com.example.flip.domain.repository

import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.MultimodalDiagnosis
import com.example.flip.domain.model.SensorReading

interface AiDiagnosticRepository {
    suspend fun diagnoseMultimodal(
        fieldTwin: FieldTwin,
        latestTelemetry: SensorReading,
        imageUri: String?,
        observedLeafState: String,
        isSimulatedCamera: Boolean = false
    ): MultimodalDiagnosis

    suspend fun escalateToHumanExpert(
        diagnosisId: String,
        fieldId: String,
        farmerNote: String
    ): Boolean
}
