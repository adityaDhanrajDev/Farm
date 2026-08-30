package com.example.flip.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.AdvisoryCategory
import com.example.flip.domain.model.AdvisoryItem
import com.example.flip.domain.model.CropStage
import com.example.flip.domain.model.Explanation5W
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.ProduceBatch
import com.example.flip.domain.model.QualityGrade
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SellDecision
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading
import com.example.flip.domain.model.SoilType
import com.example.flip.domain.model.WaterStatus

@Entity(tableName = "fields")
data class FieldEntity(
    @PrimaryKey val fieldId: String,
    val farmId: String,
    val name: String,
    val locationName: String,
    val cropName: String,
    val cropVariety: String,
    val cropStage: CropStage,
    val areaAcres: Double,
    val soilType: SoilType,
    val healthIndex: Int,
    val waterStatus: WaterStatus,
    val diseaseRisk: RiskLevel,
    val pestRisk: RiskLevel,
    val heatRisk: RiskLevel,
    val floodRisk: RiskLevel,
    val projectedYieldTons: Double,
    val daysToHarvest: Int,
    val activeActionState: String,
    val activeActionStateHi: String,
    val lastTelemetryTimestamp: Long
) {
    fun toDomain(): FieldTwin = FieldTwin(
        fieldId = fieldId,
        farmId = farmId,
        name = name,
        locationName = locationName,
        cropName = cropName,
        cropVariety = cropVariety,
        cropStage = cropStage,
        areaAcres = areaAcres,
        soilType = soilType,
        healthIndex = healthIndex,
        waterStatus = waterStatus,
        diseaseRisk = diseaseRisk,
        pestRisk = pestRisk,
        heatRisk = heatRisk,
        floodRisk = floodRisk,
        projectedYieldTons = projectedYieldTons,
        daysToHarvest = daysToHarvest,
        activeActionState = activeActionState,
        activeActionStateHi = activeActionStateHi,
        lastTelemetryTimestamp = lastTelemetryTimestamp
    )

    companion object {
        fun fromDomain(field: FieldTwin): FieldEntity = FieldEntity(
            fieldId = field.fieldId,
            farmId = field.farmId,
            name = field.name,
            locationName = field.locationName,
            cropName = field.cropName,
            cropVariety = field.cropVariety,
            cropStage = field.cropStage,
            areaAcres = field.areaAcres,
            soilType = field.soilType,
            healthIndex = field.healthIndex,
            waterStatus = field.waterStatus,
            diseaseRisk = field.diseaseRisk,
            pestRisk = field.pestRisk,
            heatRisk = field.heatRisk,
            floodRisk = field.floodRisk,
            projectedYieldTons = field.projectedYieldTons,
            daysToHarvest = field.daysToHarvest,
            activeActionState = field.activeActionState,
            activeActionStateHi = field.activeActionStateHi,
            lastTelemetryTimestamp = field.lastTelemetryTimestamp
        )
    }
}

@Entity(tableName = "sensor_readings")
data class SensorReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fieldId: String,
    val timestamp: Long,
    val soilMoisturePercent: Double,
    val soilTempC: Double,
    val airTempC: Double,
    val humidityPercent: Double,
    val rainMm: Double,
    val leafWetnessHours: Double,
    val irradianceLux: Double,
    val ecDsM: Double,
    val qualityStatus: SensorQualityStatus,
    val sensorReliabilityScore: Double
) {
    fun toDomain(): SensorReading = SensorReading(
        id = id,
        fieldId = fieldId,
        timestamp = timestamp,
        soilMoisturePercent = soilMoisturePercent,
        soilTempC = soilTempC,
        airTempC = airTempC,
        humidityPercent = humidityPercent,
        rainMm = rainMm,
        leafWetnessHours = leafWetnessHours,
        irradianceLux = irradianceLux,
        ecDsM = ecDsM,
        qualityStatus = qualityStatus,
        sensorReliabilityScore = sensorReliabilityScore
    )

    companion object {
        fun fromDomain(reading: SensorReading): SensorReadingEntity = SensorReadingEntity(
            id = reading.id,
            fieldId = reading.fieldId,
            timestamp = reading.timestamp,
            soilMoisturePercent = reading.soilMoisturePercent,
            soilTempC = reading.soilTempC,
            airTempC = reading.airTempC,
            humidityPercent = reading.humidityPercent,
            rainMm = reading.rainMm,
            leafWetnessHours = reading.leafWetnessHours,
            irradianceLux = reading.irradianceLux,
            ecDsM = reading.ecDsM,
            qualityStatus = reading.qualityStatus,
            sensorReliabilityScore = reading.sensorReliabilityScore
        )
    }
}

