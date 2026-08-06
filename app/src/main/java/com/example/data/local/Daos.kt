package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY isPopular DESC, title ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE categoryId = :catId ORDER BY title ASC")
    fun getServicesByCategory(catId: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isPopular = 1")
    fun getPopularServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :serviceId LIMIT 1")
    suspend fun getServiceById(serviceId: String): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Update
    suspend fun updateService(service: ServiceEntity)
}

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications ORDER BY submissionDate DESC")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE customerMobile = :mobile ORDER BY submissionDate DESC")
    fun getApplicationsByCustomer(mobile: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE id = :appId LIMIT 1")
    suspend fun getApplicationById(appId: String): ApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationEntity)

    @Update
    suspend fun updateApplication(app: ApplicationEntity)

    @Query("UPDATE applications SET status = :status, remarks = :remarks WHERE id = :appId")
    suspend fun updateStatus(appId: String, status: String, remarks: String)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE applicationId = :appId ORDER BY id ASC")
    fun getDocsForApp(appId: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Query("UPDATE documents SET status = :status WHERE id = :docId")
    suspend fun updateDocStatus(docId: Long, status: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface NoticeDao {
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<NoticeEntity>)
}
