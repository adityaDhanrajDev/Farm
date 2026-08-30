package com.example.flip.presentation.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flip.domain.model.CropAnalysisReport
import com.example.flip.domain.model.RiskLevel
import com.example.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ReportSeverityFilter {
    ALL,
    ACTION_NEEDED,
    WATCH,
    SAFE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropGalleryScreen(
    reports: List<CropAnalysisReport>,
    isHindi: Boolean,
    onDeleteReport: (String) -> Unit,
    onNavigateToScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(ReportSeverityFilter.ALL) }
    var sortDescending by remember { mutableStateOf(true) } // True: Newest first, False: Oldest first
    var selectedReportForDetail by remember { mutableStateOf<CropAnalysisReport?>(null) }
    var reportToDelete by remember { mutableStateOf<CropAnalysisReport?>(null) }

    // Filter and sort reports by date
    val filteredReports = remember(reports, selectedFilter, sortDescending) {
        val list = when (selectedFilter) {
            ReportSeverityFilter.ALL -> reports
            ReportSeverityFilter.ACTION_NEEDED -> reports.filter { it.severityLevel == RiskLevel.ACTION_NEEDED }
            ReportSeverityFilter.WATCH -> reports.filter { it.severityLevel == RiskLevel.WATCH }
            ReportSeverityFilter.SAFE -> reports.filter { it.severityLevel == RiskLevel.SAFE }
        }
        if (sortDescending) {
            list.sortedByDescending { it.timestamp }
        } else {
            list.sortedBy { it.timestamp }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Top Header Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery",
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "विश्लेषण रिपोर्ट गैलरी" else "Saved Diagnostic Gallery",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = if (isHindi)
                                "स्थानीय डेटाबेस से प्राप्त ${filteredReports.size} रिपोर्ट (दिनांक अनुसार क्रमबद्ध)"
                            else
                                "Fetched ${filteredReports.size} saved reports from local database (sorted by date)",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Sort Order Toggle Button
                    Surface(
                        color = AgriGreenContainer,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("btn_toggle_sort")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { sortDescending = !sortDescending }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (sortDescending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = "Sort Date",
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (sortDescending) {
                                    if (isHindi) "नवीनतम पहले" else "Newest"
                                } else {
                                    if (isHindi) "पुराना पहले" else "Oldest"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == ReportSeverityFilter.ALL,
                            onClick = { selectedFilter = ReportSeverityFilter.ALL },
                            label = {
                                Text(
                                    text = if (isHindi) "सभी (${reports.size})" else "All (${reports.size})",
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_all")
                        )
                    }
                    item {
                        val count = reports.count { it.severityLevel == RiskLevel.ACTION_NEEDED }
                        FilterChip(
                            selected = selectedFilter == ReportSeverityFilter.ACTION_NEEDED,
                            onClick = { selectedFilter = ReportSeverityFilter.ACTION_NEEDED },
                            label = {
                                Text(
                                    text = if (isHindi) "कार्रवाई योग्य ($count)" else "Action Needed ($count)",
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusActionRed,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_action")
                        )
                    }
                    item {
                        val count = reports.count { it.severityLevel == RiskLevel.WATCH }
                        FilterChip(
                            selected = selectedFilter == ReportSeverityFilter.WATCH,
                            onClick = { selectedFilter = ReportSeverityFilter.WATCH },
                            label = {
                                Text(
                                    text = if (isHindi) "निगरानी ($count)" else "Watch ($count)",
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusWatchAmber,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_watch")
                        )
                    }
                    item {
                        val count = reports.count { it.severityLevel == RiskLevel.SAFE }
                        FilterChip(
                            selected = selectedFilter == ReportSeverityFilter.SAFE,
                            onClick = { selectedFilter = ReportSeverityFilter.SAFE },
                            label = {
                                Text(
                                    text = if (isHindi) "सुरक्षित ($count)" else "Safe ($count)",
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StatusSafeGreen,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_safe")
                        )
                    }
                }
            }
        }

        // Main List Content
        if (filteredReports.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = AgriGreenContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "No reports",
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "कोई विश्लेषण रिपोर्ट नहीं मिली" else "No Analysis Reports Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isHindi)
                            "खेत की फसल का नया फोटो खींचकर AI विश्लेषण शुरू करें और रिपोर्ट यहां सुरक्षित करें।"
                        else
                            "Capture or select a crop leaf photo to run AI diagnosis. Saved reports will appear here automatically.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToScan,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_empty_scan_crop")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isHindi) "फसल स्कैन करें" else "Scan Crop Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Scrollable List of Reports
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("gallery_reports_list")
            ) {
                items(filteredReports, key = { it.reportId }) { report ->
                    CropReportGalleryCard(
                        report = report,
                        isHindi = isHindi,
                        onViewDetail = { selectedReportForDetail = report },
                        onDelete = { reportToDelete = report }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedReportForDetail?.let { report ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedReportForDetail = null },
            sheetState = sheetState,
            containerColor = SurfaceLight
        ) {
            ReportDetailSheetContent(
                report = report,
                isHindi = isHindi,
                onClose = { selectedReportForDetail = null },
                onDelete = {
                    onDeleteReport(report.reportId)
                    selectedReportForDetail = null
                }
            )
        }
    }

    // Delete Confirmation Dialog
    reportToDelete?.let { report ->
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            title = {
                Text(
                    text = if (isHindi) "रिपोर्ट हटाएं?" else "Delete Analysis Report?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi)
                        "क्या आप '${if (report.detectedConditionHi.isNotEmpty()) report.detectedConditionHi else report.detectedCondition}' रिपोर्ट को स्थानीय डेटाबेस से हटाना चाहते हैं?"
                    else
                        "Are you sure you want to delete the report for '${report.detectedCondition}' from local storage?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteReport(report.reportId)
                        reportToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusActionRed),
                    modifier = Modifier.testTag("btn_confirm_delete_report")
                ) {
                    Text(text = if (isHindi) "हटाएं" else "Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text(text = if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun CropReportGalleryCard(
    report: CropAnalysisReport,
    isHindi: Boolean,
    onViewDetail: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(report.timestamp) { dateFormat.format(Date(report.timestamp)) }

    val (badgeBg, badgeText, badgeBorder) = when (report.severityLevel) {
        RiskLevel.ACTION_NEEDED -> Triple(StatusActionContainer, StatusActionRed, StatusActionRed.copy(alpha = 0.4f))
        RiskLevel.WATCH -> Triple(StatusWatchContainer, StatusWatchAmber, StatusWatchAmber.copy(alpha = 0.4f))
        RiskLevel.SAFE -> Triple(StatusSafeContainer, StatusSafeGreen, StatusSafeGreen.copy(alpha = 0.4f))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
            .testTag("card_report_${report.reportId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Date & Severity Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AgriGreenContainer,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = report.cropName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, badgeBorder)
                ) {
                    Text(
                        text = if (isHindi) report.severityLevel.labelHi else report.severityLevel.labelEn,
                        color = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Image Preview & Condition Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Image Box
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AgriGreenContainer)
                ) {
                    val file = File(report.imagePath)
                    if (file.exists()) {
                        AsyncImage(
                            model = file,
                            contentDescription = "Crop Leaf Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Fallback icon visual for presets / samples
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    when (report.severityLevel) {
                                        RiskLevel.ACTION_NEEDED -> StatusActionContainer
                                        RiskLevel.WATCH -> StatusWatchContainer
                                        RiskLevel.SAFE -> StatusSafeContainer
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = when (report.severityLevel) {
                                        RiskLevel.ACTION_NEEDED -> Icons.Default.Warning
                                        RiskLevel.WATCH -> Icons.Default.Info
                                        RiskLevel.SAFE -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = badgeText,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = if (report.isLiveGeminiResponse) "Gemini AI" else "Sample",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeText
                                )
                            }
                        }
                    }

                    // Confidence tag overlay
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "${report.confidencePercent}%",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Condition Title & Summary Excerpt
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi && report.detectedConditionHi.isNotEmpty())
                            report.detectedConditionHi
                        else
                            report.detectedCondition,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isHindi && report.summaryTextHi.isNotEmpty())
                            report.summaryTextHi
                        else
                            report.summaryText,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Microclimate Telemetry Snapshot Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = WaterBlueContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = WaterBlue,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${report.soilMoisturePercent.toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WaterBlue
                                )
                            }
                        }

                        Surface(
                            color = HarvestGoldContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Thermostat,
                                    contentDescription = null,
                                    tint = HarvestGold,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${report.ambientTempC.toInt()}°C",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarvestGold
                                )
                            }
                        }

                        if (report.isLiveGeminiResponse) {
                            Surface(
                                color = AgriGreenContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AgriGreenPrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "AI Verified",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SurfaceBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Treatment Preview & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Treatment teaser
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi && report.recommendedTreatmentHi.isNotEmpty())
                            report.recommendedTreatmentHi
                        else
                            report.recommendedTreatment,
                        fontSize = 11.sp,
                        color = AgriGreenDark,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Report",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (isHindi) "विवरण देखें >" else "Details >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ReportDetailSheetContent(
    report: CropAnalysisReport,
    isHindi: Boolean,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, dd MMMM yyyy • hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(report.timestamp) { dateFormat.format(Date(report.timestamp)) }

    val (badgeBg, badgeText, badgeBorder) = when (report.severityLevel) {
        RiskLevel.ACTION_NEEDED -> Triple(StatusActionContainer, StatusActionRed, StatusActionRed.copy(alpha = 0.4f))
        RiskLevel.WATCH -> Triple(StatusWatchContainer, StatusWatchAmber, StatusWatchAmber.copy(alpha = 0.4f))
        RiskLevel.SAFE -> Triple(StatusSafeContainer, StatusSafeGreen, StatusSafeGreen.copy(alpha = 0.4f))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isHindi) "फसल निदान विस्तृत रिपोर्ट" else "Detailed Diagnostic Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Image Preview if local file exists
        val file = File(report.imagePath)
        if (file.exists()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = file,
                    contentDescription = "Full Leaf Scan",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Path: ${file.name}",
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Diagnosis Summary Card
        OutlinedCard(
            colors = CardDefaults.outlinedCardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, badgeBorder),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isHindi) report.severityLevel.labelHi else report.severityLevel.labelEn,
                            color = badgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = AgriGreenContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${report.confidencePercent}% Confidence",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isHindi && report.detectedConditionHi.isNotEmpty())
                        report.detectedConditionHi
                    else
                        report.detectedCondition,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isHindi && report.summaryTextHi.isNotEmpty())
                        report.summaryTextHi
                    else
                        report.summaryText,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Prescribed Treatment Card
        Card(
            colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "अनुशंसित जैविक / रासायनिक उपचार" else "Prescribed Agronomic Treatment",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isHindi && report.recommendedTreatmentHi.isNotEmpty())
                        report.recommendedTreatmentHi
                    else
                        report.recommendedTreatment,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Telemetry Context Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isHindi) "मिट्टी नमी" else "Soil Moisture", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${report.soilMoisturePercent}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WaterBlue)
                }
            }

            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isHindi) "तापमान" else "Air Temp", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${report.ambientTempC}°C", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HarvestGold)
                }
            }

            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SurfaceBorder),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = if (isHindi) "आर्द्रता" else "Humidity", fontSize = 10.sp, color = TextMuted)
                    Text(text = "${report.humidityPercent}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Delete Report Action
        OutlinedButton(
            onClick = onDelete,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusActionRed),
            border = BorderStroke(1.dp, StatusActionRed.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_delete_report_sheet")
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isHindi) "डेटाबेस से यह रिपोर्ट हटाएं" else "Delete Report from Local Storage", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
