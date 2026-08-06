package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val txnId: String,
    val applicationId: String,
    val serviceTitle: String,
    val customerName: String,
    val amount: Double,
    val paymentMode: String, // UPI, QR, GPay, PhonePe, Paytm, Razorpay
    val timestamp: Long = System.currentTimeMillis(),
    val receiptNo: String,
    val status: String = "Success"
)
