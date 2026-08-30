package com.example.flip.domain.usecase

import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading

class ClosedLoopVerificationUseCase {

    fun verifyIrrigationAction(
        action: ActionRecord,
        postTelemetry: SensorReading
    ): ActionRecord {
        // Check if sensor reading is corrupted or faulty
        if (postTelemetry.qualityStatus != SensorQualityStatus.VALID || postTelemetry.sensorReliabilityScore < 0.5) {
            return action.copy(
                postActionMoisture = postTelemetry.soilMoisturePercent,
                actualDelta = 0.0,
                verificationStatus = ActionVerificationStatus.SENSOR_FAULT,
                verificationNote = "Verification suspended: Sensor fault detected (${postTelemetry.qualityStatus.labelEn}). Hardware inspect required.",
                verificationNoteHi = "सत्यापन रुका हुआ: सेंसर में खराबी आई है। भौतिक जांच करें।"
            )
        }

        val actualDelta = postTelemetry.soilMoisturePercent - action.preActionMoisture
        val status: ActionVerificationStatus
        val noteEn: String
        val noteHi: String

        if (actualDelta >= (action.expectedDelta * 0.70)) {
            // Success: expected soil moisture bump achieved
            status = ActionVerificationStatus.VERIFIED_SUCCESS
            noteEn = "Closed-loop check: Soil moisture increased from ${action.preActionMoisture.toInt()}% to ${postTelemetry.soilMoisturePercent.toInt()}% (+${actualDelta.toInt()}% delta). Irrigation cycle successful."
            noteHi = "बंद-लूप सत्यापन: नमी ${action.preActionMoisture.toInt()}% से बढ़कर ${postTelemetry.soilMoisturePercent.toInt()}% हो गई। सिंचाई सफल रही।"
        } else if (actualDelta > 2.0) {
            // Partial: low flow or partial blockage
            status = ActionVerificationStatus.VERIFIED_PARTIAL
            noteEn = "Partial response: Soil moisture rose by only +${actualDelta.toInt()}% (expected +${action.expectedDelta.toInt()}%). Check for drip emitter clogging or low water pressure."
            noteHi = "आंशिक प्रभाव: नमी में केवल +${actualDelta.toInt()}% की वृद्धि हुई। ड्रिप पाइप में रुकावट या कम दबाव की जांच करें।"
        } else {
            // Failure: pump didn't run or pipe ruptured
            status = ActionVerificationStatus.VERIFIED_FAILED
            noteEn = "Zero moisture change detected (+${actualDelta.toInt()}%). Possible motor trip, valve failure, or severed supply line. Alarm dispatched."
            noteHi = "नमी में कोई बदलाव नहीं हुआ (+${actualDelta.toInt()}%)। मोटर ट्रिप, वाल्व बंद या पाइप फटने की संभावना है।"
        }

        return action.copy(
            postActionMoisture = postTelemetry.soilMoisturePercent,
            actualDelta = actualDelta,
            verificationStatus = status,
            verificationNote = noteEn,
            verificationNoteHi = noteHi
        )
    }
}
