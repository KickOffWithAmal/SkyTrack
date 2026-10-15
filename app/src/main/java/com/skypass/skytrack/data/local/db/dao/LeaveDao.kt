package com.skypass.skytrack.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.skypass.skytrack.data.local.db.entity.HolidayCacheEntity
import com.skypass.skytrack.data.local.db.entity.LeaveBalanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.LeaveHistoryCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaveDao {
    @Query("SELECT * FROM leave_history_cache ORDER BY `from` DESC")
    suspend fun getLeaveHistory(): List<LeaveHistoryCacheEntity>

    @Query("SELECT * FROM leave_history_cache ORDER BY `from` DESC")
    fun observeLeaveHistory(): Flow<List<LeaveHistoryCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaves(leaves: List<LeaveHistoryCacheEntity>)

    @Query("DELETE FROM leave_history_cache")
    suspend fun clearLeaveHistory()

    @Transaction
    suspend fun replaceLeaveHistory(leaves: List<LeaveHistoryCacheEntity>) {
        clearLeaveHistory()
        insertLeaves(leaves)
    }

    @Query("SELECT * FROM leave_balance_cache WHERE id = 1 LIMIT 1")
    suspend fun getLeaveBalance(): LeaveBalanceCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveBalance(balance: LeaveBalanceCacheEntity)

    @Query("SELECT * FROM holiday_cache ORDER BY date ASC")
    suspend fun getHolidays(): List<HolidayCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(holidays: List<HolidayCacheEntity>)

    @Query("DELETE FROM holiday_cache")
    suspend fun clearHolidays()

    @Transaction
    suspend fun replaceHolidays(holidays: List<HolidayCacheEntity>) {
        clearHolidays()
        insertHolidays(holidays)
    }
}
