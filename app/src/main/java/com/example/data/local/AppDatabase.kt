package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ServiceEntity::class,
        ApplicationEntity::class,
        DocumentEntity::class,
        PaymentEntity::class,
        ChatMessageEntity::class,
        NoticeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun documentDao(): DocumentDao
    abstract fun paymentDao(): PaymentDao
    abstract fun chatDao(): ChatDao
    abstract fun noticeDao(): NoticeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vishwakarma_cafe_db"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(db: AppDatabase) {
                // Populate Services
                val initialServices = listOf(
                    // Identity & Documents
                    ServiceEntity(
                        id = "srv_aadhaar",
                        title = "Aadhaar Card Assistance",
                        categoryId = "identity",
                        description = "Demographic update, mobile linking guidance, address change assistance, PVC Aadhaar order.",
                        requiredDocsJson = "Identity Proof, Address Proof, Active Mobile Number",
                        serviceFee = 50.0,
                        govtFee = 50.0,
                        processingDays = "3-5 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_pan",
                        title = "PAN Card (New / Correction)",
                        categoryId = "identity",
                        description = "Instant e-PAN or Physical PAN application assistance via NSDL / UTIITSL portal.",
                        requiredDocsJson = "Aadhaar Card, Passport Size Photo, Signature Scan",
                        serviceFee = 100.0,
                        govtFee = 107.0,
                        processingDays = "2-4 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_voter",
                        title = "Voter ID Card",
                        categoryId = "identity",
                        description = "New voter enrollment (Form 6), address change (Form 8), and e-EPIC download.",
                        requiredDocsJson = "Aadhaar Card, Age Proof, Passport Photo",
                        serviceFee = 60.0,
                        govtFee = 0.0,
                        processingDays = "7-10 Days",
                        isPopular = false
                    ),
                    ServiceEntity(
                        id = "srv_passport",
                        title = "Passport Application Assistance",
                        categoryId = "identity",
                        description = "Passport Seva Kendra appointment booking, online form filing, and annexure preparation.",
                        requiredDocsJson = "Aadhaar, PAN Card, Bank Passbook, 10th Certificate",
                        serviceFee = 300.0,
                        govtFee = 1500.0,
                        processingDays = "15-20 Days",
                        isPopular = true
                    ),

                    // Certificates
                    ServiceEntity(
                        id = "srv_income_cert",
                        title = "Income Certificate",
                        categoryId = "certificates",
                        description = "State Revenue Department Income Certificate online form application & affidavit preparation.",
                        requiredDocsJson = "Aadhaar Card, Ration Card, Salary Slip / Affidavit, Photo",
                        serviceFee = 100.0,
                        govtFee = 15.0,
                        processingDays = "5-7 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_caste_cert",
                        title = "Caste Certificate (OBC/SC/ST/EWS)",
                        categoryId = "certificates",
                        description = "Application filing for Caste verification certificate for education and government jobs.",
                        requiredDocsJson = "Aadhaar, Father Caste Certificate, Land/Khatian Proof, Photo",
                        serviceFee = 120.0,
                        govtFee = 20.0,
                        processingDays = "7-14 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_residence_cert",
                        title = "Residence / Domicile Certificate",
                        categoryId = "certificates",
                        description = "Official proof of residence issued by Tehsildar / SDO office for scheme benefits.",
                        requiredDocsJson = "Aadhaar, Electricity Bill / Land Slip, Passport Photo",
                        serviceFee = 90.0,
                        govtFee = 15.0,
                        processingDays = "5-7 Days",
                        isPopular = false
                    ),
                    ServiceEntity(
                        id = "srv_birth_cert",
                        title = "Birth Certificate Registration",
                        categoryId = "certificates",
                        description = "Online filing for municipal/Gram Panchayat birth certificate issuance and corrections.",
                        requiredDocsJson = "Hospital Discharge Certificate, Parent Aadhaar Cards",
                        serviceFee = 80.0,
                        govtFee = 25.0,
                        processingDays = "7 Days",
                        isPopular = false
                    ),

                    // Government Schemes
                    ServiceEntity(
                        id = "srv_ayushman",
                        title = "Ayushman Bharat (PM-JAY) Card",
                        categoryId = "govt_schemes",
                        description = "Check eligibility under SECC 2011/Ration Card and generate ₹5 Lakh health card.",
                        requiredDocsJson = "Aadhaar Card, Ration Card, Mobile Number",
                        serviceFee = 40.0,
                        govtFee = 0.0,
                        processingDays = "Instant (Same Day)",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_pm_kisan",
                        title = "PM Kisan Samman Nidhi eKYC & Form",
                        categoryId = "govt_schemes",
                        description = "Biometric eKYC, new farmer registration, and installment status troubleshooting.",
                        requiredDocsJson = "Aadhaar Card, Land Record (LPC/Khatian), Bank Passbook",
                        serviceFee = 50.0,
                        govtFee = 0.0,
                        processingDays = "1-2 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_pm_vishwakarma",
                        title = "PM Vishwakarma Scheme Application",
                        categoryId = "govt_schemes",
                        description = "Tool kit incentive (₹15,000) & collateral-free loan form for traditional artisans & craftsmen.",
                        requiredDocsJson = "Aadhaar Card, Bank Passbook, Artisan Trade Category, Mobile",
                        serviceFee = 70.0,
                        govtFee = 0.0,
                        processingDays = "3-5 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_eshram",
                        title = "eShram Card & Labour Registration",
                        categoryId = "govt_schemes",
                        description = "Unorganized worker national database enrollment for social security pension & accident cover.",
                        requiredDocsJson = "Aadhaar Card, Bank Passbook, Nominee Details",
                        serviceFee = 50.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = false
                    ),

                    // Business & Tax Services
                    ServiceEntity(
                        id = "srv_gst_reg",
                        title = "GST New Registration",
                        categoryId = "business",
                        description = "New Goods and Services Tax Identification Number (GSTIN) application for traders & freelancers.",
                        requiredDocsJson = "PAN Card, Aadhaar, Business Place Rent Agreement/Electricity Bill, Bank Proof",
                        serviceFee = 500.0,
                        govtFee = 0.0,
                        processingDays = "3-7 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_itr_return",
                        title = "Income Tax Return (ITR Filing)",
                        categoryId = "business",
                        description = "ITR-1 / ITR-2 filing for salaried individuals, small business owners, and TDS refund claims.",
                        requiredDocsJson = "Form 16 / Bank Statements, Aadhaar, PAN Card",
                        serviceFee = 350.0,
                        govtFee = 0.0,
                        processingDays = "1-2 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_udyam",
                        title = "Udyam / MSME Registration",
                        categoryId = "business",
                        description = "Free Government MSME Certificate generation for bank loans and government tenders.",
                        requiredDocsJson = "Aadhaar Card, PAN Card, Business Bank Account No",
                        serviceFee = 150.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = false
                    ),

                    // Banking & Finance
                    ServiceEntity(
                        id = "srv_aeps",
                        title = "AEPS Biometric Cash Withdrawal",
                        categoryId = "banking",
                        description = "Withdraw cash from any bank account using Aadhaar fingerprint authentication at kiosk.",
                        requiredDocsJson = "Aadhaar Number & Biometric Fingerprint",
                        serviceFee = 10.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_money_transfer",
                        title = "Domestic Money Transfer (DMT)",
                        categoryId = "banking",
                        description = "Instant IMPS fund transfer to any Indian bank account nationwide.",
                        requiredDocsJson = "Receiver Account Number, IFSC Code, Sender Mobile",
                        serviceFee = 25.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = true
                    ),

                    // Education & Travel
                    ServiceEntity(
                        id = "srv_scholarship",
                        title = "National & State Scholarship Form",
                        categoryId = "education",
                        description = "NSP & Post-Matric state scholarship online form filing & document verification submission.",
                        requiredDocsJson = "Income Cert, Caste Cert, Marksheet, College Admission Fee Receipt, Bank Passbook",
                        serviceFee = 100.0,
                        govtFee = 0.0,
                        processingDays = "2 Days",
                        isPopular = true
                    ),
                    ServiceEntity(
                        id = "srv_railway_ticket",
                        title = "IRCTC Railway Ticket Booking",
                        categoryId = "travel",
                        description = "Authorized IRCTC agent booking for General, Tatkal, and Sleeper/AC train reservations.",
                        requiredDocsJson = "Passenger Names, Age, ID Proof, Journey Date",
                        serviceFee = 40.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = true
                    ),

                    // Utility & Digital
                    ServiceEntity(
                        id = "srv_electricity_bill",
                        title = "Electricity & Water Bill Payment",
                        categoryId = "utility",
                        description = "Instant online bill payment with immediate printable receipt and cash cashback offers.",
                        requiredDocsJson = "Consumer Number / CA Number, Electricity Bill Copy",
                        serviceFee = 15.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = false
                    ),
                    ServiceEntity(
                        id = "srv_print_scan",
                        title = "High-Quality Document Print & Scan",
                        categoryId = "digital",
                        description = "Color/B&W laser printing, document scanning to PDF, and PVC card lamination.",
                        requiredDocsJson = "Digital Document File / Physical Document",
                        serviceFee = 10.0,
                        govtFee = 0.0,
                        processingDays = "Instant",
                        isPopular = false
                    )
                )
                db.serviceDao().insertServices(initialServices)

                // Populate Initial Applications
                val sampleApps = listOf(
                    ApplicationEntity(
                        id = "VKC-2026-8891",
                        serviceId = "srv_pan",
                        serviceTitle = "PAN Card (Correction)",
                        categoryId = "identity",
                        customerName = "Rajesh Kumar Sharma",
                        customerMobile = "+91 98765 43210",
                        customerEmail = "rajesh.sharma@gmail.com",
                        status = "Under Verification",
                        submissionDate = System.currentTimeMillis() - (86400000 * 2),
                        targetCompletionDate = "Tomorrow, 5:00 PM",
                        totalFee = 207.0,
                        isPaid = true,
                        paymentTxnId = "TXN99283811",
                        receiptNo = "REC-VKC-00481",
                        remarks = "Documents received. Verification in progress by Staff Operator.",
                        docsUploadedCount = 3,
                        staffAssigned = "Operator Amit"
                    ),
                    ApplicationEntity(
                        id = "VKC-2026-7734",
                        serviceId = "srv_ayushman",
                        serviceTitle = "Ayushman Bharat Card",
                        categoryId = "govt_schemes",
                        customerName = "Anita Devi",
                        customerMobile = "+91 91234 56789",
                        customerEmail = "anita.devi@outlook.com",
                        status = "Completed",
                        submissionDate = System.currentTimeMillis() - (86400000 * 4),
                        targetCompletionDate = "Completed",
                        totalFee = 40.0,
                        isPaid = true,
                        paymentTxnId = "TXN88291044",
                        receiptNo = "REC-VKC-00392",
                        remarks = "Card generated successfully. Download available in app.",
                        docsUploadedCount = 2,
                        staffAssigned = "Operator Suman"
                    ),
                    ApplicationEntity(
                        id = "VKC-2026-9102",
                        serviceId = "srv_pm_vishwakarma",
                        serviceTitle = "PM Vishwakarma Scheme Form",
                        categoryId = "govt_schemes",
                        customerName = "Manoj Carpenter",
                        customerMobile = "+91 99887 76655",
                        customerEmail = "manoj.artisan@gmail.com",
                        status = "Action Required",
                        submissionDate = System.currentTimeMillis() - (86400000 * 1),
                        targetCompletionDate = "3 Days",
                        totalFee = 70.0,
                        isPaid = true,
                        paymentTxnId = "TXN77102933",
                        receiptNo = "REC-VKC-00512",
                        remarks = "Please re-upload clearer scan of Bank Passbook showing IFSC code.",
                        docsUploadedCount = 2,
                        staffAssigned = "Admin VLE"
                    )
                )
                for (app in sampleApps) {
                    db.applicationDao().insertApplication(app)
                }

                // Sample Documents for Sample Application 1
                db.documentDao().insertDocument(
                    DocumentEntity(
                        applicationId = "VKC-2026-8891",
                        docName = "Aadhaar_Card_Rajesh.pdf",
                        docType = "PDF",
                        fileSizeFormatted = "1.2 MB",
                        status = "Verified"
                    )
                )
                db.documentDao().insertDocument(
                    DocumentEntity(
                        applicationId = "VKC-2026-8891",
                        docName = "Passport_Photo_Rajesh.jpg",
                        docType = "JPG",
                        fileSizeFormatted = "450 KB",
                        status = "Verified"
                    )
                )
                db.documentDao().insertDocument(
                    DocumentEntity(
                        applicationId = "VKC-2026-8891",
                        docName = "Old_PAN_Copy.png",
                        docType = "PNG",
                        fileSizeFormatted = "800 KB",
                        status = "Uploaded"
                    )
                )

                // Sample Notices
                val initialNotices = listOf(
                    NoticeEntity(
                        title = "PM Surya Ghar Muft Bijli Yojana Assistance Open",
                        description = "Get up to ₹78,000 subsidy on solar rooftop installation. Apply at Vishwakarma Cafe today!",
                        type = "Scheme",
                        dateText = "Aug 05, 2026"
                    ),
                    NoticeEntity(
                        title = "Special Income & Caste Certificate Camp",
                        description = "Fast-track processing for student scholarship submissions this week.",
                        type = "Notice",
                        dateText = "Aug 03, 2026"
                    ),
                    NoticeEntity(
                        title = "50% Off Service Charge on IRCTC Railway Tickets",
                        description = "Book your festive train travel tickets early with zero extra convenience fee.",
                        type = "Offer",
                        dateText = "Jul 28, 2026"
                    )
                )
                db.noticeDao().insertNotices(initialNotices)

                // Sample AI Chat welcome message
                db.chatDao().insertMessage(
                    ChatMessageEntity(
                        sender = "AI",
                        message = "Namaste! I am AI Mitra, your Vishwakarma Digital Services Assistant. How can I help you today? You can ask about required documents, government scheme eligibility, or application fees."
                    )
                )
            }
        }
    }
}
