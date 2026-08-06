package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ServiceEntity
import com.example.ui.components.ReceiptDialog
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryOrange
import com.example.ui.viewmodel.AppViewModel

@Composable
fun ServiceDetailScreen(
    service: ServiceEntity,
    viewModel: AppViewModel,
    onBackClick: () -> Unit,
    onApplicationSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUserName by viewModel.userName.collectAsState()
    val currentUserMobile by viewModel.userMobile.collectAsState()
    val currentUserEmail by viewModel.userEmail.collectAsState()

    var nameInput by remember { mutableStateOf(currentUserName) }
    var mobileInput by remember { mutableStateOf(currentUserMobile) }
    var emailInput by remember { mutableStateOf(currentUserEmail) }
    var selectedPaymentMode by remember { mutableStateOf("UPI / QR Code") }

    // Uploaded Documents state
    val uploadedDocs = remember {
        mutableStateListOf(
            Pair("Aadhaar_Card_Front_Back.pdf", "PDF (1.4 MB)"),
            Pair("Passport_Photo_Scan.jpg", "JPG (450 KB)")
        )
    }

    var showReceiptDialog by remember { mutableStateOf(false) }
    val activeDetailApp by viewModel.activeDetailApp.collectAsState()

    val totalFee = service.serviceFee + service.govtFee

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Service Application",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Service Title Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = service.categoryId.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryOrange
                )
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Expected Time", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.7f))
                        Text(service.processingDays, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Fee (Govt + Cafe)", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.7f))
                        Text("₹${totalFee.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = SecondaryOrange)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Official Portal Notice Disclaimer
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = service.officialNotice,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Document Upload Section
        Text(
            text = "1. Upload Required Documents (Max 20MB)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Required: ${service.requiredDocsJson}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Document Dropzone Card
        Card(
            onClick = {
                uploadedDocs.add(
                    Pair("Document_Scan_${uploadedDocs.size + 1}.pdf", "PDF (820 KB)")
                )
                Toast.makeText(context, "Document added to application upload pool", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, PrimaryBlue.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap to Pick or Drop Files (PDF, JPG, PNG)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Supports up to 20MB high resolution scan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Uploaded File List
        uploadedDocs.forEachIndexed { index, (fileName, fileInfo) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = fileName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(text = fileInfo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { uploadedDocs.removeAt(index) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Customer Details Form
        Text(
            text = "2. Applicant Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("Full Applicant Name (As per Aadhaar)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = mobileInput,
            onValueChange = { mobileInput = it },
            label = { Text("Mobile Number (For SMS Updates)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email Address (Optional)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Payment System Options
        Text(
            text = "3. Payment Method",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        val paymentModes = listOf("UPI / QR Code", "PhonePe", "Google Pay", "Paytm", "Razorpay / Card")
        paymentModes.forEach { mode ->
            Card(
                onClick = { selectedPaymentMode = mode },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPaymentMode == mode) PrimaryBlue.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(
                        1.dp,
                        if (selectedPaymentMode == mode) PrimaryBlue else MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPaymentMode == mode,
                        onClick = { selectedPaymentMode = mode },
                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mode,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedPaymentMode == mode) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Interactive QR Code Preview
        AnimatedVisibility(visible = selectedPaymentMode == "UPI / QR Code") {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan UPI QR at Cafe counter or pay online",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "UPI ID: vishwakarmacafe@upi",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Notice
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "256-Bit SSL Encrypted & Secure Auto-Receipt Generation",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit & Pay Button
        Button(
            onClick = {
                if (nameInput.isBlank() || mobileInput.isBlank()) {
                    Toast.makeText(context, "Please enter applicant name and mobile number", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val generatedAppId = viewModel.submitApplication(
                    service = service,
                    custName = nameInput,
                    custMobile = mobileInput,
                    custEmail = emailInput,
                    uploadedDocs = uploadedDocs.map { Pair(it.first, "PDF") },
                    paymentMode = selectedPaymentMode
                )

                showReceiptDialog = true
                onApplicationSubmitted(generatedAppId)
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SecondaryOrange),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Pay ₹${totalFee.toInt()} & Submit Application",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showReceiptDialog && activeDetailApp != null) {
        ReceiptDialog(
            application = activeDetailApp!!,
            onDismiss = {
                showReceiptDialog = false
                onBackClick()
            }
        )
    }
}
