package com.example.flip.domain.model

enum class CropStage(val labelEn: String, val labelHi: String) {
    SOWING("Sowing / Emergence", "बुवाई / अंकुरण"),
    VEGETATIVE("Vegetative Growth", "वानस्पतिक वृद्धि"),
    FLOWERING("Flowering", "फूल आना"),
    FRUITING("Fruiting / Pod Development", "फल / फली विकास"),
    MATURITY("Maturity / Harvest Ready", "परिपक्वता / कटाई तैयार")
}

enum class SoilType(val labelEn: String, val labelHi: String) {
    CLAY_LOAM("Clay Loam", "दोमट मिट्टी"),
    SANDY_LOAM("Sandy Loam", "बलुई दोमट"),
    BLACK_COTTON("Black Cotton", "काली मिट्टी"),
    ALLUVIAL("Alluvial", "जलोढ़ मिट्टी")
}

enum class WaterStatus(val labelEn: String, val labelHi: String, val severity: RiskLevel) {
    OPTIMAL("Optimal Moisture", "उचित नमी", RiskLevel.SAFE),
    SLIGHT_DEFICIT("Slight Deficit", "हल्की कमी", RiskLevel.WATCH),
    STRESSED_DRY("Critical Water Stress", "गंभीर सूखा तनाव", RiskLevel.ACTION_NEEDED),
    SATURATED("Waterlogged / Excess", "जलभराव / अधिक पानी", RiskLevel.ACTION_NEEDED)
}

enum class RiskLevel(val labelEn: String, val labelHi: String) {
    SAFE("SAFE", "सुरक्षित"),
    WATCH("WATCH", "सावधानी"),
    ACTION_NEEDED("ACTION NEEDED", "कार्रवाई आवश्यक")
}

enum class ConfidenceLevel(val labelEn: String, val labelHi: String) {
    CONFIDENT("High Confidence", "उच्च विश्वास (>80%)"),
    LOW_CONFIDENCE("Low Confidence", "कम विश्वास (50-80%)"),
    UNKNOWN("Unknown / Abstain", "अज्ञात / अनिश्चित"),
    EXPERT_REVIEW_REQUIRED("Expert Review Required", "विशेषज्ञ समीक्षा आवश्यक")
}

enum class SensorQualityStatus(val labelEn: String, val labelHi: String) {
    VALID("Normal & Calibrated", "सामान्य व सही"),
    SUSPECT_DRIFT("Sensor Drift Detected", "सेंसर विचलन"),
    IMPOSSIBLE_SPIKE("Impossible Spike / Conflict", "असामान्य रीडिंग / विसंगति"),
    STALE("Stale Telemetry (>3h)", "पुराना डेटा")
}

enum class AdvisoryCategory(val labelEn: String, val labelHi: String) {
    IRRIGATION("Smart Irrigation", "स्मार्ट सिंचाई"),
    DISEASE_PREVENTION("Disease Control", "रोग नियंत्रण"),
    PEST_CONTROL("Pest Management", "कीट प्रबंधन"),
    FERTILIZER_NPK("Nutrient / NPK", "पोषक तत्व / खाद"),
    HARVEST_READINESS("Harvest Window", "कटाई समय"),
    STORAGE_SAFETY("Storage & Spoilage", "भंडारण व सुरक्षा")
}

enum class ActionVerificationStatus(val labelEn: String, val labelHi: String) {
    PENDING("Awaiting Action Execution", "कार्रवाई की प्रतीक्षा"),
    VERIFIED_SUCCESS("Verified Success", "सत्यापित सफल"),
    VERIFIED_PARTIAL("Partial Response", "आंशिक प्रभाव"),
    VERIFIED_FAILED("Action Ineffective / Pump Fault", "कार्रवाई अप्रभावी / मोटर खराबी"),
    SENSOR_FAULT("Verification Blocked (Sensor Fault)", "सेंसर खराबी के कारण बाधित")
}

enum class QualityGrade(val labelEn: String, val labelHi: String) {
    GRADE_A("Grade A (Export / Premium)", "ग्रेड A (उत्कृष्ट)"),
    GRADE_B("Grade B (Standard Mandi)", "ग्रेड B (सामान्य मंडी)"),
    GRADE_C("Grade C (Processing / Quick Sale)", "ग्रेड C (प्रसंस्करण)")
}

