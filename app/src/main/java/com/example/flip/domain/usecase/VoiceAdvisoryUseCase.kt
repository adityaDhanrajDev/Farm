package com.example.flip.domain.usecase

import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.SensorReading

data class VoiceResponse(
    val spokenTextEn: String,
    val spokenTextHi: String,
    val relatedCategory: String,
    val actionSuggestion: String,
    val actionSuggestionHi: String
)

class VoiceAdvisoryUseCase {

    fun processFarmerVoiceQuery(
        query: String,
        field: FieldTwin,
        telemetry: SensorReading
    ): VoiceResponse {
        val q = query.lowercase()

        return when {
            q.contains("water") || q.contains("pani") || q.contains("paani") || q.contains("irrigate") || q.contains("sinchai") -> {
                if (telemetry.soilMoisturePercent < 20.0) {
                    VoiceResponse(
                        spokenTextEn = "Soil moisture in ${field.name} is down to ${telemetry.soilMoisturePercent.toInt()}%. We recommend running drip irrigation for 45 minutes today evening at 5:30 PM.",
                        spokenTextHi = "${field.name} में मिट्टी की नमी ${telemetry.soilMoisturePercent.toInt()}% हो गई है। आज शाम 5:30 बजे 45 मिनट के लिए ड्रिप सिंचाई चलाने की सलाह है।",
                        relatedCategory = "WATER",
                        actionSuggestion = "Start 45-min Irrigation",
                        actionSuggestionHi = "45 मिनट सिंचाई शुरू करें"
                    )
                } else {
                    VoiceResponse(
                        spokenTextEn = "Soil moisture is healthy at ${telemetry.soilMoisturePercent.toInt()}%. No watering is required today. Rain is possible tomorrow.",
                        spokenTextHi = "मिट्टी में नमी ${telemetry.soilMoisturePercent.toInt()}% के साथ पर्याप्त है। आज पानी देने की आवश्यकता नहीं है। कल बारिश की संभावना है।",
                        relatedCategory = "WATER",
                        actionSuggestion = "Check Telemetry",
                        actionSuggestionHi = "सेंसर विवरण देखें"
                    )
                }
            }

            q.contains("disease") || q.contains("bimari") || q.contains("keeda") || q.contains("pest") || q.contains("fungus") || q.contains("khatra") -> {
                VoiceResponse(
                    spokenTextEn = "Fungal blight risk is currently ${field.diseaseRisk.labelEn}. Due to ${telemetry.humidityPercent.toInt()}% humidity, avoid overhead spraying and inspect lower leaves.",
                    spokenTextHi = "फफूंद रोग का जोखिम वर्तमान में ${field.diseaseRisk.labelHi} है। ${telemetry.humidityPercent.toInt()}% आर्द्रता के कारण फव्वारा सिंचाई न करें और निचली पत्तियों की जांच करें।",
                    relatedCategory = "DIAGNOSTIC",
                    actionSuggestion = "Scan Crop Leaf with Camera",
                    actionSuggestionHi = "कैमरा से पत्ती स्कैन करें"
                )
            }

            q.contains("harvest") || q.contains("katai") || q.contains("ready") || q.contains("mandi") || q.contains("price") || q.contains("bhav") -> {
                VoiceResponse(
                    spokenTextEn = "Your ${field.cropName} is in ${field.cropStage.labelEn}. Estimated harvest is in ${field.daysToHarvest} days. Current mandi price is trending at ₹2,850 per quintal.",
                    spokenTextHi = "आपकी ${field.cropName} की फसल ${field.cropStage.labelHi} में है। लगभग ${field.daysToHarvest} दिनों में कटाई का सही समय होगा। वर्तमान मंडी भाव ₹2,850 प्रति क्विंटल है।",
                    relatedCategory = "MARKET",
                    actionSuggestion = "View Market Intelligence",
                    actionSuggestionHi = "मंडी व बिक्री विश्लेषण देखें"
                )
            }

            else -> {
                VoiceResponse(
                    spokenTextEn = "${field.name} (${field.cropName}) overall health is ${field.healthIndex} out of 100. Temperature is ${telemetry.airTempC.toInt()}°C. All systems operating normally.",
                    spokenTextHi = "${field.name} (${field.cropName}) का समग्र स्वास्थ्य सूचकांक 100 में से ${field.healthIndex} है। तापमान ${telemetry.airTempC.toInt()}°C है। स्थिति सामान्य है।",
                    relatedCategory = "GENERAL",
                    actionSuggestion = "Explore Field Twin",
                    actionSuggestionHi = "डिजिटल ट्विन देखें"
                )
            }
        }
    }
}
