package com.skypass.skytrack.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface SkyTrackApi {
    @POST
    suspend fun login(
        @Url endpoint: String,
        @Body request: LoginRequest
    ): LoginResponse

    @POST
    suspend fun startWork(
        @Url endpoint: String,
        @Body request: AttendanceRequest
    ): AttendanceResponse

    @POST
    suspend fun endWork(
        @Url endpoint: String,
        @Body request: AttendanceRequest
    ): AttendanceResponse

    @POST
    suspend fun attendanceToday(
        @Url endpoint: String,
        @Body request: AttendanceRequest
    ): AttendanceResponse

    @POST
    suspend fun submitLeave(
        @Url endpoint: String,
        @Body request: LeaveSubmitRequest
    ): LeaveResponse

    @POST
    suspend fun leaveHistory(
        @Url endpoint: String,
        @Body request: AttendanceRequest
    ): LeaveResponse

    @POST
    suspend fun attendanceMonthly(
        @Url endpoint: String,
        @Body request: MonthlyAttendanceRequest
    ): MonthlyAttendanceResponse
}