@Entity(tableName = "advisories")
data class AdvisoryEntity(
    @PrimaryKey val advisoryId: String,
    val fieldId: String,
    val category: AdvisoryCategory,
    val severity: RiskLevel,
    val title: String,
    val titleHi: String,
    val actionSummary: String,
    val actionSummaryHi: String,
    val what: String,
    val why: String,
    val whenWindow: String,
    val whatToDo: String,
    val confidenceText: String,
    val evidenceFactors: List<String>,
    val whatHi: String,
    val whyHi: String,
    val whenWindowHi: String,
    val whatToDoHi: String,
    val timestamp: Long,
    val isActionTaken: Boolean,
    val actionTakenTimestamp: Long?
) {
    fun toDomain(): AdvisoryItem = AdvisoryItem(
        advisoryId = advisoryId,
        fieldId = fieldId,
        category = category,
        severity = severity,
        title = title,
        titleHi = titleHi,
        actionSummary = actionSummary,
        actionSummaryHi = actionSummaryHi,
        explanation = Explanation5W(
            what = what,
            why = why,
            whenWindow = whenWindow,
            whatToDo = whatToDo,
            confidenceText = confidenceText,
            evidenceFactors = evidenceFactors,
            whatHi = whatHi,
            whyHi = whyHi,
            whenWindowHi = whenWindowHi,
            whatToDoHi = whatToDoHi
        ),
        timestamp = timestamp,
        isActionTaken = isActionTaken,
        actionTakenTimestamp = actionTakenTimestamp
    )

    companion object {
        fun fromDomain(item: AdvisoryItem): AdvisoryEntity = AdvisoryEntity(
            advisoryId = item.advisoryId,
            fieldId = item.fieldId,
            category = item.category,
            severity = item.severity,
            title = item.title,
            titleHi = item.titleHi,
            actionSummary = item.actionSummary,
            actionSummaryHi = item.actionSummaryHi,
            what = item.explanation.what,
            why = item.explanation.why,
            whenWindow = item.explanation.whenWindow,
            whatToDo = item.explanation.whatToDo,
            confidenceText = item.explanation.confidenceText,
            evidenceFactors = item.explanation.evidenceFactors,
            whatHi = item.explanation.whatHi,
            whyHi = item.explanation.whyHi,
            whenWindowHi = item.explanation.whenWindowHi,
            whatToDoHi = item.explanation.whatToDoHi,
            timestamp = item.timestamp,
            isActionTaken = item.isActionTaken,
            actionTakenTimestamp = item.actionTakenTimestamp
        )
    }
}

