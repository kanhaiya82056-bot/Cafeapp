package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey val id: String, // e.g. VKC-2026-8891
    val serviceId: String,
    val serviceTitle: String,
    val categoryId: String,
    val customerName: String,
    val customerMobile: String,
    val customerEmail: String = "",
    val status: String, // Submitted, Under Verification, Action Required, Approved, Completed, Rejected
    val submissionDate: Long = System.currentTimeMillis(),
    val targetCompletionDate: String = "2-3 Working Days",
    val totalFee: Double,
    val isPaid: Boolean = true,
    val paymentTxnId: String = "",
    val receiptNo: String = "",
    val remarks: String = "Application logged in Vishwakarma Citizen Portal queue.",
    val docsUploadedCount: Int = 0,
    val staffAssigned: String = "Operator #1"
)
