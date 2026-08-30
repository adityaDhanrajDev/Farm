package com.example.flip.domain.usecase

import com.example.flip.domain.model.CropStage
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SensorReading

data class IrrigationDecision(
    val actionCommand: String,
    val actionCommandHi: String,
    val severity: RiskLevel,
    val recommendedDurationMinutes: Int,
    val recommendedLitersPerAcre: Int,
    val reasoning: String,
    val reasoningHi: String,
    val optimalTimeWindow: String,
    val optimalTimeWindowHi: String,
    val rainForecastWarning: String? = null
)

class SmartIrrigationUseCase {

    fun computeIrrigationSchedule(
        fieldTwin: FieldTwin,
        telemetry: SensorReading,
        forecastRainNext48hMm: Double
    ): IrrigationDecision {
        val moisture = telemetry.soilMoisturePercent
        val isFloweringOrFruiting = fieldTwin.cropStage == CropStage.FLOWERING || fieldTwin.cropStage == CropStage.FRUITING

        // 1. Check Rain Forecast: Heavy rain incoming (>15 mm) -> DELAY / AVOID IRRIGATION
        if (forecastRainNext48hMm >= 15.0) {
            return IrrigationDecision(
                actionCommand = "DO NOT IRRIGATE (Heavy Rain Expected)",
                actionCommandHi = "सिंचाई न करें (वर्षा की संभावना)",
                severity = RiskLevel.SAFE,
                recommendedDurationMinutes = 0,
                recommendedLitersPerAcre = 0,
                reasoning = "Upcoming precipitation forecast indicates ${forecastRainNext48hMm.toInt()} mm rainfall within 24–48 hours. Irrigating now will cause root asphyxiation, nutrient leaching, and energy waste.",
                reasoningHi = "अगले 24-48 घंटों में ${forecastRainNext48hMm.toInt()} मिमी वर्षा की संभावना है। अभी सिंचाई करने से जलभराव और खाद का नुकसान होगा।",
                optimalTimeWindow = "Hold until post-rain soil assessment (in 48 hours)",
                optimalTimeWindowHi = "बारिश के 48 घंटे बाद पुनः जांच करें",
                rainForecastWarning = "48h Rain Forecast: ${forecastRainNext48hMm.toInt()} mm (Probability: 85%)"
            )
        }

        // 2. Critical Dry Water Stress (< 18% moisture)
        if (moisture < 18.0) {
            val duration = if (isFloweringOrFruiting) 55 else 40
            val liters = duration * 450

            return IrrigationDecision(
                actionCommand = "IRRIGATE NOW (Urgent Water Deficit)",
                actionCommandHi = "तुरंत सिंचाई करें (गंभीर कमी)",
                severity = RiskLevel.ACTION_NEEDED,
                recommendedDurationMinutes = duration,
                recommendedLitersPerAcre = liters,
                reasoning = "Soil moisture is critically low at ${moisture.toInt()}% (below threshold 20%). During ${fieldTwin.cropStage.labelEn}, water deficit induces flower abortion and reduces yield potential by up to 22%.",
                reasoningHi = "मिट्टी में नमी घटकर ${moisture.toInt()}% रह गई है। ${fieldTwin.cropStage.labelHi} अवस्था में पानी की कमी से फूल झड़ने और पैदावार में 22% तक गिरावट का खतरा है।",
                optimalTimeWindow = "Today Evening (5:30 PM - 7:30 PM) or Early Morning (5:00 AM)",
                optimalTimeWindowHi = "आज शाम 5:30 से 7:30 बजे या सुबह 5:00 बजे",
                rainForecastWarning = null
            )
        }

        // 3. Moderate Deficit (18% - 24% moisture)
        if (moisture in 18.0..24.0) {
            if (forecastRainNext48hMm in 5.0..14.0) {
                return IrrigationDecision(
                    actionCommand = "DELAY IRRIGATION (Light Rain Expected)",
                    actionCommandHi = "सिंचाई स्थगित करें (हल्की वर्षा की संभावना)",
                    severity = RiskLevel.WATCH,
                    recommendedDurationMinutes = 0,
                    recommendedLitersPerAcre = 0,
                    reasoning = "Soil moisture is moderate (${moisture.toInt()}%) and light rain (${forecastRainNext48hMm.toInt()} mm) is forecast. Delay watering for 24 hours to conserve water.",
                    reasoningHi = "मिट्टी में सामान्य नमी (${moisture.toInt()}%) है और हल्की बारिश (${forecastRainNext48hMm.toInt()} मिमी) का अनुमान है। 24 घंटे इंतजार करें।",
                    optimalTimeWindow = "Re-evaluate tomorrow morning",
                    optimalTimeWindowHi = "कल सुबह पुनः समीक्षा करें",
                    rainForecastWarning = "48h Rain Forecast: ${forecastRainNext48hMm.toInt()} mm"
                )
            } else {
                val duration = 30
                return IrrigationDecision(
                    actionCommand = "LIGHT IRRIGATION RECOMMENDED",
                    actionCommandHi = "हल्की सिंचाई की सलाह",
                    severity = RiskLevel.WATCH,
                    recommendedDurationMinutes = duration,
                    recommendedLitersPerAcre = duration * 400,
                    reasoning = "Soil moisture (${moisture.toInt()}%) approaching lower comfort band for ${fieldTwin.cropStage.labelEn}. A short cycle will restore root zone buffer.",
                    reasoningHi = "मिट्टी की नमी ${moisture.toInt()}% है। हल्की ड्रिप सिंचाई से जड़ क्षेत्र में पर्याप्त नमी बनी रहेगी।",
                    optimalTimeWindow = "Evening or Early Morning",
                    optimalTimeWindowHi = "शाम अथवा प्रातःकाल",
                    rainForecastWarning = null
                )
            }
        }

        // 4. Moisture Optimal / Saturated (>24%)
        return IrrigationDecision(
            actionCommand = "SOIL MOISTURE OPTIMAL (No Action Needed)",
            actionCommandHi = "नमी पर्याप्त है (सिंचाई की आवश्यकता नहीं)",
            severity = RiskLevel.SAFE,
            recommendedDurationMinutes = 0,
            recommendedLitersPerAcre = 0,
            reasoning = "Soil moisture (${moisture.toInt()}%) is in the optimal agronomic band (25%–32%) for ${fieldTwin.soilType.labelEn} soil. Evaporative demand is currently manageable.",
            reasoningHi = "मिट्टी में नमी (${moisture.toInt()}%) बिल्कुल सही है। अभी पानी देने की आवश्यकता नहीं है।",
            optimalTimeWindow = "Next Check in 36 Hours",
            optimalTimeWindowHi = "अगली जांच 36 घंटे बाद",
            rainForecastWarning = null
        )
    }
}
