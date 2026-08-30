package com.example.flip.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flip.domain.model.ConfidenceLevel
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.MultimodalDiagnosis
import com.example.flip.domain.model.SensorReading
import com.example.flip.presentation.components.Explainable5WCard
import com.example.ui.theme.*

@Composable
fun DiagnosticScreen(
    selectedField: FieldTwin?,
    latestTelemetry: SensorReading?,
    isDiagnosing: Boolean,
    latestDiagnosis: MultimodalDiagnosis?,
    expertEscalated: Boolean,
    isHindi: Boolean,
    onRunDiagnosis: (String) -> Unit,
    onEscalateToExpert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSymptomPreset by remember { mutableStateOf("Fungal brown spots on lower leaves") }

    val symptomPresets = if (isHindi) listOf(
        "पत्तियों पर भूरे गोल धब्बे (Fungal Blight Spots)",
        "पत्तियों का मुरझाना व सूखापन (Wilting & Water Stress)",
        "पत्तियां पीली पड़ना (Chlorosis / Nitrogen Deficit)",
        "अस्पष्ट बहु-लक्षण (Atypical / Unknown Anomaly)",
        "पत्तियां स्वस्थ व हरी (Healthy Vigorous Canopy)"
    ) else listOf(
        "Fungal brown spots on lower leaves",
        "Leaf wilting and dry curling",
        "Interveinal yellowing / chlorosis",
        "Atypical / Unclassified mixed anomaly",
        "Healthy vigorous green foliage"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Top Context Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = AgriGreenDark,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "मल्टीमॉडल एआई फसल निदान (Multimodal AI)" else "Multimodal Crop Intelligence",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = if (isHindi) "कैमरा + IoT सेंसर + मौसम + फसल अवस्था का संयुक्त विश्लेषण" else "Fused Camera + IoT Soil/Air + Weather + Phenology",
                            fontSize = 12.sp,
                            color = AgriGreenDark.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Camera Simulation & Symptom Input Box
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "1. पत्ती अथवा कीट का दृश्य नमूना (Visual Input):" else "1. Leaf / Pest Observation Capture:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Camera Viewport Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E2822)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isHindi) "कैमरा सक्रिय • पत्ती को फ्रेम के बीच रखें" else "Camera Active • Align Leaf in Center",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Selected: $selectedSymptomPreset",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preset Symptom Selector Chips
                    Text(
                        text = if (isHindi) "नमूना लक्षण चुनें (Symptom Patterns):" else "Select Observed Symptom Sample:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        symptomPresets.forEach { preset ->
                            val isSelected = selectedSymptomPreset == preset
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) AgriGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AgriGreenPrimary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSymptomPreset = preset }
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AgriGreenDark else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time Fused Sensor Context preview
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live IoT Telemetry: ${latestTelemetry?.soilMoisturePercent?.toInt() ?: 25}% Moisture | ${latestTelemetry?.humidityPercent?.toInt() ?: 60}% Humidity | ${latestTelemetry?.airTempC?.toInt() ?: 30}°C",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onRunDiagnosis(selectedSymptomPreset) },
                        enabled = !isDiagnosing,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_diagnosis_button")
                    ) {
                        if (isDiagnosing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "मल्टीमॉडल एआई विश्लेषण जारी..." else "Processing Multimodal Fusion AI...")
                        } else {
                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "निदान करें (Run Multimodal AI)" else "Run Multimodal Diagnosis")
                        }
                    }
                }
            }
        }

        // Diagnosis Result View
        if (latestDiagnosis != null) {
            item {
                DiagnosisResultCard(
                    diagnosis = latestDiagnosis,
                    expertEscalated = expertEscalated,
                    isHindi = isHindi,
                    onEscalate = { onEscalateToExpert("Farmer requested verification for ${latestDiagnosis.detectedCondition}") }
                )
            }
        }
    }
}

@Composable
fun DiagnosisResultCard(
    diagnosis: MultimodalDiagnosis,
    expertEscalated: Boolean,
    isHindi: Boolean,
    onEscalate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Confidence Badge & Abstention Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (badgeColor, textColor, confLabel) = when (diagnosis.confidenceLevel) {
                    ConfidenceLevel.CONFIDENT -> Triple(StatusSafeContainer, StatusSafeGreen, if (isHindi) "उच्च विश्वास (>80%)" else "HIGH CONFIDENCE")
                    ConfidenceLevel.LOW_CONFIDENCE -> Triple(StatusWatchContainer, StatusWatchAmber, if (isHindi) "मध्यम विश्वास" else "LOW CONFIDENCE")
                    ConfidenceLevel.UNKNOWN -> Triple(StatusActionContainer, StatusActionRed, if (isHindi) "अज्ञात / अनिश्चित (Abstain)" else "UNKNOWN / ABSTAIN")
                    ConfidenceLevel.EXPERT_REVIEW_REQUIRED -> Triple(StatusActionContainer, StatusActionRed, if (isHindi) "विशेषज्ञ समीक्षा आवश्यक" else "EXPERT REVIEW")
                }

                Surface(color = badgeColor, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = confLabel,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${diagnosis.confidenceScorePercent}% Confidence Score",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Detected Condition Heading
            Text(
                text = if (isHindi) diagnosis.detectedConditionHi else diagnosis.detectedCondition,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            // Warning if AI Abstained
            if (diagnosis.isAbstained) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = StatusActionContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = StatusActionRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "⚠️ एआई सुरक्षा प्रोटोकॉल: लक्षण अस्पष्ट होने के कारण गलत रासायनिक सलाह से बचने हेतु निर्णय स्थगित किया गया है।"
                            else "⚠️ AI Safety Protocol: Ambiguous symptoms detected. AI is abstaining from pesticide recommendations to prevent crop damage.",
                            fontSize = 12.sp,
                            color = StatusActionRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5-W Explainability Card
            Explainable5WCard(
                explanation = diagnosis.explanation,
                isHindi = isHindi
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Safety Warning for Chemical Sprays
            if (diagnosis.chemicalInterventionRequiresConfirmation) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = StatusWatchAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "सुरक्षा निर्देश: तेज हवा (>12 km/h) में छिड़काव न करें। सुरक्षात्मक मास्क का प्रयोग करें।"
                            else "Agronomic Safety: Do not spray in high winds (>12 km/h). Use protective PPE.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Human Expert Escalation Button
            if (diagnosis.requiresHumanExpert || diagnosis.isAbstained) {
                if (!expertEscalated) {
                    OutlinedButton(
                        onClick = onEscalate,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "कृषि विशेषज्ञ (KVK) को सत्यापन हेतु भेजें" else "Escalate to Human Agronomist (KVK)",
                            color = AgriGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Surface(
                        color = StatusSafeContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusSafeGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "विशेषज्ञ को भेजा गया • 4 घंटे में रिपोर्ट प्राप्त होगी" else "Escalated to Extension Officer • Report in 4 hours",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusSafeGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
