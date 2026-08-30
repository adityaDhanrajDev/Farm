package com.example.flip.presentation.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flip.domain.model.AdvisoryItem
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.PredictionForecast
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SensorReading
import com.example.flip.presentation.components.DigitalTwinSummaryCard
import com.example.flip.presentation.components.EdgeIoTStatusCard
import com.example.flip.presentation.components.Explainable5WCard
import com.example.flip.presentation.components.MicroclimateTelemetryCard
import com.example.flip.presentation.components.RiskStatusBadge
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    fields: List<FieldTwin>,
    selectedField: FieldTwin?,
    latestTelemetry: SensorReading?,
    activeAdvisories: List<AdvisoryItem>,
    predictionForecasts: List<PredictionForecast>,
    isHindi: Boolean,
    onSelectField: (FieldTwin) -> Unit,
    onNavigateToCheckCrop: () -> Unit,
    onNavigateToWater: () -> Unit,
    onNavigateToHarvest: () -> Unit,
    onOpenVoiceAssistant: () -> Unit,
    onTakeAdvisoryAction: (AdvisoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Field Selector Switcher (Multi-farm support)
        item {
            Column {
                Text(
                    text = if (isHindi) "खेत चुनें (Active Field Twin):" else "Select Active Field Twin:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(fields) { field ->
                        val isSelected = field.fieldId == selectedField?.fieldId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AgriGreenPrimary else MaterialTheme.colorScheme.surface,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                            modifier = Modifier
                                .clickable { onSelectField(field) }
                                .testTag("field_chip_${field.fieldId}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else AgriGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = field.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Text(
                                        text = field.cropName,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Digital Twin Summary Card
        if (selectedField != null) {
            item {
                DigitalTwinSummaryCard(
                    field = selectedField,
                    latestTelemetry = latestTelemetry,
                    isHindi = isHindi,
                    onFieldClick = {}
                )
            }
        }

        // 2. Primary Farmer Question Banner: "What to do RIGHT NOW?"
        item {
            UrgentActionBanner(
                field = selectedField,
                isHindi = isHindi,
                onNavigateToWater = onNavigateToWater,
                onNavigateToCheckCrop = onNavigateToCheckCrop
            )
        }

        // 3. Quick Action Buttons (Farmer Low-Literacy Interface)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.CameraAlt,
                    label = if (isHindi) "फसल जांचें" else "Check Crop",
                    color = AgriGreenPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCheckCrop
                )
                QuickActionButton(
                    icon = Icons.Default.WaterDrop,
                    label = if (isHindi) "स्मार्ट सिंचाई" else "Smart Water",
                    color = WaterBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWater
                )
                QuickActionButton(
                    icon = Icons.Default.MonetizationOn,
                    label = if (isHindi) "कटाई व मंडी" else "Harvest & Sell",
                    color = StatusWatchAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHarvest
                )
                QuickActionButton(
                    icon = Icons.Default.Chat,
                    label = if (isHindi) "आवाज पूछें" else "Ask AI",
                    color = Color(0xFF673AB7),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenVoiceAssistant
                )
            }
        }

        // 4. Live IoT Telemetry Card
        item {
            MicroclimateTelemetryCard(
                telemetry = latestTelemetry,
                isHindi = isHindi
            )
        }

        // 5. 24h - 72h Pre-Symptom Risk Radar
        item {
            PredictionRadarSection(
                forecasts = predictionForecasts,
                isHindi = isHindi
            )
        }

        // 6. Active Explainable Advisories
        item {
            Text(
                text = if (isHindi) "सक्रिय कृषि सलाह व 5-W कारण (Advisories):" else "Active Agronomic Advisories & Evidence:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(activeAdvisories) { advisory ->
            AdvisoryCardItem(
                advisory = advisory,
                isHindi = isHindi,
                onTakeAction = { onTakeAdvisoryAction(advisory) }
            )
        }

        // 7. Edge IoT System Status Banner (Clean Minimalism Footer)
        item {
            EdgeIoTStatusCard(isHindi = isHindi)
        }
    }
}

@Composable
fun UrgentActionBanner(
    field: FieldTwin?,
    isHindi: Boolean,
    onNavigateToWater: () -> Unit,
    onNavigateToCheckCrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = StatusActionContainer),
        border = BorderStroke(1.dp, StatusActionRed.copy(alpha = 0.2f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = StatusActionRed,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isHindi) "आज क्या करना आवश्यक है?" else "WHAT TO DO RIGHT NOW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = StatusActionRed,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isHindi) {
                    field?.activeActionStateHi ?: "सभी स्थितियां सुरक्षित हैं। नियमित निगरानी रखें।"
                } else {
                    field?.activeActionState ?: "All conditions safe. Maintain standard routine."
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNavigateToWater,
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isHindi) "सिंचाई योजना" else "Irrigation Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onNavigateToCheckCrop,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AgriGreenPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isHindi) "पत्ती जांचें" else "Diagnose Foliage", fontSize = 12.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
            .height(82.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = color.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PredictionRadarSection(
    forecasts: List<PredictionForecast>,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isHindi) "लक्षण पूर्व जोखिम (अगले 72 घंटे):" else "PREDICTIVE RISK (NEXT 72H)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            forecasts.forEach { item ->
                ForecastRowItem(forecast = item, isHindi = isHindi)
            }
        }
    }
}

@Composable
fun ForecastRowItem(
    forecast: PredictionForecast,
    isHindi: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isHighRisk = forecast.diseaseSporeGerminationRiskPercent >= 70
            Surface(
                color = if (isHighRisk) StatusActionContainer else AgriGreenContainer,
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${forecast.forecastWindowHours}h",
                        color = if (isHighRisk) StatusActionRed else AgriGreenPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isHindi) forecast.primaryThreatHi else forecast.primaryThreat,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = if (isHindi) forecast.reasoningHi else forecast.reasoning,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isHighRisk) (if (isHindi) "उच्च" else "HIGH") else (if (isHindi) "मध्यम" else "MEDIUM"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighRisk) StatusActionRed else StatusWatchAmber
                )
                Text(
                    text = "${forecast.diseaseSporeGerminationRiskPercent}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun AdvisoryCardItem(
    advisory: AdvisoryItem,
    isHindi: Boolean,
    onTakeAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RiskStatusBadge(level = advisory.severity, isHindi = isHindi)
                Text(
                    text = if (isHindi) advisory.category.labelHi else advisory.category.labelEn,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isHindi) advisory.titleHi else advisory.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = if (isHindi) advisory.actionSummaryHi else advisory.actionSummary,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5-W Explainability Card Embedded
            Explainable5WCard(
                explanation = advisory.explanation,
                isHindi = isHindi
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!advisory.isActionTaken) {
                Button(
                    onClick = onTakeAction,
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isHindi) "कार्रवाई निष्पादित करें (Execute Action)" else "Execute Action & Verify", fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    color = StatusSafeContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusSafeGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "कार्रवाई निष्पादित की जा चुकी है" else "Action Executed & Recorded",
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
