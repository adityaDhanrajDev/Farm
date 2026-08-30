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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.flip.domain.model.ProduceBatch
import com.example.flip.domain.model.QualityGrade
import com.example.flip.domain.model.SellDecision
import com.example.ui.theme.*

@Composable
fun HarvestMarketScreen(
    produceBatches: List<ProduceBatch>,
    isHindi: Boolean,
    onAddBatch: () -> Unit,
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
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = AgriGreenDark,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "कटाई उपरांत व मंडी मूल्य बुद्धिमत्ता" else "Post-Harvest & Market Intelligence",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = if (isHindi) "भंडारण में खराबी का जोखिम vs 30 दिन का मूल्य पूर्वानुमान" else "Storage Spoilage Risk vs Econometric Sell-Store Matrix",
                            fontSize = 12.sp,
                            color = AgriGreenDark.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Sell vs Store Econometric Summary Rule
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "मंडी अर्थशास्त्र विश्लेषण (Sell vs. Store Strategy)" else "Sell vs Store Econometric Decision",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isHindi) "सिस्टम प्रत्येक बैच के गुणवत्ता ग्रेड (Grade A/B), कोल्ड स्टोरेज लागत (₹6.5/दिन) और खराबी के जोखिम को मिलाकर सर्वाधिक लाभ देने वाली रणनीति सुझाता है।"
                        else "FLIP integrates optical quality grading (A/B/C), real storage microclimate sensors, and regional Mandi APMC trends to optimize farmer profit margins.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Stored Produce Batches
        items(produceBatches) { batch ->
            ProduceBatchCard(batch = batch, isHindi = isHindi)
        }
    }
}

@Composable
fun ProduceBatchCard(
    batch: ProduceBatch,
    isHindi: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top: Crop name + Grade Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${batch.cropName} (${batch.variety})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Batch ID: ${batch.batchId} • ${batch.quantityQuintals} Quintals",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                val (gradeBg, gradeText, gradeLabel) = when (batch.qualityGrade) {
                    QualityGrade.GRADE_A -> Triple(StatusSafeContainer, StatusSafeGreen, if (isHindi) "ग्रेड A (उत्कृष्ट)" else "GRADE A")
                    QualityGrade.GRADE_B -> Triple(StatusWatchContainer, StatusWatchAmber, if (isHindi) "ग्रेड B (सामान्य)" else "GRADE B")
                    QualityGrade.GRADE_C -> Triple(StatusActionContainer, StatusActionRed, if (isHindi) "ग्रेड C (कमजोर)" else "GRADE C")
                }

                Surface(color = gradeBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = gradeLabel,
                        color = gradeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Storage Microclimate & Spoilage Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = if (isHindi) "भंडारण तापमान" else "Storage Temp", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "${batch.storageTempC}°C", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text(text = if (isHindi) "भंडारण आर्द्रता" else "Storage Humidity", fontSize = 10.sp, color = TextSecondary)
                    Text(text = "${batch.storageHumidityPercent.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = if (isHindi) "सड़न / खराबी जोखिम" else "Spoilage Risk", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        text = "${batch.spoilageRiskPercent}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (batch.spoilageRiskPercent >= 60) StatusActionRed else if (batch.spoilageRiskPercent >= 30) StatusWatchAmber else StatusSafeGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price Comparison Row
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = if (isHindi) "आज का मंडी भाव" else "Current Mandi Price", fontSize = 10.sp, color = TextSecondary)
                        Text(text = "₹${batch.currentMandiPricePerQuintal.toInt()}/Q", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column {
                        Text(text = if (isHindi) "30-दिन अनुमानित भाव" else "30-Day Forecast Price", fontSize = 10.sp, color = TextSecondary)
                        Text(text = "₹${batch.forecastedPrice30Days.toInt()}/Q", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = if (isHindi) "अनुमानित लाभ अंतर" else "Net Margin Delta", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            text = "${if (batch.netExpectedMarginDiffPercent > 0) "+" else ""}${batch.netExpectedMarginDiffPercent.toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (batch.netExpectedMarginDiffPercent > 0) StatusSafeGreen else StatusActionRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Recommendation Banner
            val (recBg, recColor, recTitle, recDesc) = when (batch.recommendation) {
                SellDecision.STORE_7_DAYS, SellDecision.STORE_3_DAYS -> Quadruple(
                    StatusSafeContainer, StatusSafeGreen,
                    if (isHindi) "रणनीति: अभी स्टोर करें (+${batch.netExpectedMarginDiffPercent.toInt()}% अतिरिक्त लाभ)" else "STRATEGY: HOLD IN STORAGE",
                    if (isHindi) "कम खराबी जोखिम व बढ़ते मूल्य अनुमान के कारण 3-7 दिन बाद बेचने पर अधिक लाभ होगा।"
                    else "High quality Grade A produce. Cold holding cost is offset by expected +18% price surge."
                )
                SellDecision.FIND_IMMEDIATE_BUYER -> Quadruple(
                    StatusActionContainer, StatusActionRed,
                    if (isHindi) "रणनीति: तुरंत खरीदार ढूंढें (खराबी का खतरा!)" else "STRATEGY: IMMEDIATE DISPATCH (Spoilage Threat)",
                    if (isHindi) "उच्च आर्द्रता (${batch.storageHumidityPercent.toInt()}%) व ${batch.spoilageRiskPercent}% खराबी जोखिम के कारण तुरंत बेचना सुरक्षित है।"
                    else "Spoilage risk is critical (${batch.spoilageRiskPercent}%). Sell immediately to avoid total write-off."
                )
                SellDecision.SELL_NOW -> Quadruple(
                    StatusWatchContainer, StatusWatchAmber,
                    if (isHindi) "रणनीति: आज मंडी में बेचें" else "STRATEGY: SELL AT CURRENT MANDI PRICE",
                    if (isHindi) "भंडारण लागत और भविष्य के मूल्य में अधिक अंतर नहीं है।" else "Holding cost matches forecast margin. Liquidate today."
                )
            }

            Surface(color = recBg, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = recTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = recColor)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = recDesc, fontSize = 11.sp, color = TextPrimary, lineHeight = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Matched FPO Buyer Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Matched Buyer: ${batch.matchedFpoBuyerName}",
                    fontSize = 11.sp,
                    color = AgriGreenDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
