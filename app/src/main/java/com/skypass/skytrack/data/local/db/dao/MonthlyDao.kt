package com.skypass.skytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.skypass.skytrack.data.local.db.entity.MonthlyAttendanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.MonthlyAttendanceSummaryCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyDao {
    @Query("SELECT * FROM monthly_attendance_cache WHERE month = :month ORDER BY date ASC")
    suspend fun getMonthlyAttendance(month: String): List<MonthlyAttendanceCacheEntity>

    @Query("SELECT * FROM monthly_attendance_summary_cache WHERE month = :month LIMIT 1")
    suspend fun getMonthlySummary(month: String): MonthlyAttendanceSummaryCacheEntity?

    @Query("SELECT * FROM monthly_attendance_summary_cache WHERE month = :month LIMIT 1")
    fun observeMonthlySummary(month: String): Flow<MonthlyAttendanceSummaryCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyAttendance(list: List<MonthlyAttendanceCacheEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlySummary(summary: MonthlyAttendanceSummaryCacheEntity)

    @Query("DELETE FROM monthly_attendance_cache WHERE month = :month")
    suspend fun clearAttendanceForMonth(month: String)

    @Transaction
    suspend fun replaceMonthlyData(
        month: String,
        summary: MonthlyAttendanceSummaryCacheEntity?,
        attendance: List<MonthlyAttendanceCacheEntity>
    ) {
        clearAttendanceForMonth(month)
        if (summary != null) {
            insertMonthlySummary(summary)
        }
        if (attendance.isNotEmpty()) {
            insertMonthlyAttendance(attendance)
        }
    }
}
