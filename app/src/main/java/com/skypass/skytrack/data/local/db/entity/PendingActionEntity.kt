package com.skypass.skytrack.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_actions")
data class PendingActionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val actionType: String,
    val token: String,
    val payloadJson: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val status: String = "PENDING",
    val lastError: String? = null
)
