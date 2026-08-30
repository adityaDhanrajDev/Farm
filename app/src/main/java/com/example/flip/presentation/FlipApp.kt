package com.example.flip.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flip.presentation.components.ClosedLoopVerificationDialog
import com.example.flip.presentation.components.VoiceAssistantBottomSheet
import com.example.flip.presentation.screens.DashboardScreen
import com.example.flip.presentation.screens.DiagnosticScreen
import com.example.flip.presentation.screens.HarvestMarketScreen
import com.example.flip.presentation.screens.HotspotExpertScreen
import com.example.flip.presentation.screens.SimulationScreen
import com.example.flip.presentation.screens.SmartWaterScreen
import com.example.ui.theme.*
import com.example.flip.presentation.viewmodel.FlipViewModel
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.StatusSafeGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WaterBlue

enum class FlipNavigationItem(
    val titleEn: String,
    val titleHi: String,
    val icon: ImageVector
) {
    DASHBOARD("Twin", "खेत ट्विन", Icons.Default.Dashboard),
    DIAGNOSTIC("Diagnose", "फसल जांच", Icons.Default.CameraAlt),
    WATER("Irrigate", "सिंचाई", Icons.Default.WaterDrop),
    MARKET("Market", "मंडी", Icons.Default.MonetizationOn),
    HOTSPOT("Hotspots", "प्रकोप", Icons.Default.Map),
    SIMULATION("Sandbox", "परीक्षण", Icons.Default.Science)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipApp(
    viewModel: FlipViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentTab by remember { mutableStateOf(FlipNavigationItem.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.lastSyncMessage) {
        uiState.lastSyncMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AgriGreenPrimary,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "F",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "FLIP",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                            Text(
                                text = if (uiState.isHindi) "फील्ड इंटेलिजेंस • EDGE AI" else "FIELD INTELLIGENCE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    // Offline Delta Sync Button
                    Surface(
                        color = SurfaceElevated,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_sync_offline")
                    ) {
                        IconButton(
                            onClick = { viewModel.syncData() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = AgriGreenPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Sync",
                                    tint = AgriGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Language Toggle (English / हिन्दी)
                    Surface(
                        color = AgriGreenContainer,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("btn_toggle_language")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleLanguage() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = AgriGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = if (uiState.isHindi) "हिन्दी" else "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    FlipNavigationItem.values().forEach { item ->
                        val isSelected = currentTab == item
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = item },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.titleEn,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = if (uiState.isHindi) item.titleHi else item.titleEn,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AgriGreenPrimary,
                                selectedTextColor = AgriGreenPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = AgriGreenContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${item.name.lowercase()}")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openVoiceAssistant() },
                containerColor = AgriGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_voice_assistant")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Assistant",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentTab) {
                FlipNavigationItem.DASHBOARD -> {
                    DashboardScreen(
                        fields = uiState.fields,
                        selectedField = uiState.selectedField,
                        latestTelemetry = uiState.latestTelemetry,
                        activeAdvisories = uiState.activeAdvisories,
                        predictionForecasts = uiState.predictionForecasts,
                        isHindi = uiState.isHindi,
                        onSelectField = { viewModel.selectField(it) },
                        onNavigateToCheckCrop = { currentTab = FlipNavigationItem.DIAGNOSTIC },
                        onNavigateToWater = { currentTab = FlipNavigationItem.WATER },
                        onNavigateToHarvest = { currentTab = FlipNavigationItem.MARKET },
                        onOpenVoiceAssistant = { viewModel.openVoiceAssistant() },
                        onTakeAdvisoryAction = { currentTab = FlipNavigationItem.WATER }
                    )
                }

                FlipNavigationItem.DIAGNOSTIC -> {
                    DiagnosticScreen(
                        selectedField = uiState.selectedField,
                        latestTelemetry = uiState.latestTelemetry,
                        isDiagnosing = uiState.isDiagnosing,
                        latestDiagnosis = uiState.latestDiagnosis,
                        expertEscalated = uiState.expertEscalationSuccess,
                        isHindi = uiState.isHindi,
                        onRunDiagnosis = { viewModel.runMultimodalDiagnosis(it) },
                        onEscalateToExpert = { viewModel.escalateToExpert(it) }
                    )
                }

                FlipNavigationItem.WATER -> {
                    SmartWaterScreen(
                        selectedField = uiState.selectedField,
                        latestTelemetry = uiState.latestTelemetry,
                        telemetryHistory = uiState.telemetryHistory,
                        decision = uiState.currentIrrigationDecision,
                        actionHistory = uiState.actionHistory,
                        isExecuting = uiState.isExecutingIrrigation,
                        isHindi = uiState.isHindi,
                        onExecuteIrrigation = { viewModel.executeIrrigationAction(it) }
                    )
                }

                FlipNavigationItem.MARKET -> {
                    HarvestMarketScreen(
                        produceBatches = uiState.produceBatches,
                        isHindi = uiState.isHindi,
                        onAddBatch = {}
                    )
                }

                FlipNavigationItem.HOTSPOT -> {
                    HotspotExpertScreen(
                        hotspots = uiState.regionalHotspots,
                        isHindi = uiState.isHindi
                    )
                }

                FlipNavigationItem.SIMULATION -> {
                    SimulationScreen(
                        isHindi = uiState.isHindi,
                        onTriggerScenario = { viewModel.triggerSimulation(it) }
                    )
                }
            }
        }
    }

    // Voice Assistant Bottom Sheet
    VoiceAssistantBottomSheet(
        isOpen = uiState.isVoiceModalOpen,
        isListening = uiState.isListeningVoice,
        voiceQueryText = uiState.voiceQueryText,
        response = uiState.voiceResponse,
        isHindi = uiState.isHindi,
        onDismiss = { viewModel.closeVoiceAssistant() },
        onSubmitQuery = { viewModel.submitVoiceQuery(it) }
    )

    // Closed-Loop Verification Result Dialog
    uiState.actionVerificationResult?.let { record ->
        ClosedLoopVerificationDialog(
            record = record,
            isHindi = uiState.isHindi,
            onDismiss = { viewModel.dismissVerificationDialog() }
        )
    }
}
