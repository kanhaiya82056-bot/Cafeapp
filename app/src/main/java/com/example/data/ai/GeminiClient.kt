package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
You are AI Mitra, an expert digital citizen guide for Vishwakarma Internet Cafe (CSC VLE Digital Service Platform).
Your role is to guide citizens in India regarding government schemes, required documents, application procedures (Aadhaar, PAN, Voter ID, Passport, Income/Caste Certificates, Ayushman Bharat, PM Kisan, PM Vishwakarma, GST, eShram, IRCTC, etc.), and digital service fees.

DISCLAIMER MANDATE:
Always clarify when helpful: "Vishwakarma Internet Cafe provides independent form-filling, document verification, and submission assistance. Final verification and processing are handled by official government departments."

Be polite, accurate, concise, and structured (use bullet points where appropriate). Keep responses encouraging and clear.
    """

    suspend fun askGemini(userPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineFallbackResponse(userPrompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", userPrompt)
                            })
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", SYSTEM_PROMPT)
                        })
                    })
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getOfflineFallbackResponse(userPrompt)
                }

                val responseBodyStr = response.body?.string() ?: ""
                val jsonResp = JSONObject(responseBodyStr)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCand = candidates.getJSONObject(0)
                    val content = firstCand.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", getOfflineFallbackResponse(userPrompt))
                    }
                }
                getOfflineFallbackResponse(userPrompt)
            }
        } catch (e: Exception) {
            getOfflineFallbackResponse(userPrompt)
        }
    }

    private fun getOfflineFallbackResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("pan") -> """
📌 **PAN Card Required Documents & Process:**
- **Required Documents:**
  1. Aadhaar Card (with mobile linked)
  2. Passport size Photograph
  3. Specimen Signature Scan
- **Processing Time:** 2 to 4 Working Days for e-PAN; 7-10 Days for physical card dispatch.
- **Service Charge:** ₹100 Cafe Fee + ₹107 Government Fee.
- **How Vishwakarma Cafe helps:** We fill your NSDL/UTIITSL application, scan & compress your photo/signature to exact portal specs, and verify biometric eKYC.
            """.trimIndent()

            lower.contains("income") || lower.contains("caste") || lower.contains("residence") -> """
📜 **State Certificate Guidance (Income / Caste / Domicile):**
- **Required Documents:**
  1. Applicant Aadhaar Card
  2. Ration Card or Electricity Bill
  3. Father's Caste Proof / Land Khatian (for Caste Cert)
  4. Income Affidavit / Salary Slip (for Income Cert)
- **Processing Time:** 5 to 7 Working Days via State Revenue e-District portal.
- **Service Charge:** ₹80 - ₹100 Cafe Fee + ₹15 Govt Fee.
- **How Vishwakarma Cafe helps:** We prepare affidavit templates, scan supporting documents, and file your e-District application with real-time status tracking.
            """.trimIndent()

            lower.contains("vishwakarma") || lower.contains("pm vishwakarma") -> """
⚒️ **PM Vishwakarma Scheme Highlights:**
- **Eligibility:** Artisans & Craftsmen working in 18 traditional trades (Carpenters, Blacksmiths, Tailors, Goldsmiths, Potters, Masons, Barbers, etc.).
- **Benefits:**
  1. Free skill training with ₹500/day stipend
  2. ₹15,000 Tool Kit E-Voucher
  3. Collateral-free credit support up to ₹3 Lakh at 5% interest rate
- **Required Documents:** Aadhaar, Bank Passbook, Active Mobile Number.
- **How Vishwakarma Cafe helps:** We register your trade profile on the official PM Vishwakarma portal and verify Gram Panchayat/ULB submission.
            """.trimIndent()

            lower.contains("ayushman") -> """
🏥 **Ayushman Bharat (PM-JAY) Golden Card:**
- **Benefits:** Cashless health cover up to ₹5,000,000 per family per year for secondary and tertiary care hospitalization.
- **Required Documents:** Aadhaar Card + Ration Card or SECC-2011 Name Listing.
- **Fee:** FREE at government hospitals; ₹40 Cafe processing fee for instant PVC printing & e-KYC.
            """.trimIndent()

            else -> """
💡 **Vishwakarma Cafe Digital Citizen Guidance:**
- **Services Supported:** Aadhaar Assistance, PAN Card, Voter ID, Passport, Income & Caste Certificates, Ayushman Card, PM Kisan, PM Vishwakarma, GST Filing, ITR, IRCTC Railway Booking, & Utility Bill Payments.
- **How To Proceed:** Select a service from our catalog, upload your documents securely, complete payment, and receive a digital receipt instantly!
- **Note:** Vishwakarma Internet Cafe provides independent form-filling & document processing assistance. Official approvals rest with concerned government departments.
            """.trimIndent()
        }
    }
}
