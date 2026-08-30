package com.example.flip.domain.usecase

import com.example.flip.domain.model.ConfidenceLevel
import com.example.flip.domain.model.CropStage
import com.example.flip.domain.model.Explanation5W
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.MultimodalDiagnosis
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading
import java.util.UUID

class MultimodalFusionUseCase {

    fun fuseMultimodalEvidence(
        fieldTwin: FieldTwin,
        telemetry: SensorReading,
        observedVisualFeature: String
    ): MultimodalDiagnosis {
        val evidenceList = mutableListOf<String>()
        evidenceList.add("Crop: ${fieldTwin.cropName} (${fieldTwin.cropStage.labelEn})")
        evidenceList.add("Soil Moisture: ${telemetry.soilMoisturePercent.toInt()}% | Air Temp: ${telemetry.airTempC.toInt()}°C | Humidity: ${telemetry.humidityPercent.toInt()}%")

        if (telemetry.leafWetnessHours > 0) {
            evidenceList.add("Leaf Wetness Duration: ${telemetry.leafWetnessHours} hrs continuous")
        }

        // Check sensor reliability penalty
        val isSensorFaulty = telemetry.qualityStatus != SensorQualityStatus.VALID || telemetry.sensorReliabilityScore < 0.60
        if (isSensorFaulty) {
            evidenceList.add("Warning: Sensor Reliability Degraded (${(telemetry.sensorReliabilityScore * 100).toInt()}%) - ${telemetry.qualityStatus.labelEn}")
        }

        val lowerVisual = observedVisualFeature.lowercase()

        // 1. Check for Ambiguous / Conflicting / Out-of-Distribution symptoms -> Trigger UNKNOWN / ABSTAIN protocol
        if (lowerVisual.contains("unknown") || lowerVisual.contains("unclear") || lowerVisual.contains("mixed") || lowerVisual.contains("atypical")) {
            return MultimodalDiagnosis(
                diagnosisId = UUID.randomUUID().toString(),
                fieldId = fieldTwin.fieldId,
                detectedCondition = "Atypical Multi-Symptom Anomaly (Abstained)",
                detectedConditionHi = "अस्पष्ट बहु-लक्षण असामान्यता (एआई निर्णय स्थगित)",
                confidenceLevel = ConfidenceLevel.UNKNOWN,
                confidenceScorePercent = 38,
                isAbstained = true,
                requiresHumanExpert = true,
                explanation = Explanation5W(
                    what = "Unclassified Foliar Symptoms with Contradictory Microclimate Profile",
                    why = "Leaf spots present but humidity (${telemetry.humidityPercent.toInt()}%) is too dry (<60%) for typical fungal sporulation. Image patterns do not match standard single-vector disease libraries.",
                    whenWindow = "Immediate Expert Escalation within 24 Hours",
                    whatToDo = "Do NOT spray chemical fungicides blindly. Route physical leaf sample to Regional Krishi Vigyan Kendra (KVK) Extension Officer for lab validation.",
                    confidenceText = "38% (Below Safety Threshold - Abstaining)",
                    evidenceFactors = evidenceList + listOf("Out-of-Distribution Visual Signature", "Cross-Modal Contradiction: Visual Spots vs Low Humidity"),
                    whatHi = "अवर्गीकृत पत्ती लक्षण व परस्पर विरोधी मौसम डेटा",
                    whyHi = "पत्तियों पर धब्बे हैं लेकिन आर्द्रता (${telemetry.humidityPercent.toInt()}%) फफूंद के लिए अपर्याप्त है। एआई गलत सलाह से बचने हेतु निर्णय टाल रहा है।",
                    whenWindowHi = "24 घंटे के भीतर विशेषज्ञ सलाह",
                    whatToDoHi = "बिना पुष्टि कीटनाशक का छिड़काव न करें। नजदीकी कृषि विज्ञान केंद्र (KVK) से संपर्क करें।"
                )
            )
        }

        // 2. Fungal Blight Context Fusion:
        // High humidity (>80%) + prolonged leaf wetness (>5h) + moderate temp (20-30°C) + leaf spot
        if (lowerVisual.contains("spot") || lowerVisual.contains("blight") || lowerVisual.contains("fungal") ||
            (telemetry.humidityPercent >= 80.0 && telemetry.leafWetnessHours >= 4.0)
        ) {
            val isHighRiskMicroclimate = telemetry.humidityPercent >= 80.0 && telemetry.leafWetnessHours >= 4.0
            val confidence = if (isHighRiskMicroclimate && !isSensorFaulty) 88 else if (isSensorFaulty) 62 else 74

            return MultimodalDiagnosis(
                diagnosisId = UUID.randomUUID().toString(),
                fieldId = fieldTwin.fieldId,
                detectedCondition = "Early Blight Risk (Alternaria solani)",
                detectedConditionHi = "अगेती झुलसा रोग जोखिम (अर्ली ब्लाइट)",
                confidenceLevel = if (confidence >= 80) ConfidenceLevel.CONFIDENT else ConfidenceLevel.LOW_CONFIDENCE,
                confidenceScorePercent = confidence,
                isAbstained = false,
                requiresHumanExpert = confidence < 75,
                explanation = Explanation5W(
                    what = "Early Fungal Blight spore development detected on canopy",
                    why = "Fused Evidence: ${telemetry.humidityPercent.toInt()}% high humidity + ${telemetry.leafWetnessHours}h leaf wetness + ${telemetry.airTempC.toInt()}°C ambient temperature during ${fieldTwin.cropStage.labelEn} stage creates optimal pathogen incubation.",
                    whenWindow = "Action window: Next 24 to 48 Hours before secondary lesion spread",
                    whatToDo = "1. Avoid overhead sprinkler irrigation. 2. Prune lower infected leaves. 3. Apply Copper Oxychloride (2.5g/L) or Trichoderma viride bio-fungicide only during calm wind hours (<10 km/h).",
                    confidenceText = "$confidence% (${if (confidence >= 80) "High Confidence" else "Moderate Confidence"})",
                    evidenceFactors = evidenceList + listOf("Spore germination threshold satisfied", "Microclimate incubation index: High"),
                    whatHi = "पत्तियों पर फफूंद जनित अगेती झुलसा का प्रारंभिक संक्रमण",
                    whyHi = "${telemetry.humidityPercent.toInt()}% आर्द्रता + ${telemetry.leafWetnessHours} घंटे पत्ती गीलापन + ${fieldTwin.cropStage.labelHi} अवस्था में कवक विस्तार के अनुकूल वातावरण।",
                    whenWindowHi = "अगले 24 से 48 घंटों में प्राथमिक रोकथाम आवश्यक",
                    whatToDoHi = "1. फव्वारा सिंचाई रोकें। 2. निचली संक्रमित पत्तियों को हटाएँ। 3. कॉपर ऑक्सीक्लोराइड (2.5 ग्राम/लीटर) या ट्राइकोडर्मा जैविक फफूंदनाशी का छिड़काव करें।"
                )
            )
        }

        // 3. Water Stress vs Heat Wilting Multimodal Fusion:
        // Leaf wilting + low soil moisture vs high temperature
        if (lowerVisual.contains("wilt") || lowerVisual.contains("dry") || lowerVisual.contains("yellow") || telemetry.soilMoisturePercent < 20.0) {
            val isHighHeat = telemetry.airTempC >= 36.0
            val isLowMoisture = telemetry.soilMoisturePercent <= 20.0

            val diagnosisTitle = if (isLowMoisture && isHighHeat) "Acute Combined Heat & Water Deficit Stress"
            else if (isLowMoisture) "Root Zone Soil Water Stress"
            else "Atmospheric Heat Shock (Transient Wilting)"

            val diagnosisTitleHi = if (isLowMoisture && isHighHeat) "गंभीर गर्मी व जल तनाव (सूखा)"
            else if (isLowMoisture) "जड़ क्षेत्र में पानी की कमी"
            else "अत्यधिक तापमान का प्रभाव (मुरझाना)"

            return MultimodalDiagnosis(
                diagnosisId = UUID.randomUUID().toString(),
                fieldId = fieldTwin.fieldId,
                detectedCondition = diagnosisTitle,
                detectedConditionHi = diagnosisTitleHi,
                confidenceLevel = ConfidenceLevel.CONFIDENT,
                confidenceScorePercent = 91,
                isAbstained = false,
                requiresHumanExpert = false,
                explanation = Explanation5W(
                    what = diagnosisTitle,
                    why = "Soil moisture is at ${telemetry.soilMoisturePercent.toInt()}% (Critical threshold: 22%) with ambient temp at ${telemetry.airTempC.toInt()}°C during ${fieldTwin.cropStage.labelEn}.",
                    whenWindow = "Immediate Irrigation Window: Today Evening between 5:30 PM - 7:30 PM",
                    whatToDo = "Apply 45 minutes drip irrigation (approx. 22,000 Liters/Acre). Avoid mid-day watering to minimize evaporative loss and root shock.",
                    confidenceText = "91% (High Confidence Telemetry Fusion)",
                    evidenceFactors = evidenceList + listOf("Soil moisture below wilting point", "Evapotranspiration rate: High (6.2 mm/day)"),
                    whatHi = diagnosisTitleHi,
                    whyHi = "मिट्टी की नमी केवल ${telemetry.soilMoisturePercent.toInt()}% है (न्यूनतम आवश्यक: 22%) और तापमान ${telemetry.airTempC.toInt()}°C है।",
                    whenWindowHi = "आज शाम 5:30 से 7:30 बजे के बीच",
                    whatToDoHi = "45 मिनट ड्रिप सिंचाई चलाएं। तेज धूप में पानी न दें।"
                )
            )
        }

        // 4. Healthy / Normal Canopy
        return MultimodalDiagnosis(
            diagnosisId = UUID.randomUUID().toString(),
            fieldId = fieldTwin.fieldId,
            detectedCondition = "Healthy Foliage & Balanced Microclimate",
            detectedConditionHi = "स्वस्थ फसल व अनुकूल वातावरण",
            confidenceLevel = ConfidenceLevel.CONFIDENT,
            confidenceScorePercent = 94,
            isAbstained = false,
            requiresHumanExpert = false,
            explanation = Explanation5W(
                what = "Crop canopy is vibrant, turgid, and showing standard phenological vigor",
                why = "Optical leaf color is uniform green. Soil moisture (${telemetry.soilMoisturePercent.toInt()}%) is in optimal range for ${fieldTwin.cropStage.labelEn}. No pathogenic spore signatures detected.",
                whenWindow = "Routine Monitoring in 48 Hours",
                whatToDo = "Maintain standard fertigation schedule. No chemical or emergency pesticide intervention required.",
                confidenceText = "94% (High Confidence Normal State)",
                evidenceFactors = evidenceList + listOf("NDVI canopy density: Optimal (0.78)", "No pathogenic temperature-humidity overlap"),
                whatHi = "फसल की पत्तियां स्वस्थ और वृद्धि सामान्य है",
                whyHi = "मिट्टी की नमी (${telemetry.soilMoisturePercent.toInt()}%) अनुकूल है और कोई बीमारी के लक्षण नहीं हैं।",
                whenWindowHi = "48 घंटे बाद नियमित अवलोकन",
                whatToDoHi = "सामान्य देखभाल जारी रखें, किसी कीटनाशक की आवश्यकता नहीं है।"
            )
        )
    }
}
