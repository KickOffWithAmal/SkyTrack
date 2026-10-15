package com.skypass.skytrack.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skypass.skytrack.data.local.db.dao.AttendanceDao
import com.skypass.skytrack.data.local.db.dao.LeaveDao
import com.skypass.skytrack.data.local.db.dao.MonthlyDao
import com.skypass.skytrack.data.local.db.dao.PendingActionDao
import com.skypass.skytrack.data.local.db.entity.AttendanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.HolidayCacheEntity
import com.skypass.skytrack.data.local.db.entity.LeaveBalanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.LeaveHistoryCacheEntity
import com.skypass.skytrack.data.local.db.entity.MonthlyAttendanceCacheEntity
import com.skypass.skytrack.data.local.db.entity.MonthlyAttendanceSummaryCacheEntity
import com.skypass.skytrack.data.local.db.entity.PendingActionEntity

@Database(
    entities = [
        AttendanceCacheEntity::class,
        LeaveHistoryCacheEntity::class,
        LeaveBalanceCacheEntity::class,
        HolidayCacheEntity::class,
        MonthlyAttendanceCacheEntity::class,
        MonthlyAttendanceSummaryCacheEntity::class,
        PendingActionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SkyTrackDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveDao(): LeaveDao
    abstract fun monthlyDao(): MonthlyDao
    abstract fun pendingActionDao(): PendingActionDao
}
