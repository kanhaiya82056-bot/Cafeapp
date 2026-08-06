package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val description: String,
    val requiredDocsJson: String, // Comma separated or JSON string
    val serviceFee: Double,
    val govtFee: Double,
    val processingDays: String,
    val isPopular: Boolean = false,
    val isActive: Boolean = true,
    val iconName: String = "description",
    val officialNotice: String = "Assistance provided by Vishwakarma Internet Cafe. Final approval subject to government department verification."
)
