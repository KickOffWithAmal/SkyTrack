package com.skypass.skytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skypass.skytrack.data.local.db.entity.AttendanceCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_cache WHERE date = :date LIMIT 1")
    suspend fun getAttendance(date: String): AttendanceCacheEntity?

    @Query("SELECT * FROM attendance_cache ORDER BY date DESC LIMIT 1")
    suspend fun getLatestAttendance(): AttendanceCacheEntity?

    @Query("SELECT * FROM attendance_cache WHERE date = :date LIMIT 1")
    fun observeAttendance(date: String): Flow<AttendanceCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: AttendanceCacheEntity)

    @Query("DELETE FROM attendance_cache")
    suspend fun clear()
}
