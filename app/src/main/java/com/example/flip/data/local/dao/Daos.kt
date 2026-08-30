package com.example.flip.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flip.data.local.entity.ActionRecordEntity
import com.example.flip.data.local.entity.AdvisoryEntity
import com.example.flip.data.local.entity.FieldEntity
import com.example.flip.data.local.entity.ProduceBatchEntity
import com.example.flip.data.local.entity.SensorReadingEntity
import com.example.flip.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields ORDER BY name ASC")
    fun getAllFields(): Flow<List<FieldEntity>>

    @Query("SELECT * FROM fields WHERE fieldId = :fieldId LIMIT 1")
    fun getFieldById(fieldId: String): Flow<FieldEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateField(field: FieldEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fields: List<FieldEntity>)
}

@Dao
interface SensorDao {
    @Query("SELECT * FROM sensor_readings WHERE fieldId = :fieldId ORDER BY timestamp DESC LIMIT 1")
    fun getLatestReading(fieldId: String): Flow<SensorReadingEntity?>

    @Query("SELECT * FROM sensor_readings WHERE fieldId = :fieldId ORDER BY timestamp DESC LIMIT 50")
    fun getReadingHistory(fieldId: String): Flow<List<SensorReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: SensorReadingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(readings: List<SensorReadingEntity>)
}

@Dao
interface AdvisoryDao {
    @Query("SELECT * FROM advisories WHERE fieldId = :fieldId ORDER BY timestamp DESC")
    fun getAdvisoriesForField(fieldId: String): Flow<List<AdvisoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvisory(advisory: AdvisoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(advisories: List<AdvisoryEntity>)

    @Query("UPDATE advisories SET isActionTaken = 1, actionTakenTimestamp = :timestamp WHERE advisoryId = :advisoryId")
    suspend fun markActionTaken(advisoryId: String, timestamp: Long)
}

@Dao
interface ActionDao {
    @Query("SELECT * FROM action_records WHERE fieldId = :fieldId ORDER BY executedAt DESC")
    fun getActionsForField(fieldId: String): Flow<List<ActionRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ActionRecordEntity)

    @Update
    suspend fun updateAction(action: ActionRecordEntity)
}

@Dao
interface ProduceDao {
    @Query("SELECT * FROM produce_batches ORDER BY harvestDate DESC")
    fun getAllBatches(): Flow<List<ProduceBatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: ProduceBatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(batches: List<ProduceBatchEntity>)
}

@Dao
interface SyncDao {
    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING_SYNC' ORDER BY timestamp ASC")
    suspend fun getPendingSyncItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueueItem(item: SyncQueueEntity)

    @Query("UPDATE sync_queue SET status = 'SYNCED' WHERE queueId = :queueId")
    suspend fun markSynced(queueId: String)
}
