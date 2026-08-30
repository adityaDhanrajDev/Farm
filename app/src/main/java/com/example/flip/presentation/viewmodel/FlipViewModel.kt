package com.example.flip.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flip.data.local.FlipDatabase
import com.example.flip.data.repository.AiDiagnosticRepositoryImpl
import com.example.flip.data.repository.FarmRepositoryImpl
import com.example.flip.domain.model.ActionRecord
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.CropAnalysisReport
import com.example.flip.domain.model.FieldTwin
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SensorReading
import com.example.flip.domain.repository.AiDiagnosticRepository
import com.example.flip.domain.repository.FarmRepository
import com.example.flip.domain.usecase.ClosedLoopVerificationUseCase
import com.example.flip.domain.usecase.SmartIrrigationUseCase
import com.example.flip.domain.usecase.VoiceAdvisoryUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import com.example.flip.data.remote.gemini.GeminiCropAnalysisService
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class FlipViewModel @JvmOverloads constructor(
    application: Application,
    private val farmRepository: FarmRepository = FarmRepositoryImpl(FlipDatabase.getInstance(application)),
    private val aiRepository: AiDiagnosticRepository = AiDiagnosticRepositoryImpl(),
    private val geminiAnalysisService: GeminiCropAnalysisService = GeminiCropAnalysisService(application),
    private val smartIrrigationEngine: SmartIrrigationUseCase = SmartIrrigationUseCase(),
    private val closedLoopVerifier: ClosedLoopVerificationUseCase = ClosedLoopVerificationUseCase(),
    private val voiceAdvisoryEngine: VoiceAdvisoryUseCase = VoiceAdvisoryUseCase()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FlipUiState(isLoading = true))
    val uiState: StateFlow<FlipUiState> = _uiState.asStateFlow()

    init {
        observeFields()
        observeHotspots()
        observeProduceBatches()
        observeAnalysisReports()
    }

    private fun observeAnalysisReports() {
        farmRepository.getAnalysisReportsStream().onEach { reports ->
            _uiState.update { it.copy(analysisReports = reports) }
        }.launchIn(viewModelScope)
    }


    private fun observeFields() {
        farmRepository.getFieldsStream().onEach { fieldsList ->
            val currentSelected = _uiState.value.selectedField
            val newSelected = if (currentSelected != null) {
                fieldsList.find { it.fieldId == currentSelected.fieldId } ?: fieldsList.firstOrNull()
            } else {
                fieldsList.firstOrNull()
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    fields = fieldsList,
                    selectedField = newSelected
                )
            }

            if (newSelected != null) {
                bindFieldData(newSelected.fieldId)
            }
        }.launchIn(viewModelScope)
    }

    fun selectField(field: FieldTwin) {
        _uiState.update { it.copy(selectedField = field) }
        bindFieldData(field.fieldId)
    }

    private fun bindFieldData(fieldId: String) {
        farmRepository.getLatestTelemetryStream(fieldId).onEach { telemetry ->
            val field = _uiState.value.selectedField
            val irrigationDecision = if (field != null && telemetry != null) {
                smartIrrigationEngine.computeIrrigationSchedule(
                    fieldTwin = field,
                    telemetry = telemetry,
                    forecastRainNext48hMm = if (telemetry.humidityPercent > 85.0) 18.0 else 0.0
                )
            } else null

            _uiState.update {
                it.copy(
                    latestTelemetry = telemetry,
                    currentIrrigationDecision = irrigationDecision
                )
            }
        }.launchIn(viewModelScope)

        farmRepository.getTelemetryHistoryStream(fieldId).onEach { history ->
            _uiState.update { it.copy(telemetryHistory = history) }
        }.launchIn(viewModelScope)

        farmRepository.getActiveAdvisoriesStream(fieldId).onEach { advisories ->
            _uiState.update { it.copy(activeAdvisories = advisories) }
        }.launchIn(viewModelScope)

        farmRepository.getActionHistoryStream(fieldId).onEach { actions ->
            _uiState.update { it.copy(actionHistory = actions) }
        }.launchIn(viewModelScope)

        val forecasts = farmRepository.getPredictionForecasts(fieldId)
        _uiState.update { it.copy(predictionForecasts = forecasts) }
    }

    private fun observeHotspots() {
        farmRepository.getRegionalHotspotsStream().onEach { hotspots ->
            _uiState.update { it.copy(regionalHotspots = hotspots) }
        }.launchIn(viewModelScope)
    }

    private fun observeProduceBatches() {
        farmRepository.getProduceBatchesStream().onEach { batches ->
            _uiState.update { it.copy(produceBatches = batches) }
        }.launchIn(viewModelScope)
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isHindi = !it.isHindi) }
    }

    fun runMultimodalDiagnosis(observedSymptom: String) {
        val field = _uiState.value.selectedField ?: return
        val telemetry = _uiState.value.latestTelemetry ?: SensorReading(
            fieldId = field.fieldId,
            soilMoisturePercent = 24.0,
            soilTempC = 25.0,
            airTempC = 32.0,
            humidityPercent = 60.0,
            rainMm = 0.0,
            leafWetnessHours = 0.0,
            irradianceLux = 65000.0
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isDiagnosing = true, latestDiagnosis = null, expertEscalationSuccess = false) }
            val diagnosis = aiRepository.diagnoseMultimodal(
                fieldTwin = field,
                latestTelemetry = telemetry,
                imageUri = null,
                observedLeafState = observedSymptom
            )
            _uiState.update { it.copy(isDiagnosing = false, latestDiagnosis = diagnosis) }
        }
    }

    /**
     * Executes multimodal Gemini API analysis on a captured and saved crop photo
     */
    fun runGeminiCropImageAnalysis(imagePath: String) {
        val field = _uiState.value.selectedField
        val telemetry = _uiState.value.latestTelemetry
        val imageFile = File(imagePath)

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzingGemini = true,
                    lastCapturedImagePath = imagePath,
                    geminiCropAnalysis = null
                )
            }

            val analysisResult = geminiAnalysisService.analyzeCropImage(
                imageFile = imageFile,
                fieldTwin = field,
                telemetry = telemetry
            )

            // Auto-persist analysis report to local Room database for Gallery view
            val reportEntity = CropAnalysisReport(
                reportId = "REP-${System.currentTimeMillis()}",
                fieldId = field?.fieldId ?: "FIELD-001",
                cropName = field?.cropName ?: "Tomato (टमाटर)",
                imagePath = imagePath,
                detectedCondition = analysisResult.conditionName,
                detectedConditionHi = analysisResult.conditionNameHi,
                severityLevel = when (analysisResult.severity.uppercase()) {
                    "ACTION_NEEDED", "HIGH" -> RiskLevel.ACTION_NEEDED
                    "WATCH", "MEDIUM" -> RiskLevel.WATCH
                    else -> RiskLevel.SAFE
                },
                confidencePercent = analysisResult.confidencePercent,
                summaryText = analysisResult.summaryText,
                summaryTextHi = analysisResult.summaryTextHi,
                recommendedTreatment = analysisResult.prescribedTreatment,
                recommendedTreatmentHi = analysisResult.prescribedTreatmentHi,
                soilMoisturePercent = telemetry?.soilMoisturePercent ?: 24.0,
                ambientTempC = telemetry?.airTempC ?: 31.0,
                humidityPercent = telemetry?.humidityPercent ?: 65.0,
                timestamp = System.currentTimeMillis(),
                isLiveGeminiResponse = !analysisResult.isModelFallback,
                farmerNotes = "Auto-saved AI leaf diagnosis"
            )
            farmRepository.insertAnalysisReport(reportEntity)

            _uiState.update {
                it.copy(
                    isAnalyzingGemini = false,
                    geminiCropAnalysis = analysisResult
                )
            }
        }
    }

    fun saveCropAnalysisReport(report: CropAnalysisReport) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingReport = true) }
            farmRepository.insertAnalysisReport(report)
            _uiState.update {
                it.copy(
                    isSavingReport = false,
                    lastSyncMessage = if (it.isHindi) "विश्लेषण रिपोर्ट डेटाबेस में सुरक्षित हो गई" else "Analysis report saved to local database"
                )
            }
        }
    }

    fun deleteCropAnalysisReport(reportId: String) {
        viewModelScope.launch {
            farmRepository.deleteAnalysisReport(reportId)
            _uiState.update {
                it.copy(
                    lastSyncMessage = if (it.isHindi) "रिपोर्ट सफलतापूर्वक हटा दी गई" else "Report removed from local database"
                )
            }
        }
    }

    fun clearGeminiAnalysis() {
        _uiState.update { it.copy(geminiCropAnalysis = null, isAnalyzingGemini = false) }
    }


    fun escalateToExpert(farmerNote: String = "") {
        val diag = _uiState.value.latestDiagnosis ?: return
        viewModelScope.launch {
            val success = aiRepository.escalateToHumanExpert(
                diagnosisId = diag.diagnosisId,
                fieldId = diag.fieldId,
                farmerNote = farmerNote
            )
            _uiState.update { it.copy(expertEscalationSuccess = success) }
        }
    }

    /**
     * Executes closed-loop irrigation action and triggers sensor verification check
     */
    fun executeIrrigationAction(durationMinutes: Int = 45) {
        val field = _uiState.value.selectedField ?: return
        val currentTelemetry = _uiState.value.latestTelemetry ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingIrrigation = true) }

            val actionId = "ACT-${System.currentTimeMillis() % 10000}"
            val preMoisture = currentTelemetry.soilMoisturePercent
            val expectedBump = 8.5

            val initialRecord = ActionRecord(
                actionId = actionId,
                advisoryId = "ADV-${System.currentTimeMillis() % 1000}",
                fieldId = field.fieldId,
                actionType = "Smart Drip Irrigation ($durationMinutes min)",
                scheduledDurationMinutes = durationMinutes,
                executedAt = System.currentTimeMillis(),
                preActionMoisture = preMoisture,
                postActionMoisture = null,
                expectedDelta = expectedBump,
                actualDelta = null,
                verificationStatus = ActionVerificationStatus.PENDING,
                verificationNote = "Pump activated. Sensor verification scheduled post water infiltration.",
                verificationNoteHi = "मोटर चालू की गई। पानी रिसने के बाद सेंसर द्वारा सत्यापन किया जाएगा।"
            )
            farmRepository.recordAction(initialRecord)

            // Simulate irrigation execution and infiltration
            delay(1500)

            val postMoisture = (preMoisture + expectedBump).coerceAtMost(36.0)
            val updatedTelemetry = currentTelemetry.copy(
                soilMoisturePercent = postMoisture,
                timestamp = System.currentTimeMillis()
            )
            farmRepository.insertSensorReading(updatedTelemetry)

            // Closed-loop verification check
            val verifiedRecord = closedLoopVerifier.verifyIrrigationAction(initialRecord, updatedTelemetry)
            farmRepository.updateActionVerification(
                actionId = actionId,
                status = verifiedRecord.verificationStatus,
                postMoisture = postMoisture,
                note = verifiedRecord.verificationNote,
                noteHi = verifiedRecord.verificationNoteHi
            )

            // Update Field health
            farmRepository.updateFieldTwin(
                field.copy(
                    healthIndex = (field.healthIndex + 5).coerceAtMost(98),
                    activeActionState = "Irrigation Verified: Soil moisture restored to ${postMoisture.toInt()}%",
                    activeActionStateHi = "सिंचाई सत्यापित: मिट्टी में नमी बढ़कर ${postMoisture.toInt()}% हुई"
                )
            )

            _uiState.update {
                it.copy(
                    isExecutingIrrigation = false,
                    actionVerificationResult = verifiedRecord
                )
            }
        }
    }

    fun dismissVerificationDialog() {
        _uiState.update { it.copy(actionVerificationResult = null) }
    }

    fun openVoiceAssistant() {
        _uiState.update { it.copy(isVoiceModalOpen = true, voiceQueryText = "", voiceResponse = null) }
    }

    fun closeVoiceAssistant() {
        _uiState.update { it.copy(isVoiceModalOpen = false, isListeningVoice = false) }
    }

    fun submitVoiceQuery(query: String) {
        val field = _uiState.value.selectedField ?: return
        val telemetry = _uiState.value.latestTelemetry ?: SensorReading(
            fieldId = field.fieldId,
            soilMoisturePercent = 25.0,
            soilTempC = 24.0,
            airTempC = 34.0,
            humidityPercent = 50.0,
            rainMm = 0.0,
            leafWetnessHours = 0.0,
            irradianceLux = 60000.0
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isListeningVoice = true, voiceQueryText = query) }
            delay(500)
            val response = voiceAdvisoryEngine.processFarmerVoiceQuery(query, field, telemetry)
            _uiState.update { it.copy(isListeningVoice = false, voiceResponse = response) }
        }
    }

    fun triggerSimulation(scenarioType: String) {
        val field = _uiState.value.selectedField ?: return
        viewModelScope.launch {
            farmRepository.triggerSimulationScenario(field.fieldId, scenarioType)
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, lastSyncMessage = null) }
            farmRepository.syncOfflineData()
            delay(600)
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    lastSyncMessage = if (it.isHindi) "सभी डेटा स्थानीय रूप से सुरक्षित व सिंक है" else "All telemetry & actions synchronized securely"
                )
            }
        }
    }
}
