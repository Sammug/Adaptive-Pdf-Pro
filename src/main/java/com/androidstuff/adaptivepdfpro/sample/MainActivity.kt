package com.androidstuff.adaptivepdfpro.sample

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.androidstuff.adaptivepdfpro.branding.LogoPosition
import com.androidstuff.adaptivepdfpro.compose.*
import com.androidstuff.adaptivepdfpro.compose.theme.AdaptivePdfTheme
import com.androidstuff.adaptivepdfpro.core.PdfViewer
import com.androidstuff.adaptivepdfpro.data.*
import com.androidstuff.adaptivepdfpro.navigation.SwipeDirection
import com.androidstuff.adaptivepdfpro.theme.ThemeMode
import java.math.BigDecimal
import java.util.Date

/**
 * Sample Activity demonstrating all library features
 */
class MainActivity : ComponentActivity() {
    
    private val pickPdfLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { showPdfViewer(it) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AdaptivePdfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SampleApp()
                }
            }
        }
    }
    
    @Composable
    private fun SampleApp() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "AdaptivePdfPro Demo",
                style = MaterialTheme.typography.headlineMedium
            )
            
            // PDF Viewer Tests
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("PDF Viewer Tests", style = MaterialTheme.typography.titleMedium)
                    
                    Button(
                        onClick = { pickPdfLauncher.launch("application/pdf") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pick PDF File")
                    }
                    
                    Button(
                        onClick = { testAssetPdf() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Asset PDF")
                    }
                    
                    Button(
                        onClick = { testUrlPdf() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test URL PDF")
                    }
                }
            }
            
            // Data Generation Tests
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Data Generation Tests", style = MaterialTheme.typography.titleMedium)
                    
                    Button(
                        onClick = { showSampleInvoice() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Sample Invoice")
                    }
                    
                    Button(
                        onClick = { showSampleReceipt() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Sample Receipt")
                    }
                    
                    Button(
                        onClick = { showTransactionReport() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Transaction Report")
                    }
                    
                    Button(
                        onClick = { showBusinessReport() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Business Report")
                    }
                }
            }
            
            // Theme Tests
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Theme Tests", style = MaterialTheme.typography.titleMedium)
                    
                    Button(
                        onClick = { testLightTheme() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Light Theme")
                    }
                    
                    Button(
                        onClick = { testDarkTheme() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Dark Theme")
                    }
                    
                    Button(
                        onClick = { testSepiaTheme() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Sepia Theme")
                    }
                    
                    Button(
                        onClick = { testCustomTheme() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Custom Theme")
                    }
                }
            }
            
            // Branding Tests
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Branding Tests", style = MaterialTheme.typography.titleMedium)
                    
                    Button(
                        onClick = { testBrandingFeatures() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test All Branding Features")
                    }
                    
                    Button(
                        onClick = { testWatermarkOnly() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Watermark Only")
                    }
                    
                    Button(
                        onClick = { testHeaderFooter() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Test Header & Footer")
                    }
                }
            }
        }
    }
    
    // PDF Viewer Test Methods
    private fun showPdfViewer(uri: Uri) {
        PdfViewer.with(this)
            .load(uri)
            .setBranding {
                logo(android.R.drawable.ic_dialog_info, position = LogoPosition.TOP_RIGHT)
                title("Test Document")
                subtitle("AdaptivePdfPro Demo")
            }
            .setTheme {
                primaryColor(Color.Blue.hashCode())
                lightTheme()
            }
            .setNavigation {
                enableSwipe(true)
                showPageSlider(true)
                showThumbnails(true)
                showNavigationButtons(true)
            }
            .show()
    }
    
    private fun testAssetPdf() {
        // Test with asset PDF (you would put a sample.pdf in assets folder)
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setBranding {
                title("Asset PDF Test")
                watermark("SAMPLE")
            }
            .show()
    }
    
    private fun testUrlPdf() {
        // Test with URL PDF
        PdfViewer.with(this)
            .load("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
            .setBranding {
                title("URL PDF Test")
                header(text = "Downloaded PDF", showPageNumber = true)
            }
            .setNavigation {
                swipeDirection(SwipeDirection.VERTICAL)
            }
            .show()
    }
    
    // Data Generation Test Methods
    @Composable
    private fun showSampleInvoice() {
        val invoiceData = invoice {
            header("Tech Solutions Inc.", "INV-2024-001") {
                businessAddress = Address(
                    "123 Tech Street",
                    city = "San Francisco",
                    state = "CA",
                    postalCode = "94105"
                )
                businessContact = ContactInfo(
                    email = "billing@techsolutions.com",
                    phone = "+1 (555) 123-4567"
                )
                description = "Monthly consulting services"
            }
            addItem("Software Development", BigDecimal("40"), BigDecimal("150.00"))
            addItem("Code Review", BigDecimal("8"), BigDecimal("100.00"))
            addItem("Documentation", BigDecimal("12"), BigDecimal("75.00"))
            summary(
                taxRate = BigDecimal("8.5"),
                discount = BigDecimal("500.00")
            )
            footer(
                paymentTerms = "Net 30 days",
                notes = "Thank you for your business. Payment due within 30 days."
            )
        }
        
        // Show in new activity
        setContent {
            AdaptivePdfTheme {
                PdfContentViewer(data = invoiceData)
            }
        }
    }
    
    @Composable
    private fun showSampleReceipt() {
        ReceiptViewer(
            storeName = "Coffee & More",
            receiptNumber = "R-${System.currentTimeMillis()}",
            items = listOf(
                "Grande Latte" to BigDecimal("4.95"),
                "Blueberry Muffin" to BigDecimal("3.50"),
                "Extra Shot" to BigDecimal("0.75")
            ),
            total = BigDecimal("9.20"),
            paymentMethod = PaymentType.CREDIT_CARD,
            taxRate = BigDecimal("8.25")
        )
    }
    
    @Composable
    private fun showTransactionReport() {
        val transactions = listOf(
            ContentItem.Transaction(
                id = "T001",
                date = Date(),
                description = "Product Sale",
                amount = BigDecimal("299.99"),
                category = "Sales"
            ),
            ContentItem.Transaction(
                id = "T002",
                date = Date(),
                description = "Service Fee",
                amount = BigDecimal("150.00"),
                category = "Services"
            ),
            ContentItem.Transaction(
                id = "T003",
                date = Date(),
                description = "Refund",
                amount = BigDecimal("-50.00"),
                category = "Returns"
            )
        )
        
        TransactionReportViewer(
            title = "Daily Sales Report",
            transactions = transactions,
            groupByCategory = true,
            showSummary = true
        )
    }
    
    @Composable
    private fun showBusinessReport() {
        val reportData = report {
            header("Q4 2024 Business Report", author = "Finance Team")
            
            addSection("Executive Summary") {
                paragraph("This quarter showed strong growth across all business segments.")
                keyValue(
                    "Total Revenue" to "$1,250,000",
                    "Growth Rate" to "18%",
                    "Customer Satisfaction" to "94%"
                )
            }
            
            addSection("Financial Performance") {
                table(
                    headers = listOf("Month", "Revenue", "Expenses", "Profit"),
                    rows = listOf(
                        listOf("October", "$400,000", "$280,000", "$120,000"),
                        listOf("November", "$425,000", "$285,000", "$140,000"),
                        listOf("December", "$425,000", "$275,000", "$150,000")
                    ),
                    showTotals = true,
                    totals = listOf("Total", "$1,250,000", "$840,000", "$410,000")
                )
            }
            
            addSection("Key Metrics") {
                chart(
                    type = ChartType.BAR,
                    values = listOf(120f, 140f, 150f),
                    labels = listOf("Oct", "Nov", "Dec"),
                    title = "Monthly Profit (in thousands)"
                )
            }
            
            footer("Generated automatically by AdaptivePdfPro")
        }
        
        PdfContentViewer(data = reportData)
    }
    
    // Theme Test Methods
    private fun testLightTheme() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setTheme {
                lightTheme()
                primaryColor(0xFF2196F3)
            }
            .show()
    }
    
    private fun testDarkTheme() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setTheme {
                darkTheme()
                primaryColor(0xFF3F51B5)
            }
            .show()
    }
    
    private fun testSepiaTheme() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setTheme {
                sepiaTheme()
            }
            .show()
    }
    
    private fun testCustomTheme() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setTheme {
                themeMode(ThemeMode.CUSTOM)
                primaryColor(0xFF8E24AA)
                backgroundColor(0xFFF3E5F5)
            }
            .show()
    }
    
    // Branding Test Methods
    private fun testBrandingFeatures() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setBranding {
                logo(android.R.drawable.ic_dialog_info, position = LogoPosition.TOP_LEFT)
                title("Branded Document", color = 0xFF1976D2, isBold = true)
                subtitle("Company Confidential", color = 0xFF666666)
                watermark("DRAFT", opacity = 0.15f)
                header(
                    text = "Internal Document",
                    showPageNumber = true,
                    showDate = true
                )
                footer(
                    text = "Confidential & Proprietary",
                    copyright = "© 2024 My Company"
                )
            }
            .show()
    }
    
    private fun testWatermarkOnly() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setBranding {
                watermark("CONFIDENTIAL", opacity = 0.2f, rotation = -30f)
            }
            .show()
    }
    
    private fun testHeaderFooter() {
        PdfViewer.with(this)
            .loadFromAssets("sample.pdf")
            .setBranding {
                header(
                    text = "Document Header",
                    showPageNumber = true,
                    showDate = true,
                    backgroundColor = 0xFF1976D2,
                    textColor = 0xFFFFFFFF
                )
                footer(
                    text = "Document Footer",
                    showPageNumber = true,
                    copyright = "© 2024 All Rights Reserved"
                )
            }
            .show()
    }
}