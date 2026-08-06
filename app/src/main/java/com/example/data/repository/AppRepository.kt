package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.ApplicationEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DocumentEntity
import com.example.data.local.NoticeEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.ServiceEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {

    // Services
    val allServices: Flow<List<ServiceEntity>> = db.serviceDao().getAllServices()
    val popularServices: Flow<List<ServiceEntity>> = db.serviceDao().getPopularServices()

    fun getServicesByCategory(catId: String): Flow<List<ServiceEntity>> {
        return if (catId == "all") {
            db.serviceDao().getAllServices()
        } else {
            db.serviceDao().getServicesByCategory(catId)
        }
    }

    suspend fun getServiceById(id: String): ServiceEntity? {
        return db.serviceDao().getServiceById(id)
    }

    suspend fun insertService(service: ServiceEntity) {
        db.serviceDao().insertService(service)
    }

    suspend fun updateService(service: ServiceEntity) {
        db.serviceDao().updateService(service)
    }

    // Applications
    val allApplications: Flow<List<ApplicationEntity>> = db.applicationDao().getAllApplications()

    fun getApplicationsByCustomer(mobile: String): Flow<List<ApplicationEntity>> {
        return db.applicationDao().getApplicationsByCustomer(mobile)
    }

    suspend fun getApplicationById(id: String): ApplicationEntity? {
        return db.applicationDao().getApplicationById(id)
    }

    suspend fun submitApplication(app: ApplicationEntity) {
        db.applicationDao().insertApplication(app)
    }

    suspend fun updateApplicationStatus(appId: String, status: String, remarks: String) {
        db.applicationDao().updateStatus(appId, status, remarks)
    }

    suspend fun updateApplication(app: ApplicationEntity) {
        db.applicationDao().updateApplication(app)
    }

    // Documents
    fun getDocsForApp(appId: String): Flow<List<DocumentEntity>> {
        return db.documentDao().getDocsForApp(appId)
    }

    suspend fun addDocument(doc: DocumentEntity) {
        db.documentDao().insertDocument(doc)
    }

    suspend fun updateDocStatus(docId: Long, status: String) {
        db.documentDao().updateDocStatus(docId, status)
    }

    // Payments
    val allPayments: Flow<List<PaymentEntity>> = db.paymentDao().getAllPayments()

    suspend fun recordPayment(payment: PaymentEntity) {
        db.paymentDao().insertPayment(payment)
    }

    // AI Chat
    val chatMessages: Flow<List<ChatMessageEntity>> = db.chatDao().getChatMessages()

    suspend fun sendChatMessage(msg: ChatMessageEntity) {
        db.chatDao().insertMessage(msg)
    }

    suspend fun clearChat() {
        db.chatDao().clearHistory()
    }

    // Notices
    val allNotices: Flow<List<NoticeEntity>> = db.noticeDao().getAllNotices()

    suspend fun addNotice(notice: NoticeEntity) {
        db.noticeDao().insertNotices(listOf(notice))
    }
}
