package com.example.flip.domain.usecase

import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading

class SensorAnomalyDetectionUseCase {

    fun evaluateTelemetry(current: SensorReading, previous: SensorReading?): SensorReading {
        var status = SensorQualityStatus.VALID
        var reliability = 0.98

        // 1. Physical impossible-value check
        if (current.soilMoisturePercent < 0.0 || current.soilMoisturePercent > 100.0 ||
            current.humidityPercent < 0.0 || current.humidityPercent > 100.0 ||
            current.airTempC < -10.0 || current.airTempC > 65.0
        ) {
            return current.copy(
                qualityStatus = SensorQualityStatus.IMPOSSIBLE_SPIKE,
                sensorReliabilityScore = 0.20
            )
        }

        // 2. Cross-modal anomaly check:
        // If Rain is heavy (>15mm) and Humidity > 85%, but Soil Moisture reports critical drought (<12%)
        if (current.rainMm > 15.0 && current.humidityPercent > 80.0 && current.soilMoisturePercent < 15.0) {
            status = SensorQualityStatus.IMPOSSIBLE_SPIKE
            reliability = 0.35
            return current.copy(qualityStatus = status, sensorReliabilityScore = reliability)
        }

        // 3. Sensor Drift / Sudden Spikes without environmental cause
        if (previous != null) {
            val moistureDelta = Math.abs(current.soilMoisturePercent - previous.soilMoisturePercent)
            val timeDeltaHours = (current.timestamp - previous.timestamp) / (1000.0 * 60 * 60)

            // Sudden moisture drop > 25% in 1 hour without drainage event
            if (moistureDelta > 25.0 && timeDeltaHours <= 1.0 && current.rainMm == 0.0) {
                status = SensorQualityStatus.SUSPECT_DRIFT
                reliability = 0.45
            }

            // Stale check
            if (timeDeltaHours > 3.0) {
                status = SensorQualityStatus.STALE
                reliability = 0.60
            }
        }

        return current.copy(qualityStatus = status, sensorReliabilityScore = reliability)
    }
}
