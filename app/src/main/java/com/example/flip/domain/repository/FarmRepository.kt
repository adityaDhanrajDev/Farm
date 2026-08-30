package com.example.flip.domain.repository

import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.AdvisoryItem
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.HotspotZone
import com.example.flip.domain.model.PredictionForecast
import com.example.flip.domain.model.ProduceBatch
import com.example.flip.domain.model.SensorReading
import kotlinx.coroutines.flow.Flow

interface FarmRepository {
    fun getFieldsStream(): Flow<List<FieldTwin>>
    fun getFieldByIdStream(fieldId: String): Flow<FieldTwin?>
    fun getLatestTelemetryStream(fieldId: String): Flow<SensorReading?>
    fun getTelemetryHistoryStream(fieldId: String): Flow<List<SensorReading>>
    fun getActiveAdvisoriesStream(fieldId: String): Flow<List<AdvisoryItem>>
    fun getActionHistoryStream(fieldId: String): Flow<List<ActionRecord>>
    fun getProduceBatchesStream(): Flow<List<ProduceBatch>>
    fun getRegionalHotspotsStream(): Flow<List<HotspotZone>>
    fun getPredictionForecasts(fieldId: String): List<PredictionForecast>

    suspend fun insertSensorReading(reading: SensorReading)
    suspend fun updateFieldTwin(fieldTwin: FieldTwin)
    suspend fun recordAction(actionRecord: ActionRecord)
    suspend fun updateActionVerification(actionId: String, status: ActionVerificationStatus, postMoisture: Double, note: String, noteHi: String)
    suspend fun markAdvisoryActionTaken(advisoryId: String)
    suspend fun addProduceBatch(batch: ProduceBatch)
    suspend fun triggerSimulationScenario(fieldId: String, scenarioType: String)
    suspend fun syncOfflineData(): Boolean
}
