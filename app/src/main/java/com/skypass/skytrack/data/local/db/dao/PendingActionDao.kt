package com.skypass.skytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.skypass.skytrack.data.local.db.entity.PendingActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(action: PendingActionEntity): Long

    @Query("SELECT * FROM pending_actions WHERE status != 'FAILED' ORDER BY createdAt ASC")
    suspend fun getPendingActions(): List<PendingActionEntity>

    @Query("SELECT COUNT(*) FROM pending_actions WHERE status = 'PENDING'")
    fun observePendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pending_actions WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int

    @Query("DELETE FROM pending_actions WHERE id = :id")
    suspend fun delete(id: Long)

    @Update
    suspend fun update(action: PendingActionEntity)

    @Query("DELETE FROM pending_actions")
    suspend fun clearAll()
}
