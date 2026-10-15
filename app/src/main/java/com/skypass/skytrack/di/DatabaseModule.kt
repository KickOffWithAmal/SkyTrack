package com.skypass.skytrack.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.skypass.skytrack.data.local.db.SkyTrackDatabase
import com.skypass.skytrack.data.local.db.dao.AttendanceDao
import com.skypass.skytrack.data.local.db.dao.LeaveDao
import com.skypass.skytrack.data.local.db.dao.MonthlyDao
import com.skypass.skytrack.data.local.db.dao.PendingActionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SkyTrackDatabase =
        Room.databaseBuilder(
            context,
            SkyTrackDatabase::class.java,
            "skytrack.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAttendanceDao(database: SkyTrackDatabase): AttendanceDao =
        database.attendanceDao()

    @Provides
    fun provideLeaveDao(database: SkyTrackDatabase): LeaveDao =
        database.leaveDao()

    @Provides
    fun provideMonthlyDao(database: SkyTrackDatabase): MonthlyDao =
        database.monthlyDao()

    @Provides
    fun providePendingActionDao(database: SkyTrackDatabase): PendingActionDao =
        database.pendingActionDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