@Entity(tableName = "action_records")
data class ActionRecordEntity(
    @PrimaryKey val actionId: String,
    val advisoryId: String,
    val fieldId: String,
    val actionType: String,
    val scheduledDurationMinutes: Int,
    val executedAt: Long,
    val preActionMoisture: Double,
    val postActionMoisture: Double?,
    val expectedDelta: Double,
    val actualDelta: Double?,
    val verificationStatus: ActionVerificationStatus,
    val verificationNote: String,
    val verificationNoteHi: String
) {
    fun toDomain(): ActionRecord = ActionRecord(
        actionId = actionId,
        advisoryId = advisoryId,
        fieldId = fieldId,
        actionType = actionType,
        scheduledDurationMinutes = scheduledDurationMinutes,
        executedAt = executedAt,
        preActionMoisture = preActionMoisture,
        postActionMoisture = postActionMoisture,
        expectedDelta = expectedDelta,
        actualDelta = actualDelta,
        verificationStatus = verificationStatus,
        verificationNote = verificationNote,
        verificationNoteHi = verificationNoteHi
    )

    companion object {
        fun fromDomain(record: ActionRecord): ActionRecordEntity = ActionRecordEntity(
            actionId = record.actionId,
            advisoryId = record.advisoryId,
            fieldId = record.fieldId,
            actionType = record.actionType,
            scheduledDurationMinutes = record.scheduledDurationMinutes,
            executedAt = record.executedAt,
            preActionMoisture = record.preActionMoisture,
            postActionMoisture = record.postActionMoisture,
            expectedDelta = record.expectedDelta,
            actualDelta = record.actualDelta,
            verificationStatus = record.verificationStatus,
            verificationNote = record.verificationNote,
            verificationNoteHi = record.verificationNoteHi
        )
    }
}

@Entity(tableName = "produce_batches")
data class ProduceBatchEntity(
    @PrimaryKey val batchId: String,
    val cropName: String,
    val variety: String,
    val quantityQuintals: Double,
    val harvestDate: Long,
    val qualityGrade: QualityGrade,
    val storageTempC: Double,
    val storageHumidityPercent: Double,
    val spoilageRiskPercent: Int,
    val currentMandiPricePerQuintal: Double,
    val forecastedPrice30Days: Double,
    val storageCostPerDayPerQuintal: Double,
    val recommendation: SellDecision,
    val netExpectedMarginDiffPercent: Double,
    val matchedFpoBuyerName: String
) {
    fun toDomain(): ProduceBatch = ProduceBatch(
        batchId = batchId,
        cropName = cropName,
        variety = variety,
        quantityQuintals = quantityQuintals,
        harvestDate = harvestDate,
        qualityGrade = qualityGrade,
        storageTempC = storageTempC,
        storageHumidityPercent = storageHumidityPercent,
        spoilageRiskPercent = spoilageRiskPercent,
        currentMandiPricePerQuintal = currentMandiPricePerQuintal,
        forecastedPrice30Days = forecastedPrice30Days,
        storageCostPerDayPerQuintal = storageCostPerDayPerQuintal,
        recommendation = recommendation,
        netExpectedMarginDiffPercent = netExpectedMarginDiffPercent,
        matchedFpoBuyerName = matchedFpoBuyerName
    )

    companion object {
        fun fromDomain(batch: ProduceBatch): ProduceBatchEntity = ProduceBatchEntity(
            batchId = batch.batchId,
            cropName = batch.cropName,
            variety = batch.variety,
            quantityQuintals = batch.quantityQuintals,
            harvestDate = batch.harvestDate,
            qualityGrade = batch.qualityGrade,
            storageTempC = batch.storageTempC,
            storageHumidityPercent = batch.storageHumidityPercent,
            spoilageRiskPercent = batch.spoilageRiskPercent,
            currentMandiPricePerQuintal = batch.currentMandiPricePerQuintal,
            forecastedPrice30Days = batch.forecastedPrice30Days,
            storageCostPerDayPerQuintal = batch.storageCostPerDayPerQuintal,
            recommendation = batch.recommendation,
            netExpectedMarginDiffPercent = batch.netExpectedMarginDiffPercent,
            matchedFpoBuyerName = batch.matchedFpoBuyerName
        )
    }
}

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val queueId: String,
    val entityType: String,
    val payloadJson: String,
    val timestamp: Long,
    val retryCount: Int = 0,
    val status: String = "PENDING_SYNC"
)
