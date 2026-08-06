package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiClient
import com.example.data.local.AppDatabase
import com.example.data.local.ApplicationEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.DocumentEntity
import com.example.data.local.NoticeEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.ServiceEntity
import com.example.data.model.UserRole
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db)
    }

    // Role state
    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    // Customer profile state
    val userName = MutableStateFlow("Rajesh Kumar Sharma")
    val userMobile = MutableStateFlow("+91 98765 43210")
    val userEmail = MutableStateFlow("rajesh.sharma@gmail.com")
    val isOtpVerified = MutableStateFlow(true)

    fun loginWithOtp(mobile: String, name: String) {
        userMobile.value = mobile
        if (name.isNotBlank()) userName.value = name
        isOtpVerified.value = true
    }

    // Search & Category filter state
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("all")

    val filteredServices: StateFlow<List<ServiceEntity>> = combine(
        repository.allServices,
        searchQuery,
        selectedCategory
    ) { services, query, category ->
        services.filter { service ->
            val matchesCat = category == "all" || service.categoryId == category
            val matchesQuery = query.isBlank() ||
                    service.title.contains(query, ignoreCase = true) ||
                    service.description.contains(query, ignoreCase = true) ||
                    service.categoryId.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularServices: StateFlow<List<ServiceEntity>> = repository.popularServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApplications: StateFlow<List<ApplicationEntity>> = repository.allApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerApplications: StateFlow<List<ApplicationEntity>> = combine(
        repository.allApplications,
        userMobile
    ) { apps, mobile ->
        apps.filter { it.customerMobile == mobile || mobile.isBlank() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<NoticeEntity>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Service Selection for Application form
    val selectedServiceForApply = MutableStateFlow<ServiceEntity?>(null)

    // Tracker App ID state
    val activeTrackerAppId = MutableStateFlow("VKC-2026-8891")

    // Selected Application for Detail/Receipt view
    val activeDetailApp = MutableStateFlow<ApplicationEntity?>(null)

    // AI Loading State
    val aiIsLoading = MutableStateFlow(false)

    // Submit new Application Flow
    fun submitApplication(
        service: ServiceEntity,
        custName: String,
        custMobile: String,
        custEmail: String,
        uploadedDocs: List<Pair<String, String>>, // Name to Type
        paymentMode: String
    ): String {
        val rand4Digit = Random.nextInt(1000, 9999)
        val appId = "VKC-2026-$rand4Digit"
        val totalFee = service.serviceFee + service.govtFee
        val txnId = "TXN" + System.currentTimeMillis().toString().takeLast(8)
        val receiptNo = "REC-VKC-" + Random.nextInt(100, 999)

        val newApp = ApplicationEntity(
            id = appId,
            serviceId = service.id,
            serviceTitle = service.title,
            categoryId = service.categoryId,
            customerName = if (custName.isNotBlank()) custName else userName.value,
            customerMobile = if (custMobile.isNotBlank()) custMobile else userMobile.value,
            customerEmail = if (custEmail.isNotBlank()) custEmail else userEmail.value,
            status = "Submitted",
            submissionDate = System.currentTimeMillis(),
            targetCompletionDate = service.processingDays,
            totalFee = totalFee,
            isPaid = true,
            paymentTxnId = txnId,
            receiptNo = receiptNo,
            remarks = "Application received at Vishwakarma Cafe. Awaiting document verification by Staff.",
            docsUploadedCount = uploadedDocs.size,
            staffAssigned = "Operator #1"
        )

        viewModelScope.launch {
            repository.submitApplication(newApp)

            // Insert documents
            for ((docName, docType) in uploadedDocs) {
                repository.addDocument(
                    DocumentEntity(
                        applicationId = appId,
                        docName = docName,
                        docType = docType,
                        fileSizeFormatted = "${Random.nextInt(300, 1800)} KB",
                        status = "Uploaded"
                    )
                )
            }

            // Insert Payment
            repository.recordPayment(
                PaymentEntity(
                    txnId = txnId,
                    applicationId = appId,
                    serviceTitle = service.title,
                    customerName = newApp.customerName,
                    amount = totalFee,
                    paymentMode = paymentMode,
                    receiptNo = receiptNo,
                    status = "Success"
                )
            )

            // Update active tracker and detail app
            activeTrackerAppId.value = appId
            activeDetailApp.value = newApp
        }

        return appId
    }

    // Staff actions
    fun updateStatusByStaff(appId: String, newStatus: String, remarks: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(appId, newStatus, remarks)
            val updated = repository.getApplicationById(appId)
            if (updated != null) {
                activeDetailApp.value = updated
            }
        }
    }

    // AI Chat action
    fun sendAiMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(
                ChatMessageEntity(sender = "User", message = userText)
            )
            aiIsLoading.value = true

            val aiResponse = GeminiClient.askGemini(userText)

            repository.sendChatMessage(
                ChatMessageEntity(sender = "AI", message = aiResponse)
            )
            aiIsLoading.value = false
        }
    }

    // Admin Actions
    fun addNewService(
        title: String,
        categoryId: String,
        desc: String,
        reqDocs: String,
        serviceFee: Double,
        govtFee: Double,
        procDays: String
    ) {
        viewModelScope.launch {
            val id = "srv_" + title.lowercase(Locale.ROOT).replace(" ", "_").take(15) + "_" + Random.nextInt(10, 99)
            val newSrv = ServiceEntity(
                id = id,
                title = title,
                categoryId = categoryId,
                description = desc,
                requiredDocsJson = reqDocs,
                serviceFee = serviceFee,
                govtFee = govtFee,
                processingDays = procDays,
                isPopular = false,
                isActive = true
            )
            repository.insertService(newSrv)
        }
    }

    fun addNewNotice(title: String, desc: String, type: String) {
        viewModelScope.launch {
            repository.addNotice(
                NoticeEntity(
                    title = title,
                    description = desc,
                    type = type,
                    dateText = "Today"
                )
            )
        }
    }
}
