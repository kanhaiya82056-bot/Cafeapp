package com.example.data.model

enum class ServiceCategory(
    val id: String,
    val displayName: String,
    val description: String
) {
    ALL("all", "All Services", "Browse complete digital catalog"),
    IDENTITY_DOCS("identity", "Identity & Documents", "Aadhaar, PAN Card, Voter ID, Passport"),
    CERTIFICATES("certificates", "Certificates", "Caste, Income, Residence, Birth & Death"),
    GOVT_SCHEMES("govt_schemes", "Govt Schemes", "Ayushman, PM Kisan, Vishwakarma, eShram"),
    BUSINESS_SERVICES("business", "Business & Tax", "GST, ITR, Udyam MSME, FSSAI License"),
    BANKING_FINANCIAL("banking", "Banking & Finance", "AEPS, Money Transfer, Balance, Insurance"),
    EDUCATION("education", "Education & Exams", "Scholarships, Admission Forms, Exam Results"),
    TRAVEL("travel", "Travel & Tickets", "Railway IRCTC, Bus, Flight, Hotel Booking"),
    UTILITY("utility", "Bills & Recharge", "Electricity, Gas, Water, Mobile & FASTag"),
    DIGITAL_SERVICES("digital", "Digital & Printing", "Print, Scan, Lamination, Online Forms")
}