enum class SellDecision(val labelEn: String, val labelHi: String, val actionTone: RiskLevel) {
    SELL_NOW("Sell Immediately (Optimal Price)", "तुरंत बेचें (अच्छा भाव)", RiskLevel.SAFE),
    STORE_3_DAYS("Store 3–5 Days (Price Surge Expected)", "3-5 दिन रोकें (भाव बढ़ने का अनुमान)", RiskLevel.WATCH),
    STORE_7_DAYS("Store 7+ Days (High Margin Forecast)", "7+ दिन भंडारण करें", RiskLevel.WATCH),
    FIND_IMMEDIATE_BUYER("Find Fast Bulk Buyer (Spoilage Risk)", "शीघ्र खरीदार खोजें (खराब होने का खतरा)", RiskLevel.ACTION_NEEDED)
}

/**
 * 5-W Explainability Framework
 */
data class Explanation5W(
    val what: String,
    val why: String,
    val whenWindow: String,
    val whatToDo: String,
    val confidenceText: String,
    val evidenceFactors: List<String> = emptyList(),
    val whatHi: String = "",
    val whyHi: String = "",
    val whenWindowHi: String = "",
    val whatToDoHi: String = ""
)

/**
 * Digital Twin state of a specific field plot
 */
data class FieldTwin(
    val fieldId: String,
    val farmId: String,
    val name: String,
    val locationName: String,
    val cropName: String,
    val cropVariety: String,
    val cropStage: CropStage,
    val areaAcres: Double,
    val soilType: SoilType,
    val healthIndex: Int, // 0 to 100
    val waterStatus: WaterStatus,
    val diseaseRisk: RiskLevel,
    val pestRisk: RiskLevel,
    val heatRisk: RiskLevel,
    val floodRisk: RiskLevel,
    val projectedYieldTons: Double,
    val daysToHarvest: Int,
    val activeActionState: String,
    val activeActionStateHi: String,
    val lastTelemetryTimestamp: Long = System.currentTimeMillis()
)

/**
 * Real-time IoT sensor telemetry frame
 */
data class SensorReading(
    val id: Long = 0,
    val fieldId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val soilMoisturePercent: Double,
    val soilTempC: Double,
    val airTempC: Double,
    val humidityPercent: Double,
    val rainMm: Double,
    val leafWetnessHours: Double,
    val irradianceLux: Double,
    val ecDsM: Double = 1.2,
    val qualityStatus: SensorQualityStatus = SensorQualityStatus.VALID,
    val sensorReliabilityScore: Double = 0.95 // 0.0 to 1.0
)

/**
 * Multimodal AI Diagnostic output
 */
data class MultimodalDiagnosis(
    val diagnosisId: String,
    val fieldId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val detectedCondition: String,
    val detectedConditionHi: String,
    val confidenceLevel: ConfidenceLevel,
    val confidenceScorePercent: Int,
    val isAbstained: Boolean = false,
    val requiresHumanExpert: Boolean = false,
    val explanation: Explanation5W,
    val chemicalInterventionRequiresConfirmation: Boolean = true,
    val safetyWindSpeedMaxKmh: Double = 12.0
)

/**
 * 24h - 72h Pre-Symptom Prediction Forecast
 */
data class PredictionForecast(
    val forecastWindowHours: Int, // 24, 48, 72
    val diseaseSporeGerminationRiskPercent: Int,
    val pestPopulationGrowthPercent: Int,
    val waterStressRiskPercent: Int,
    val heatWiltingRiskPercent: Int,
    val primaryThreat: String,
    val primaryThreatHi: String,
    val reasoning: String,
    val reasoningHi: String
)

/**
 * Actionable Agronomic Advisory
 */
data class AdvisoryItem(
    val advisoryId: String,
    val fieldId: String,
    val category: AdvisoryCategory,
    val severity: RiskLevel,
    val title: String,
    val titleHi: String,
    val actionSummary: String,
    val actionSummaryHi: String,
    val explanation: Explanation5W,
    val timestamp: Long = System.currentTimeMillis(),
    val isActionTaken: Boolean = false,
    val actionTakenTimestamp: Long? = null
)

/**
 * Closed-Loop Action Verification Record
 */
data class ActionRecord(
    val actionId: String,
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
)

/**
 * Post-Harvest Produce Batch & Market Economics
 */
data class ProduceBatch(
    val batchId: String,
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
)

/**
 * Macro Regional / Village Outbreak Hotspot GIS Layer
 */
data class HotspotZone(
    val zoneId: String,
    val villageName: String,
    val district: String,
    val dominantCrop: String,
    val activeThreat: String,
    val activeThreatHi: String,
    val threatLevel: RiskLevel,
    val spreadVelocityKmPerDay: Double,
    val affectedFarmsCount: Int,
    val distanceKmFromUser: Double
)
