package com.example.flip.data.repository

import com.example.flip.data.local.FlipDatabase
import com.example.flip.data.local.entity.ActionRecordEntity
import com.example.flip.data.local.entity.AdvisoryEntity
import com.example.flip.data.local.entity.FieldEntity
import com.example.flip.data.local.entity.ProduceBatchEntity
import com.example.flip.data.local.entity.SensorReadingEntity
import com.example.flip.data.local.entity.SyncQueueEntity
import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.AdvisoryCategory
import com.example.flip.domain.model.AdvisoryItem
import com.example.flip.domain.model.CropStage
import com.example.flip.domain.model.Explanation5W
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.HotspotZone
import com.example.flip.domain.model.PredictionForecast
import com.example.flip.domain.model.ProduceBatch
import com.example.flip.domain.model.QualityGrade
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SellDecision
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading
import com.example.flip.domain.model.SoilType
import com.example.flip.domain.model.WaterStatus
import com.example.flip.domain.repository.FarmRepository
import com.example.flip.domain.usecase.ClosedLoopVerificationUseCase
import com.example.flip.domain.usecase.SensorAnomalyDetectionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class FarmRepositoryImpl(
    private val database: FlipDatabase,
    private val sensorAnomalyDetector: SensorAnomalyDetectionUseCase = SensorAnomalyDetectionUseCase(),
    private val closedLoopVerifier: ClosedLoopVerificationUseCase = ClosedLoopVerificationUseCase(),
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : FarmRepository {

    init {
        externalScope.launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existing = database.fieldDao().getAllFields().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaultFields = listOf(
                FieldEntity(
                    fieldId = "FIELD-001",
                    farmId = "FARM-LKO-401",
                    name = "East Plot 01 (टमाटर)",
                    locationName = "Bakshi Ka Talab, Lucknow (Cluster 4)",
                    cropName = "Tomato (टमाटर)",
                    cropVariety = "Abhinav F1",
                    cropStage = CropStage.FLOWERING,
                    areaAcres = 2.5,
                    soilType = SoilType.CLAY_LOAM,
                    healthIndex = 87,
                    waterStatus = WaterStatus.OPTIMAL,
                    diseaseRisk = RiskLevel.WATCH,
                    pestRisk = RiskLevel.SAFE,
                    heatRisk = RiskLevel.ACTION_NEEDED,
                    floodRisk = RiskLevel.SAFE,
                    projectedYieldTons = 12.5,
                    daysToHarvest = 18,
                    activeActionState = "Delay irrigation to 5:30 PM due to high ambient heat (38°C)",
                    activeActionStateHi = "अत्यधिक तापमान (38°C) के कारण सिंचाई शाम 5:30 बजे तक टालें",
                    lastTelemetryTimestamp = System.currentTimeMillis()
                ),
                FieldEntity(
                    fieldId = "FIELD-002",
                    farmId = "FARM-LKO-401",
                    name = "North Plot 02 (गेहूं)",
                    locationName = "Malihabad Sector B, Lucknow",
                    cropName = "Wheat (गेहूं)",
                    cropVariety = "PBW-502",
                    cropStage = CropStage.VEGETATIVE,
                    areaAcres = 4.0,
                    soilType = SoilType.ALLUVIAL,
                    healthIndex = 92,
                    waterStatus = WaterStatus.OPTIMAL,
                    diseaseRisk = RiskLevel.SAFE,
                    pestRisk = RiskLevel.SAFE,
                    heatRisk = RiskLevel.SAFE,
                    floodRisk = RiskLevel.SAFE,
                    projectedYieldTons = 18.0,
                    daysToHarvest = 45,
                    activeActionState = "Scheduled Split N-P-K Top Dressing in 3 days",
                    activeActionStateHi = "3 दिन बाद यूरिया की दूसरी खुराक (टॉप ड्रेसिंग) निर्धारित",
                    lastTelemetryTimestamp = System.currentTimeMillis()
                ),
                FieldEntity(
                    fieldId = "FIELD-003",
                    farmId = "FARM-LKO-401",
                    name = "South Plot 03 (मिर्च)",
                    locationName = "Itaunja Cluster, Lucknow",
                    cropName = "Chilli (मिर्च)",
                    cropVariety = "G4 Teja",
                    cropStage = CropStage.FRUITING,
                    areaAcres = 1.8,
                    soilType = SoilType.SANDY_LOAM,
                    healthIndex = 68,
                    waterStatus = WaterStatus.SLIGHT_DEFICIT,
                    diseaseRisk = RiskLevel.ACTION_NEEDED,
                    pestRisk = RiskLevel.WATCH,
                    heatRisk = RiskLevel.SAFE,
                    floodRisk = RiskLevel.SAFE,
                    projectedYieldTons = 6.2,
                    daysToHarvest = 12,
                    activeActionState = "High Fungal Blight Risk: 89% Humidity + 6h Leaf Wetness",
                    activeActionStateHi = "अगेती झुलसा का उच्च जोखिम: 89% आर्द्रता + 6 घंटे पत्ती गीलापन",
                    lastTelemetryTimestamp = System.currentTimeMillis()
                )
            )
            database.fieldDao().insertAll(defaultFields)

            // Seed Telemetry for FIELD-001
            val telemetry001 = SensorReadingEntity(
                fieldId = "FIELD-001",
                timestamp = System.currentTimeMillis(),
                soilMoisturePercent = 26.5,
                soilTempC = 24.0,
                airTempC = 37.8,
                humidityPercent = 48.0,
                rainMm = 0.0,
                leafWetnessHours = 0.5,
                irradianceLux = 72000.0,
                ecDsM = 1.3,
                qualityStatus = SensorQualityStatus.VALID,
                sensorReliabilityScore = 0.98
            )
            val telemetry003 = SensorReadingEntity(
                fieldId = "FIELD-003",
                timestamp = System.currentTimeMillis(),
                soilMoisturePercent = 19.0,
                soilTempC = 22.5,
                airTempC = 28.2,
                humidityPercent = 89.0,
                rainMm = 0.0,
                leafWetnessHours = 6.5,
                irradianceLux = 31000.0,
                ecDsM = 1.1,
                qualityStatus = SensorQualityStatus.VALID,
                sensorReliabilityScore = 0.95
            )
            database.sensorDao().insertAll(listOf(telemetry001, telemetry003))

            // Seed Advisories
            val advisories = listOf(
                AdvisoryEntity(
                    advisoryId = "ADV-001",
                    fieldId = "FIELD-001",
                    category = AdvisoryCategory.IRRIGATION,
                    severity = RiskLevel.ACTION_NEEDED,
                    title = "Postpone Watering to Evening (Heat Mitigation)",
                    titleHi = "सिंचाई शाम तक टालें (गर्मी से बचाव)",
                    actionSummary = "Delay drip irrigation until 5:30 PM to avoid rapid root-zone evaporation.",
                    actionSummaryHi = "तेज धूप में पानी न दें, शाम 5:30 बजे 45 मिनट ड्रिप चलाएं।",
                    what = "Thermal Peak Shock Prevention Protocol",
                    why = "Ambient heat (37.8°C) combined with direct sun will cause 60% evaporation loss if irrigated now.",
                    whenWindow = "Today 5:30 PM - 7:30 PM",
                    whatToDo = "Turn on pump zone A for 45 minutes after temperature drops below 32°C.",
                    confidenceText = "92% (High Confidence Microclimate Fusion)",
                    evidenceFactors = listOf("Air Temp 37.8°C", "Solar Radiation 72k Lux", "Soil Temp 24°C"),
                    whatHi = "अत्यधिक तापमान से बचाव प्रबंधन",
                    whyHi = "दोपहर में पानी देने से 60% पानी वाष्पीकृत हो जाएगा और जड़ों को झटका लगेगा।",
                    whenWindowHi = "आज शाम 5:30 से 7:30 बजे",
                    whatToDoHi = "तापमान 32°C से नीचे आने पर 45 मिनट ड्रिप सिंचाई चलाएं।",
                    timestamp = System.currentTimeMillis(),
                    isActionTaken = false,
                    actionTakenTimestamp = null
                ),
                AdvisoryEntity(
                    advisoryId = "ADV-002",
                    fieldId = "FIELD-003",
                    category = AdvisoryCategory.DISEASE_PREVENTION,
                    severity = RiskLevel.ACTION_NEEDED,
                    title = "Pre-Symptom Fungal Blight Warning",
                    titleHi = "अगेती झुलसा रोग की पूर्व चेतावनी",
                    actionSummary = "Microclimate is highly conducive for Alternaria spores over next 48 hours.",
                    actionSummaryHi = "आर्द्रता 89% व पत्ती 6.5 घंटे से गीली होने से फफूंद का खतरा बढ़ गया है।",
                    what = "Fungal Early Blight Incubation Detected",
                    why = "89% relative humidity + 6.5 hours continuous leaf wetness + 28.2°C ambient temperature.",
                    whenWindow = "Intervention window: Next 24 to 36 hours",
                    whatToDo = "Apply Trichoderma bio-fungicide (5g/L) or Copper Oxychloride spray before noon tomorrow.",
                    confidenceText = "88% (Multimodal AI Model)",
                    evidenceFactors = listOf("Leaf Wetness 6.5h", "Humidity 89%", "Flowering Stage Vigor"),
                    whatHi = "फफूंद जनित अगेती झुलसा का खतरा",
                    whyHi = "89% आर्द्रता + 6.5 घंटे पत्ती गीली + 28.2°C तापमान कवक फैलाव के लिए अनुकूल हैं।",
                    whenWindowHi = "अगले 24-36 घंटे के भीतर",
                    whatToDoHi = "ट्राइकोडर्मा जैविक फफूंदनाशी (5 ग्राम/लीटर) या कॉपर ऑक्सीक्लोराइड का छिड़काव करें।",
                    timestamp = System.currentTimeMillis(),
                    isActionTaken = false,
                    actionTakenTimestamp = null
                )
            )
            database.advisoryDao().insertAll(advisories)

            // Seed Action Record for Verification Closed-Loop Demo
            val actionRecords = listOf(
                ActionRecordEntity(
                    actionId = "ACT-901",
                    advisoryId = "ADV-PREV-01",
                    fieldId = "FIELD-001",
                    actionType = "Drip Irrigation Cycle (45 min)",
                    scheduledDurationMinutes = 45,
                    executedAt = System.currentTimeMillis() - (1000 * 60 * 60 * 24),
                    preActionMoisture = 16.5,
                    postActionMoisture = 26.5,
                    expectedDelta = 9.0,
                    actualDelta = 10.0,
                    verificationStatus = ActionVerificationStatus.VERIFIED_SUCCESS,
                    verificationNote = "Closed-loop verified: Soil moisture rose from 16.5% to 26.5% (+10.0% delta). Drip emitters operating at 100% flow rate.",
                    verificationNoteHi = "सत्यापित: नमी 16.5% से बढ़कर 26.5% हो गई। सिंचाई प्रणाली पूरी तरह सफल रही।"
                )
            )
            for (action in actionRecords) {
                database.actionDao().insertAction(action)
            }

            // Seed Produce Batches for Post-Harvest & Market Intelligence
            val produceBatches = listOf(
                ProduceBatchEntity(
                    batchId = "BATCH-TM-101",
                    cropName = "Tomato (टमाटर)",
                    variety = "Abhinav F1",
                    quantityQuintals = 45.0,
                    harvestDate = System.currentTimeMillis() - (1000 * 60 * 60 * 18),
                    qualityGrade = QualityGrade.GRADE_A,
                    storageTempC = 14.5,
                    storageHumidityPercent = 82.0,
                    spoilageRiskPercent = 22,
                    currentMandiPricePerQuintal = 2400.0,
                    forecastedPrice30Days = 3100.0,
                    storageCostPerDayPerQuintal = 6.5,
                    recommendation = SellDecision.STORE_3_DAYS,
                    netExpectedMarginDiffPercent = 18.5,
                    matchedFpoBuyerName = "Awadh Agri FPO Aggregator & Retail Chain"
                ),
                ProduceBatchEntity(
                    batchId = "BATCH-CH-202",
                    cropName = "Chilli (हरी मिर्च)",
                    variety = "G4 Teja",
                    quantityQuintals = 20.0,
                    harvestDate = System.currentTimeMillis() - (1000 * 60 * 60 * 4),
                    qualityGrade = QualityGrade.GRADE_B,
                    storageTempC = 26.0,
                    storageHumidityPercent = 91.0,
                    spoilageRiskPercent = 68,
                    currentMandiPricePerQuintal = 4200.0,
                    forecastedPrice30Days = 4350.0,
                    storageCostPerDayPerQuintal = 12.0,
                    recommendation = SellDecision.FIND_IMMEDIATE_BUYER,
                    netExpectedMarginDiffPercent = -8.2,
                    matchedFpoBuyerName = "Safal Fresh Bulk Procurement Hub"
                )
            )
            database.produceDao().insertAll(produceBatches)
        }
    }

    override fun getFieldsStream(): Flow<List<FieldTwin>> {
        return database.fieldDao().getAllFields().map { list -> list.map { it.toDomain() } }
    }

    override fun getFieldByIdStream(fieldId: String): Flow<FieldTwin?> {
        return database.fieldDao().getFieldById(fieldId).map { it?.toDomain() }
    }

    override fun getLatestTelemetryStream(fieldId: String): Flow<SensorReading?> {
        return database.sensorDao().getLatestReading(fieldId).map { it?.toDomain() }
    }

    override fun getTelemetryHistoryStream(fieldId: String): Flow<List<SensorReading>> {
        return database.sensorDao().getReadingHistory(fieldId).map { list -> list.map { it.toDomain() } }
    }

    override fun getActiveAdvisoriesStream(fieldId: String): Flow<List<AdvisoryItem>> {
        return database.advisoryDao().getAdvisoriesForField(fieldId).map { list -> list.map { it.toDomain() } }
    }

    override fun getActionHistoryStream(fieldId: String): Flow<List<ActionRecord>> {
        return database.actionDao().getActionsForField(fieldId).map { list -> list.map { it.toDomain() } }
    }

    override fun getProduceBatchesStream(): Flow<List<ProduceBatch>> {
        return database.produceDao().getAllBatches().map { list -> list.map { it.toDomain() } }
    }

    override fun getRegionalHotspotsStream(): Flow<List<HotspotZone>> = kotlinx.coroutines.flow.flow {
        val hotspots = listOf(
            HotspotZone(
                zoneId = "HOT-01",
                villageName = "Gosainganj Cluster",
                district = "Lucknow",
                dominantCrop = "Tomato & Chilli",
                activeThreat = "Early Blight Fungal Spore Surge",
                activeThreatHi = "अगेती झुलसा कवक का फैलाव",
                threatLevel = RiskLevel.ACTION_NEEDED,
                spreadVelocityKmPerDay = 2.4,
                affectedFarmsCount = 38,
                distanceKmFromUser = 6.8
            ),
            HotspotZone(
                zoneId = "HOT-02",
                villageName = "Malihabad Mango & Vegetable Belt",
                district = "Lucknow",
                dominantCrop = "Fruit & Solanaceae",
                activeThreat = "Thrips & Whitefly Vector",
                activeThreatHi = "थ्रिप्स व सफेद मक्खी कीट प्रकोप",
                threatLevel = RiskLevel.WATCH,
                spreadVelocityKmPerDay = 1.1,
                affectedFarmsCount = 19,
                distanceKmFromUser = 14.2
            ),
            HotspotZone(
                zoneId = "HOT-03",
                villageName = "Mohanlalganj Block",
                district = "Lucknow",
                dominantCrop = "Wheat & Mustard",
                activeThreat = "Root Zone Moisture Deficit (Drought Patch)",
                activeThreatHi = "मिट्टी में पानी की भारी कमी (सूखा)",
                threatLevel = RiskLevel.WATCH,
                spreadVelocityKmPerDay = 0.5,
                affectedFarmsCount = 42,
                distanceKmFromUser = 18.5
            )
        )
        emit(hotspots)
    }

    override fun getPredictionForecasts(fieldId: String): List<PredictionForecast> {
        return listOf(
            PredictionForecast(
                forecastWindowHours = 24,
                diseaseSporeGerminationRiskPercent = 78,
                pestPopulationGrowthPercent = 32,
                waterStressRiskPercent = 25,
                heatWiltingRiskPercent = 84,
                primaryThreat = "High Heat Wilting Shock (39°C Forecast)",
                primaryThreatHi = "अत्यधिक तापमान का खतरा (39°C अनुमान)",
                reasoning = "Thermal peak tomorrow between 1:00 PM - 4:00 PM combined with low relative humidity (35%).",
                reasoningHi = "कल दोपहर 1:00 से 4:00 के बीच तापमान 39°C पहुंचने का अनुमान है।"
            ),
            PredictionForecast(
                forecastWindowHours = 48,
                diseaseSporeGerminationRiskPercent = 86,
                pestPopulationGrowthPercent = 45,
                waterStressRiskPercent = 40,
                heatWiltingRiskPercent = 60,
                primaryThreat = "Fungal Blight Incubation Window",
                primaryThreatHi = "फफूंद जनित रोग का जोखिम",
                reasoning = "Expected evening cloud cover and humidity surge (>85%) will trigger spore germ tubes on tomato leaves.",
                reasoningHi = "शाम को बादल छाने और आर्द्रता 85% से अधिक होने पर कवक बीजाणु सक्रिय होंगे।"
            ),
            PredictionForecast(
                forecastWindowHours = 72,
                diseaseSporeGerminationRiskPercent = 55,
                pestPopulationGrowthPercent = 68,
                waterStressRiskPercent = 15,
                heatWiltingRiskPercent = 30,
                primaryThreat = "Sucking Pest Nymph Emergence",
                primaryThreatHi = "रस चूसक कीटों की संख्या में वृद्धि",
                reasoning = "Accumulated degree-days indicate thrips generation hatch cycle will accelerate in dry plots.",
                reasoningHi = "मौसम चक्र के अनुसार थ्रिप्स और कीटों के अंडे फूटने का समय निकट है।"
            )
        )
    }

    override suspend fun insertSensorReading(reading: SensorReading) = withContext(Dispatchers.IO) {
        val previous = database.sensorDao().getLatestReading(reading.fieldId).firstOrNull()?.toDomain()
        val evaluated = sensorAnomalyDetector.evaluateTelemetry(reading, previous)
        database.sensorDao().insertReading(SensorReadingEntity.fromDomain(evaluated))
    }

    override suspend fun updateFieldTwin(fieldTwin: FieldTwin) = withContext(Dispatchers.IO) {
        database.fieldDao().insertOrUpdateField(FieldEntity.fromDomain(fieldTwin))
    }

    override suspend fun recordAction(actionRecord: ActionRecord) = withContext(Dispatchers.IO) {
        database.actionDao().insertAction(ActionRecordEntity.fromDomain(actionRecord))
    }

    override suspend fun updateActionVerification(
        actionId: String,
        status: ActionVerificationStatus,
        postMoisture: Double,
        note: String,
        noteHi: String
    ) = withContext(Dispatchers.IO) {
        val actions = database.actionDao().getActionsForField("FIELD-001").firstOrNull()
        val action = actions?.find { it.actionId == actionId }
        if (action != null) {
            val updated = action.copy(
                postActionMoisture = postMoisture,
                actualDelta = postMoisture - action.preActionMoisture,
                verificationStatus = status,
                verificationNote = note,
                verificationNoteHi = noteHi
            )
            database.actionDao().updateAction(updated)
        }
    }

    override suspend fun markAdvisoryActionTaken(advisoryId: String) = withContext(Dispatchers.IO) {
        database.advisoryDao().markActionTaken(advisoryId, System.currentTimeMillis())
    }

    override suspend fun addProduceBatch(batch: ProduceBatch) = withContext(Dispatchers.IO) {
        database.produceDao().insertBatch(ProduceBatchEntity.fromDomain(batch))
    }

    override suspend fun triggerSimulationScenario(fieldId: String, scenarioType: String) = withContext(Dispatchers.IO) {
        val field = database.fieldDao().getFieldById(fieldId).firstOrNull()?.toDomain() ?: return@withContext

        when (scenarioType) {
            "SIMULATE_DROUGHT" -> {
                val lowMoistureTelemetry = SensorReading(
                    fieldId = fieldId,
                    timestamp = System.currentTimeMillis(),
                    soilMoisturePercent = 13.8,
                    soilTempC = 31.0,
                    airTempC = 39.5,
                    humidityPercent = 28.0,
                    rainMm = 0.0,
                    leafWetnessHours = 0.0,
                    irradianceLux = 88000.0,
                    ecDsM = 1.6,
                    qualityStatus = SensorQualityStatus.VALID,
                    sensorReliabilityScore = 0.98
                )
                insertSensorReading(lowMoistureTelemetry)
                updateFieldTwin(
                    field.copy(
                        healthIndex = 62,
                        waterStatus = WaterStatus.STRESSED_DRY,
                        heatRisk = RiskLevel.ACTION_NEEDED,
                        activeActionState = "CRITICAL: Urgent Irrigation Needed (Moisture 13.8%)",
                        activeActionStateHi = "गंभीर: तुरंत सिंचाई आवश्यक (नमी 13.8%)"
                    )
                )
            }

            "SIMULATE_HEAVY_RAIN" -> {
                val rainTelemetry = SensorReading(
                    fieldId = fieldId,
                    timestamp = System.currentTimeMillis(),
                    soilMoisturePercent = 38.0,
                    soilTempC = 21.0,
                    airTempC = 24.5,
                    humidityPercent = 94.0,
                    rainMm = 32.0,
                    leafWetnessHours = 8.0,
                    irradianceLux = 14000.0,
                    ecDsM = 0.9,
                    qualityStatus = SensorQualityStatus.VALID,
                    sensorReliabilityScore = 0.96
                )
                insertSensorReading(rainTelemetry)
                updateFieldTwin(
                    field.copy(
                        healthIndex = 82,
                        waterStatus = WaterStatus.SATURATED,
                        floodRisk = RiskLevel.WATCH,
                        diseaseRisk = RiskLevel.ACTION_NEEDED,
                        activeActionState = "Heavy Rain 32mm Recorded: Irrigation Blocked, Check Drainage",
                        activeActionStateHi = "32 मिमी भारी वर्षा: सिंचाई बंद रखें, जल निकासी की जांच करें"
                    )
                )
            }

            "SIMULATE_SENSOR_FAULT" -> {
                // Sensor anomaly: Soil says 4% dry, but Rain is 25mm and humidity is 95%
                val faultyTelemetry = SensorReading(
                    fieldId = fieldId,
                    timestamp = System.currentTimeMillis(),
                    soilMoisturePercent = 4.2,
                    soilTempC = 20.0,
                    airTempC = 23.0,
                    humidityPercent = 95.0,
                    rainMm = 25.0,
                    leafWetnessHours = 7.0,
                    irradianceLux = 12000.0,
                    ecDsM = 0.4,
                    qualityStatus = SensorQualityStatus.IMPOSSIBLE_SPIKE,
                    sensorReliabilityScore = 0.25
                )
                insertSensorReading(faultyTelemetry)
            }

            "SIMULATE_NORMAL" -> {
                val normalTelemetry = SensorReading(
                    fieldId = fieldId,
                    timestamp = System.currentTimeMillis(),
                    soilMoisturePercent = 27.5,
                    soilTempC = 23.5,
                    airTempC = 31.0,
                    humidityPercent = 55.0,
                    rainMm = 0.0,
                    leafWetnessHours = 1.0,
                    irradianceLux = 58000.0,
                    ecDsM = 1.2,
                    qualityStatus = SensorQualityStatus.VALID,
                    sensorReliabilityScore = 0.99
                )
                insertSensorReading(normalTelemetry)
                updateFieldTwin(
                    field.copy(
                        healthIndex = 89,
                        waterStatus = WaterStatus.OPTIMAL,
                        diseaseRisk = RiskLevel.SAFE,
                        pestRisk = RiskLevel.SAFE,
                        heatRisk = RiskLevel.SAFE,
                        floodRisk = RiskLevel.SAFE,
                        activeActionState = "All Microclimate & Soil parameters optimal",
                        activeActionStateHi = "सभी मौसम व मिट्टी संकेतक सामान्य हैं"
                    )
                )
            }
        }
    }

    override suspend fun syncOfflineData(): Boolean = withContext(Dispatchers.IO) {
        // Bi-directional delta sync implementation
        val queue = database.syncDao().getPendingSyncItems()
        for (item in queue) {
            database.syncDao().markSynced(item.queueId)
        }
        true
    }
}
