package com.example.flip.data.local

import androidx.room.TypeConverter
import com.example.flip.domain.model.ActionVerificationStatus
import com.example.flip.domain.model.AdvisoryCategory
import com.example.flip.domain.model.CropStage
import com.example.flip.domain.model.QualityGrade
import com.example.flip.domain.model.RiskLevel
import com.example.flip.domain.model.SellDecision
import com.example.flip.domain.model.SensorQualityStatus
import com.example.flip.domain.model.SoilType
import com.example.flip.domain.model.WaterStatus

class DataConverters {
    @TypeConverter
    fun fromCropStage(value: CropStage): String = value.name

    @TypeConverter
    fun toCropStage(value: String): CropStage = enumValueOf(value)

    @TypeConverter
    fun fromSoilType(value: SoilType): String = value.name

    @TypeConverter
    fun toSoilType(value: String): SoilType = enumValueOf(value)

    @TypeConverter
    fun fromWaterStatus(value: WaterStatus): String = value.name

    @TypeConverter
    fun toWaterStatus(value: String): WaterStatus = enumValueOf(value)

    @TypeConverter
    fun fromRiskLevel(value: RiskLevel): String = value.name

    @TypeConverter
    fun toRiskLevel(value: String): RiskLevel = enumValueOf(value)

    @TypeConverter
    fun fromSensorQualityStatus(value: SensorQualityStatus): String = value.name

    @TypeConverter
    fun toSensorQualityStatus(value: String): SensorQualityStatus = enumValueOf(value)

    @TypeConverter
    fun fromAdvisoryCategory(value: AdvisoryCategory): String = value.name

    @TypeConverter
    fun toAdvisoryCategory(value: String): AdvisoryCategory = enumValueOf(value)

    @TypeConverter
    fun fromActionVerificationStatus(value: ActionVerificationStatus): String = value.name

    @TypeConverter
    fun toActionVerificationStatus(value: String): ActionVerificationStatus = enumValueOf(value)

    @TypeConverter
    fun fromQualityGrade(value: QualityGrade): String = value.name

    @TypeConverter
    fun toQualityGrade(value: String): QualityGrade = enumValueOf(value)

    @TypeConverter
    fun fromSellDecision(value: SellDecision): String = value.name

    @TypeConverter
    fun toSellDecision(value: String): SellDecision = enumValueOf(value)

    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString("|||")

    @TypeConverter
    fun toStringList(data: String): List<String> = if (data.isEmpty()) emptyList() else data.split("|||")
}
