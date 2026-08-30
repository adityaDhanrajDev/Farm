package com.example.flip.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.Explanation5W
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SensorReading
import com.example.flip.domain.usecase.VoiceResponse
import com.example.ui.theme.*

@Composable
fun RiskStatusBadge(
    level: RiskLevel,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (level) {
        RiskLevel.SAFE -> Triple(StatusSafeContainer, StatusSafeGreen, if (isHindi) "सुरक्षित (SAFE)" else "SAFE")
        RiskLevel.WATCH -> Triple(StatusWatchContainer, StatusWatchAmber, if (isHindi) "सावधानी (WATCH)" else "WATCH")
        RiskLevel.ACTION_NEEDED -> Triple(StatusActionContainer, StatusActionRed, if (isHindi) "कार्रवाई आवश्यक" else "ACTION NEEDED")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DigitalTwinSummaryCard(
    field: FieldTwin,
    latestTelemetry: SensorReading?,
    isHindi: Boolean,
    onFieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
        border = BorderStroke(1.dp, AgriGreenPrimary.copy(alpha = 0.15f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onFieldClick)
            .testTag("digital_twin_summary_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Pill Badge & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = AgriGreenPrimary,
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = if (isHindi) "डिजिटल ट्विन: सक्रिय" else "Digital Twin: Active",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = if (field.healthIndex < 80) "WATCH • ${field.name}" else "OPTIMAL • ${field.name}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${field.cropName} • ${if (isHindi) field.cropStage.labelHi else field.cropStage.labelEn}",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Confidence / Health Gauge
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isHindi) "विश्वास स्कोर" else "CONFIDENCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${field.healthIndex}%",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-Cards (White frosted style)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Soil Moisture Sub-Card
                Surface(
                    color = Color.White.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SurfaceBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isHindi) "मिट्टी नमी" else "SOIL MOISTURE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val moisture = latestTelemetry?.soilMoisturePercent?.toInt() ?: 25
                        Text(
                            text = "$moisture%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (moisture < 22) (if (isHindi) "↓ कम (लक्ष्य 30%)" else "↓ Low (Target 30%)")
                            else if (moisture > 35) (if (isHindi) "↑ अधिक जल" else "↑ Saturated")
                            else (if (isHindi) "✓ अनुकूल" else "✓ Optimal"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (moisture < 22) StatusActionRed else StatusSafeGreen
                        )
                    }
                }

                // Air Temp Sub-Card
                Surface(
                    color = Color.White.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SurfaceBorder.copy(alpha = 0.6f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isHindi) "तापमान" else "AIR TEMP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val temp = latestTelemetry?.airTempC?.toInt() ?: 31
                        Text(
                            text = "$temp°C",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (temp > 35) (if (isHindi) "↑ उच्च ताप" else "↑ Heat Spike")
                            else (if (isHindi) "✓ अनुकूल" else "✓ Optimal"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (temp > 35) StatusWatchAmber else StatusSafeGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EdgeIoTStatusCard(
    isHindi: Boolean,
    isSyncing: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkBanner,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = AgriGreenPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(8.dp)
                ) {}
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isHindi) "एज IoT सिस्टम: कनेक्टेड (लाइव)" else "Edge IoT System: Connected",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Text(
                text = if (isSyncing) "SYNCING..." else "SYNC: 14s AGO",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MetricChip(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Explainable5WCard(
    explanation: Explanation5W,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AgriGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "5-W एआई कारण व साक्ष्य (Explainability Engine)" else "5-W AI Explainability & Evidence",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = AgriGreenContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = explanation.confidenceText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            ExplainRow(label = if (isHindi) "क्या? (WHAT)" else "WHAT?", content = if (isHindi && explanation.whatHi.isNotEmpty()) explanation.whatHi else explanation.what)
            ExplainRow(label = if (isHindi) "क्यों? (WHY)" else "WHY?", content = if (isHindi && explanation.whyHi.isNotEmpty()) explanation.whyHi else explanation.why)
            ExplainRow(label = if (isHindi) "कब? (WHEN)" else "WHEN?", content = if (isHindi && explanation.whenWindowHi.isNotEmpty()) explanation.whenWindowHi else explanation.whenWindow)
            ExplainRow(label = if (isHindi) "क्या करें? (ACTION)" else "WHAT TO DO?", content = if (isHindi && explanation.whatToDoHi.isNotEmpty()) explanation.whatToDoHi else explanation.whatToDo, isHighlighted = true)

            if (explanation.evidenceFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isHindi) "प्रयुक्त साक्ष्य घटक (Fused Telemetry Factors):" else "Evidence Factors Fused:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    explanation.evidenceFactors.forEach { factor ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = factor,
                                fontSize = 10.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplainRow(
    label: String,
    content: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) AgriGreenPrimary else TextSecondary,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = content,
            fontSize = 12.sp,
            color = if (isHighlighted) TextPrimary else TextSecondary,
            fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MicroclimateTelemetryCard(
    telemetry: SensorReading?,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    if (telemetry == null) return

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "फील्ड IoT सेंसर व माइक्रोक्लाइमेट" else "Field IoT & Microclimate Stream",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // Sensor Health Badge
                val isHealthy = telemetry.qualityStatus == SensorQualityStatus.VALID
                Surface(
                    color = if (isHealthy) StatusSafeContainer else StatusActionContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isHealthy) {
                            if (isHindi) "सेंसर 100% सही" else "Sensors OK (${(telemetry.sensorReliabilityScore * 100).toInt()}%)"
                        } else {
                            if (isHindi) "सेंसर त्रुटि!" else "Sensor Fault!"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHealthy) StatusSafeGreen else StatusActionRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TelemetryGridItem(
                    label = if (isHindi) "मिट्टी नमी" else "Soil Moisture",
                    value = "${telemetry.soilMoisturePercent.toInt()}%",
                    sub = if (isHindi) "लक्ष्य: 24-30%" else "Opt: 24-30%"
                )
                TelemetryGridItem(
                    label = if (isHindi) "वातावरण तापमान" else "Air Temp",
                    value = "${telemetry.airTempC.toInt()}°C",
                    sub = if (isHindi) "मिट्टी: ${telemetry.soilTempC.toInt()}°C" else "Soil: ${telemetry.soilTempC.toInt()}°C"
                )
                TelemetryGridItem(
                    label = if (isHindi) "सापेक्ष आर्द्रता" else "Humidity",
                    value = "${telemetry.humidityPercent.toInt()}%",
                    sub = if (telemetry.humidityPercent > 80) if (isHindi) "कवक खतरा" else "Fungal Risk" else if (isHindi) "सामान्य" else "Normal"
                )
                TelemetryGridItem(
                    label = if (isHindi) "पत्ती गीलापन" else "Leaf Wetness",
                    value = "${telemetry.leafWetnessHours}h",
                    sub = if (telemetry.leafWetnessHours > 4) if (isHindi) "अधिक" else "High" else if (isHindi) "कम" else "Dry"
                )
            }
        }
    }
}

@Composable
private fun TelemetryGridItem(
    label: String,
    value: String,
    sub: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Text(text = sub, fontSize = 9.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAssistantBottomSheet(
    isOpen: Boolean,
    isListening: Boolean,
    voiceQueryText: String,
    response: VoiceResponse?,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSubmitQuery: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "FLIP आवाज सहायक (Voice AI)" else "FLIP Agrarian Voice Assistant",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Microphone pulse visual
                Surface(
                    color = if (isListening) StatusWatchAmber else AgriGreenPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(72.dp)
                        .clickable {
                            onSubmitQuery(
                                if (isHindi) "मेरी फसल को पानी कब देना है?" else "When should I irrigate my tomato crop?"
                            )
                        }
                        .testTag("voice_mic_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isListening) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(40.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isListening) {
                        if (isHindi) "सुन रहा हूँ... (Listening)" else "Listening to field query..."
                    } else {
                        if (isHindi) "माइक दबाएं या नीचे दिए प्रश्न पर टैप करें" else "Tap mic or select a quick question below"
                    },
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Questions Chips
                Text(
                    text = if (isHindi) "त्वरित प्रश्न (Quick Questions):" else "Sample Questions:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                val sampleQueries = if (isHindi) listOf(
                    "मेरी फसल को पानी कब देना है?",
                    "क्या खेत में कोई बीमारी का खतरा है?",
                    "टमाटर की कटाई कब करें और मंडी भाव क्या है?"
                ) else listOf(
                    "When should I irrigate my crop?",
                    "Is there any fungal disease risk?",
                    "When is the optimal harvest date?"
                )

                sampleQueries.forEach { query ->
                    OutlinedButton(
                        onClick = { onSubmitQuery(query) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(text = query, fontSize = 12.sp)
                    }
                }

                // Spoken Response View
                AnimatedVisibility(visible = response != null) {
                    if (response != null) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = AgriGreenDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isHindi) "एआई का उत्तर (Voice Response):" else "AI Audio Advisory:",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriGreenDark
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) response.spokenTextHi else response.spokenTextEn,
                                    fontSize = 14.sp,
                                    color = AgriOnGreenContainer,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "👉 ${if (isHindi) response.actionSuggestionHi else response.actionSuggestion}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ClosedLoopVerificationDialog(
    record: ActionRecord,
    isHindi: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (record.verificationStatus) {
                        ActionVerificationStatus.VERIFIED_SUCCESS -> Icons.Default.CheckCircle
                        ActionVerificationStatus.VERIFIED_PARTIAL -> Icons.Default.Warning
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = if (record.verificationStatus == ActionVerificationStatus.VERIFIED_SUCCESS) StatusSafeGreen else StatusWatchAmber
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "बंद-लूप कार्रवाई सत्यापन (Closed-Loop Result)" else "Closed-Loop Verification",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = if (isHindi) record.verificationNoteHi else record.verificationNote,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${if (isHindi) "पूर्व नमी" else "Pre-Moisture"}: ${record.preActionMoisture.toInt()}%",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${if (isHindi) "पश्चात नमी" else "Post-Moisture"}: ${record.postActionMoisture?.toInt() ?: "--"}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                    Text(
                        text = "${if (isHindi) "वृद्धि" else "Delta"}: +${record.actualDelta?.toInt() ?: 0}%",
                        fontSize = 12.sp,
                        color = StatusSafeGreen
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isHindi) "💡 यह डेटा मॉडल के लर्निंग लूप में दर्ज कर लिया गया है।" else "💡 Result recorded into Closed-Loop Learning Dataset for farm adaptation.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Text(if (isHindi) "ठीक है (OK)" else "Acknowledge")
            }
        }
    )
}
