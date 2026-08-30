package com.example.flip.presentation.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SimulationScreen(
    isHindi: Boolean,
    onTriggerScenario: (String) -> Unit,
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isHindi) "IoT हार्डवेयर व एआई परीक्षण लैब (Sandbox)" else "IoT Hardware & Edge-AI Testbed",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isHindi) "हैकाथॉन व फील्ड परीक्षण हेतु लाइव वातावरण परिदृश्य इंजेक्ट करें" else "Inject Extreme Weather, Drought & Sensor Fault Events",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Scenario 1: Drought Stress
        item {
            SimulationScenarioCard(
                title = if (isHindi) "परिदृश्य 1: भीषण गर्मी व सूखा तनाव (Drought Shock)" else "Scenario 1: Critical Heat & Drought Shock",
                desc = if (isHindi) "मिट्टी की नमी गिरकर 13.8% हो जाएगी व तापमान 39.5°C होगा। डिजिटल ट्विन तुरंत सिंचाई अलर्ट सक्रिय करेगा।"
                else "Injects 13.8% soil moisture and 39.5°C heat spike. Evaluates immediate water deficit advisory trigger.",
                tag = "SIMULATE_DROUGHT",
                buttonText = if (isHindi) "सूखा परिदृश्य इंजेक्ट करें" else "Inject Drought Stress Event",
                containerColor = StatusWatchContainer,
                accentColor = StatusWatchAmber,
                icon = Icons.Default.WbSunny,
                onTrigger = onTriggerScenario
            )
        }

        // Scenario 2: 32mm Heavy Rain
        item {
            SimulationScenarioCard(
                title = if (isHindi) "परिदृश्य 2: 32 मिमी भारी वर्षा (Heavy Rainfall Flood Risk)" else "Scenario 2: 32mm Heavy Rain Event",
                desc = if (isHindi) "मिट्टी में 38% जलभराव व 94% आर्द्रता होगी। एआई सिंचाई रोकेगा तथा जल निकासी व फफूंद रोकथाम अलर्ट देगा।"
                else "Injects 32mm rain telemetry and 94% humidity. Evaluates rain-hold logic and fungal spore risk surge.",
                tag = "SIMULATE_HEAVY_RAIN",
                buttonText = if (isHindi) "भारी बारिश परिदृश्य इंजेक्ट करें" else "Inject Heavy Rainfall Event",
                containerColor = WaterBlueContainer,
                accentColor = WaterBlue,
                icon = Icons.Default.WaterDamage,
                onTrigger = onTriggerScenario
            )
        }

        // Scenario 3: Sensor Hardware Anomaly / Fault
        item {
            SimulationScenarioCard(
                title = if (isHindi) "परिदृश्य 3: सेंसर हार्डवेयर खराबी / असम्भव रीडिंग" else "Scenario 3: Sensor Anomaly / Impossible Spike",
                desc = if (isHindi) "सेंसर असम्भव 4% सूखा भेजेगा जबकि वर्षा 25mm है। विश्वसनीयता इंजन सेंसर को दोषपूर्ण चिह्नित कर किसान को गलत सलाह से रोकेगा।"
                else "Injects conflicting 4% moisture alongside 25mm rain. Verifies that the Sensor Anomaly Engine flags faulty hardware.",
                tag = "SIMULATE_SENSOR_FAULT",
                buttonText = if (isHindi) "सेंसर खराबी इंजेक्ट करें" else "Inject Corrupted Sensor Telemetry",
                containerColor = StatusActionContainer,
                accentColor = StatusActionRed,
                icon = Icons.Default.SensorsOff,
                onTrigger = onTriggerScenario
            )
        }

        // Scenario 4: Reset Normal
        item {
            SimulationScenarioCard(
                title = if (isHindi) "परिदृश्य 4: सामान्य व अनुकूल स्थिति (Optimal State)" else "Scenario 4: Restore Optimal Baseline",
                desc = if (isHindi) "मिट्टी नमी 27.5%, तापमान 31°C व 99% सेंसर विश्वसनीयता पर सामान्य अवस्था बहाल करता है।"
                else "Resets soil moisture to 27.5% and air temperature to 31°C. Restores all health indexes to optimal baseline.",
                tag = "SIMULATE_NORMAL",
                buttonText = if (isHindi) "सामान्य स्थिति बहाल करें" else "Restore Healthy Baseline",
                containerColor = AgriGreenContainer,
                accentColor = AgriGreenDark,
                icon = Icons.Default.Restore,
                onTrigger = onTriggerScenario
            )
        }
    }
}

@Composable
fun SimulationScenarioCard(
    title: String,
    desc: String,
    tag: String,
    buttonText: String,
    containerColor: Color,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onTrigger: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = containerColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = desc,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onTrigger(tag) },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_$tag")
            ) {
                Text(text = buttonText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
