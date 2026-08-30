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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearbyError
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flip.domain.model.HotspotZone
import com.example.flip.domain.model.RiskLevel
import com.example.flip.presentation.components.RiskStatusBadge
import com.example.ui.theme.*

@Composable
fun HotspotExpertScreen(
    hotspots: List<HotspotZone>,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StatusWatchContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CrisisAlert,
                        contentDescription = null,
                        tint = StatusWatchAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "क्षेत्रीय रोग प्रकोप व विशेषज्ञ सहायता" else "Regional Outbreak Radar & KVK Experts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusWatchAmber
                        )
                        Text(
                            text = if (isHindi) "निकटवर्ती 20 किमी में रोगों के फैलाव का रियल-टाइम मैप" else "Federated Epidemiological Vectors & Extension Officer Network",
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // GIS Heatmap Visual Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "स्थानिक संक्रमण प्रसार मानचित्र (Spread Vectors):" else "Spatial Infection Spread Velocity:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Map Viewport Simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF263238)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Lucknow Agrarian Cluster GIS Map", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "3 Hotspots Active • Spread Velocity 2.4 km/day (SE)", color = Color(0xFFFFCC80), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Hotspot Items
        item {
            Text(
                text = if (isHindi) "सक्रिय प्रकोप क्षेत्र (Active Hotspot Clusters):" else "Active Regional Hotspots Near You:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(hotspots) { hotspot ->
            HotspotCardItem(hotspot = hotspot, isHindi = isHindi)
        }

        // Expert Human-in-the-loop Direct Connect Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = AgriGreenDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "कृषि विज्ञान केंद्र (KVK) वैज्ञानिक हेल्पलाइन" else "Agronomist & KVK Extension Hotline",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isHindi) "यदि एआई किसी असामान्य लक्षण पर अनिश्चित (Unknown) रहे, तो आप सीधे निकटतम सरकारी वैज्ञानिक अथवा एफपीओ विशेषज्ञ से जुड़ सकते हैं।"
                        else "FLIP guarantees human oversight. Complex, low-confidence or high-risk chemical diagnoses are reviewed by certified ICAR/KVK scientists.",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHindi) "KVK वैज्ञानिक से बात करें (1800-180-1551)" else "Call Toll-Free Kisan Helpline")
                    }
                }
            }
        }
    }
}

@Composable
fun HotspotCardItem(
    hotspot: HotspotZone,
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
                    text = "${hotspot.villageName}, ${hotspot.district}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                RiskStatusBadge(level = hotspot.threatLevel, isHindi = isHindi)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Threat: ${if (isHindi) hotspot.activeThreatHi else hotspot.activeThreat}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (hotspot.threatLevel == RiskLevel.ACTION_NEEDED) StatusActionRed else StatusWatchAmber
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Distance: ${hotspot.distanceKmFromUser} km", fontSize = 11.sp, color = TextSecondary)
                Text(text = "Spread: ${hotspot.spreadVelocityKmPerDay} km/day", fontSize = 11.sp, color = TextSecondary)
                Text(text = "Farms Affected: ${hotspot.affectedFarmsCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
            }
        }
    }
}
