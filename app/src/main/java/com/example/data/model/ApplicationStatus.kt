package com.example.data.model

enum class ApplicationStatus(val label: String, val stepIndex: Int) {
    SUBMITTED("Submitted", 0),
    UNDER_VERIFICATION("Under Verification", 1),
    ACTION_REQUIRED("Action Required", 1),
    APPROVED("Approved & Processing", 2),
    COMPLETED("Completed & Ready", 3),
    REJECTED("Rejected", -1)
}

enum class UserRole(val displayName: String) {
    CUSTOMER("Customer Portal"),
    STAFF("Staff Portal"),
    ADMIN("Admin Management")
}
