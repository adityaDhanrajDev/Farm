package com.example.flip.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flip.data.local.dao.ActionDao
import com.example.flip.data.local.dao.AdvisoryDao
import com.example.flip.data.local.dao.FieldDao
import com.example.flip.data.local.dao.ProduceDao
import com.example.flip.data.local.dao.SensorDao
import com.example.flip.data.local.dao.SyncDao
import com.example.flip.data.local.entity.ActionRecordEntity
import com.example.flip.data.local.entity.AdvisoryEntity
import com.example.flip.data.local.entity.FieldEntity
import com.example.flip.data.local.entity.ProduceBatchEntity
import com.example.flip.data.local.entity.SensorReadingEntity
import com.example.flip.data.local.entity.SyncQueueEntity

@Database(
    entities = [
        FieldEntity::class,
        SensorReadingEntity::class,
        AdvisoryEntity::class,
        ActionRecordEntity::class,
        ProduceBatchEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataConverters::class)
abstract class FlipDatabase : RoomDatabase() {
    abstract fun fieldDao(): FieldDao
    abstract fun sensorDao(): SensorDao
    abstract fun advisoryDao(): AdvisoryDao
    abstract fun actionDao(): ActionDao
    abstract fun produceDao(): ProduceDao
    abstract fun syncDao(): SyncDao

    companion object {
        @Volatile
        private var INSTANCE: FlipDatabase? = null

        fun getInstance(context: Context): FlipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlipDatabase::class.java,
                    "flip_farm_intelligence.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
