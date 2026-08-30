package com.example.flip.domain.usecase

import com.example.flip.domain.model.ProduceBatch
import com.example.flip.domain.model.QualityGrade
import com.example.flip.domain.model.SellDecision

class SellVsStoreUseCase {

    fun computeSellVsStoreEconomics(batch: ProduceBatch): ProduceBatch {
        // Daily holding cost calculation (cold storage rental + electricity)
        val dailyHoldingCostPerQuintal = batch.storageCostPerDayPerQuintal

        // Spoilage risk penalty index
        val spoilageLossFactor = (batch.spoilageRiskPercent / 100.0) * 0.15

        // Expected future price in 14-30 days
        val netForecastPrice30d = batch.forecastedPrice30Days * (1.0 - spoilageLossFactor) - (dailyHoldingCostPerQuintal * 20)
        val currentPrice = batch.currentMandiPricePerQuintal

        val diffPercent = ((netForecastPrice30d - currentPrice) / currentPrice) * 100.0

        val decision: SellDecision = when {
            batch.spoilageRiskPercent >= 65 -> {
                SellDecision.FIND_IMMEDIATE_BUYER
            }
            diffPercent > 12.0 && batch.qualityGrade == QualityGrade.GRADE_A -> {
                SellDecision.STORE_7_DAYS
            }
            diffPercent > 6.0 && batch.spoilageRiskPercent < 40 -> {
                SellDecision.STORE_3_DAYS
            }
            else -> {
                SellDecision.SELL_NOW
            }
        }

        return batch.copy(
            recommendation = decision,
            netExpectedMarginDiffPercent = diffPercent
        )
    }
}
