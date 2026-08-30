package com.example.flip.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading
import com.example.flip.domain.usecase.IrrigationDecision
import com.example.flip.presentation.components.RiskStatusBadge
import com.example.ui.theme.*

@Composable
fun SmartWaterScreen(
    selectedField: FieldTwin?,
    latestTelemetry: SensorReading?,
    telemetryHistory: List<SensorReading>,
    decision: IrrigationDecision?,
    actionHistory: List<ActionRecord>,
    isExecuting: Boolean,
    isHindi: Boolean,
    onExecuteIrrigation: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Top Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WaterBlueContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = WaterBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "स्मार्ट सिंचाई व बंद-लूप नियंत्रण" else "Adaptive Smart Irrigation & Closed-Loop",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF01579B)
                        )
                        Text(
                            text = if (isHindi) "मिट्टी नमी + 48h वर्षा पूर्वानुमान + फसल अवस्था पर आधारित निर्णय" else "Soil Probes + 48h Rain Forecast + Phenological ETc",
                            fontSize = 12.sp,
                            color = Color(0xFF0277BD)
                        )
                    }
                }
            }
        }

        // Live Soil Water Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "मिट्टी की वर्तमान नमी (Root Zone)" else "Current Root-Zone Moisture",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        val moisture = latestTelemetry?.soilMoisturePercent ?: 25.0
                        Text(
                            text = "${moisture.toInt()}%",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (moisture >= 24) StatusSafeGreen else if (moisture >= 18) StatusWatchAmber else StatusActionRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val moistureProgress = ((latestTelemetry?.soilMoisturePercent ?: 25.0) / 40.0).toFloat().coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { moistureProgress },
                        color = WaterBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "0% (Dry)", fontSize = 10.sp, color = TextSecondary)
                        Text(text = "20% (Wilting Point)", fontSize = 10.sp, color = StatusWatchAmber)
                        Text(text = "28% (Field Capacity)", fontSize = 10.sp, color = StatusSafeGreen)
                        Text(text = "40% (Saturation)", fontSize = 10.sp, color = WaterBlue)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sensor Health Check Status
                    val sensorStatus = latestTelemetry?.qualityStatus ?: SensorQualityStatus.VALID
                    val isFaulty = sensorStatus != SensorQualityStatus.VALID
                    Surface(
                        color = if (!isFaulty) StatusSafeContainer else StatusActionContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (!isFaulty) Icons.Default.Sensors else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (!isFaulty) StatusSafeGreen else StatusActionRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (!isFaulty) {
                                        if (isHindi) "सेंसर स्वास्थ्य: सामान्य व कैलिब्रेटेड (98% विश्वसनीयता)" else "Sensor Reliability: Normal (98% Trust Score)"
                                    } else {
                                        if (isHindi) "⚠️ सेंसर विसंगति: ${sensorStatus.labelHi}" else "⚠️ Sensor Anomaly: ${sensorStatus.labelEn}"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isFaulty) StatusSafeGreen else StatusActionRed
                                )
                                Text(
                                    text = if (!isFaulty) {
                                        if (isHindi) "क्रॉस-सेंसर सत्यापन सफल (मौसम व पत्ती स्थिति से मेल खाता है)" else "Cross-modal checks verified against ambient weather"
                                    } else {
                                        if (isHindi) "सेंसर डेटा में गड़बड़ी है। निर्णय विश्वास कम किया गया।" else "Impossible spike / drift detected. Confidence degraded."
                                    },
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Smart Decision Engine Card
        if (decision != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RiskStatusBadge(level = decision.severity, isHindi = isHindi)
                            Text(
                                text = if (isHindi) "एआई निर्णय इंजन" else "AI Decision Engine",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isHindi) decision.actionCommandHi else decision.actionCommand,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isHindi) decision.reasoningHi else decision.reasoning,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (decision.recommendedDurationMinutes > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = if (isHindi) "अनुशंसित समय" else "Run Duration", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "${decision.recommendedDurationMinutes} Min", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                                }
                                Column {
                                    Text(text = if (isHindi) "जल मात्रा" else "Water Volume", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = "${decision.recommendedLitersPerAcre} L/Acre", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WaterBlue)
                                }
                                Column {
                                    Text(text = if (isHindi) "सही समय" else "Best Window", fontSize = 11.sp, color = TextSecondary)
                                    Text(text = if (isHindi) "शाम 5:30 बजे" else "Evening 5:30 PM", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusWatchAmber)
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Execution Button with Closed-Loop Simulation
                        Button(
                            onClick = { onExecuteIrrigation(if (decision.recommendedDurationMinutes > 0) decision.recommendedDurationMinutes else 30) },
                            enabled = !isExecuting,
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("execute_irrigation_button")
                        ) {
                            if (isExecuting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHindi) "सिंचाई चल रही है व सेंसर सत्यापन जारी..." else "Irrigating & Verifying Sensor Delta...")
                            } else {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isHindi) "सिंचाई शुरू करें व बंद-लूप सत्यापन जांचें" else "Execute Irrigation & Verify Loop")
                            }
                        }
                    }
                }
            }
        }

        // Closed-Loop Verification History
        item {
            Text(
                text = if (isHindi) "कार्रवाई व सत्यापन इतिहास (Closed-Loop Logs):" else "Closed-Loop Action Verification History:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(actionHistory) { record ->
            ActionHistoryCard(record = record, isHindi = isHindi)
        }
    }
}

@Composable
fun ActionHistoryCard(
    record: ActionRecord,
    isHindi: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.actionType,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                val (badgeColor, textColor, label) = when (record.verificationStatus) {
                    ActionVerificationStatus.VERIFIED_SUCCESS -> Triple(StatusSafeContainer, StatusSafeGreen, if (isHindi) "सत्यापित सफल" else "VERIFIED SUCCESS")
                    ActionVerificationStatus.VERIFIED_PARTIAL -> Triple(StatusWatchContainer, StatusWatchAmber, if (isHindi) "आंशिक प्रभाव" else "PARTIAL")
                    ActionVerificationStatus.VERIFIED_FAILED -> Triple(StatusActionContainer, StatusActionRed, if (isHindi) "विफल / मोटर खराबी" else "FAILED")
                    ActionVerificationStatus.SENSOR_FAULT -> Triple(StatusActionContainer, StatusActionRed, if (isHindi) "सेंसर त्रुटि" else "SENSOR FAULT")
                    ActionVerificationStatus.PENDING -> Triple(MaterialTheme.colorScheme.surfaceVariant, TextSecondary, if (isHindi) "प्रतीक्षारत" else "PENDING")
                }

                Surface(color = badgeColor, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = label,
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isHindi) record.verificationNoteHi else record.verificationNote,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pre: ${record.preActionMoisture.toInt()}%",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Post: ${record.postActionMoisture?.toInt() ?: "--"}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
                Text(
                    text = "Delta: +${record.actualDelta?.toInt() ?: 0}%",
                    fontSize = 11.sp,
                    color = StatusSafeGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
