package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val applicationId: String,
    val docName: String,
    val docType: String, // PDF, JPG, PNG
    val fileSizeFormatted: String,
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val status: String = "Uploaded", // Uploaded, Verified, Resubmit Needed
    val localUri: String = ""
)
