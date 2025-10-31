package com.androidstuff.adaptivepdfpro.sample

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.androidstuff.adaptivepdfpro.compose.PdfReportGenerator
import com.androidstuff.adaptivepdfpro.data.*
import java.io.File

/**
 * Activity for previewing and generating PDFs using AdaptivePdfPro library
 */
class PdfPreviewActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val templateType = intent.getStringExtra("TEMPLATE_TYPE") ?: "goods_received_report"
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PdfPreviewScreen(
                        templateType = templateType,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(
    templateType: String,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val pdfData = remember(templateType) { createPdfDataForTemplate(templateType) }
    val templateInfo = getTemplateInfo(templateType)
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = templateInfo.title,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Download Button
                    IconButton(
                        onClick = {
                            generatedPdfFile?.let { file ->
                                downloadPdf(context, file)
                            }
                        },
                        enabled = generatedPdfFile != null
                    ) {
                        Icon(
                            Icons.Default.Download, 
                            contentDescription = "Download PDF",
                            tint = if (generatedPdfFile != null) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    
                    // Share Button
                    IconButton(
                        onClick = {
                            generatedPdfFile?.let { file ->
                                sharePdf(context, file, templateInfo.title)
                            }
                        },
                        enabled = generatedPdfFile != null
                    ) {
                        Icon(
                            Icons.Default.Share, 
                            contentDescription = "Share PDF",
                            tint = if (generatedPdfFile != null) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show instruction if no PDF generated yet
            if (generatedPdfFile == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "🔧 Generate PDF first using the buttons in the preview below to enable download and sharing",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            PdfReportGenerator(
                data = pdfData,
                modifier = Modifier.weight(1f),
                onPdfGenerated = { file: File ->
                    generatedPdfFile = file
                    Toast.makeText(
                        context,
                        "PDF generated successfully: ${file.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onError = { error: Throwable ->
                    Toast.makeText(
                        context,
                        "Error generating PDF: ${error.message ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}

private fun getTemplateInfo(templateType: String): PdfTemplate {
    return when (templateType) {
        "goods_received_report" -> PdfTemplate("goods_received_report", "Goods Received Report", "", "📦")
        "invoice" -> PdfTemplate("invoice", "Business Invoice", "", "📄")
        "sales_report" -> PdfTemplate("sales_report", "Sales Report", "", "📈")
        "receipt" -> PdfTemplate("receipt", "Sales Receipt", "", "🧾")
        "financial_statement" -> PdfTemplate("financial_statement", "Financial Statement", "", "💰")
        "employee_report" -> PdfTemplate("employee_report", "Employee Report", "", "👥")
        else -> PdfTemplate("default", "PDF Document", "", "📄")
    }
}

private fun createPdfDataForTemplate(templateType: String): PdfContentData {
    return when (templateType) {
        "goods_received_report" -> createGoodsReceivedReportData()
        "invoice" -> createInvoiceData()
        "sales_report" -> createSalesReportData()
        "receipt" -> createReceiptData()
        "financial_statement" -> createFinancialStatementData()
        "employee_report" -> createEmployeeReportData()
        else -> createSampleReportData()
    }
}

private fun createGoodsReceivedReportData(): PdfContentData {
    return buildPdfContent {
        // Main header with title on left and company box on right
        header(
            title = "GOODS\nRECEIVED\nREPORT",
            subtitle = "",
            logoResourceId = 1
        ) {
            dataItem("", "Master Chef Limited")
            dataItem("", "Foods Street, Nairobi")
            dataItem("", "foodrest@mail.com")
            dataItem("", "+254 711 223 456")
        }
        
        // Date and location info row with exact styling
        section {
            keyValuePairs(
                listOf(
                    "Received At" to "Main Warehouse",
                    "From" to "01-01-2024",
                    "To" to "31-01-2024"
                ),
                styling = PdfItemStyle(fontSize = 11f, padding = 6f)
            )
            
            // Orange badge with exact colors and borders
            text(
                "Generated On: 14-02-2024 12:30PM",
                styling = PdfItemStyle(
                    fontSize = 10f,
                    backgroundColor = 0xFFFF6A00.toInt(),
                    textColor = 0xFFFFFFFF.toInt(),
                    borderWidth = 1f,
                    borderColor = 0xFFE55A00.toInt(),
                    borderStyle = BorderStyle.SOLID,
                    cornerRadius = 4f,
                    padding = 6f,
                    margin = 4f,
                    alignment = PdfAlignment.RIGHT
                )
            )
            spacer(16f)
        }
        
        // Summary metrics with exact gray background and borders
        section {
            keyValuePairs(
                listOf(
                    "Total New Stock" to "1000",
                    "Total Buying Price" to "KES 400,000.00",
                    "Total Amount" to "KES 480,000.00"
                ),
                styling = PdfItemStyle(
                    backgroundColor = 0xFFF5F5F5.toInt(),
                    borderWidth = 1f,
                    borderColor = 0xFFE0E0E0.toInt(),
                    borderStyle = BorderStyle.SOLID,
                    cornerRadius = 6f,
                    fontSize = 12f,
                    isBold = true,
                    padding = 12f,
                    margin = 8f
                )
            )
            spacer(20f)
        }
        
        // First GRN Section with dotted border exactly like screenshot
        section {
            keyValuePairs(
                listOf(
                    "GRN NO: 0012112" to "Received On: 14-02-2024 12:30PM"
                ),
                styling = PdfItemStyle(
                    fontSize = 11f,
                    padding = 8f,
                    borderWidth = 2f,
                    borderColor = 0xFFCCCCCC.toInt(),
                    borderStyle = BorderStyle.DASHED,
                    backgroundColor = 0xFFFAFAFA.toInt(),
                    cornerRadius = 4f,
                    margin = 4f
                )
            )
            
            text(
                "Received By: John Waweru",
                styling = PdfItemStyle(
                    fontSize = 11f, 
                    padding = 6f,
                    margin = 2f
                )
            )
            
            spacer(8f)
            
            // Product table with alternating row colors and borders
            table(
                headers = listOf("Product", "New Stock", "Buying Price", "Selling Price"),
                rows = listOf(
                    listOf("Wheat Flour 2kg", "100", "KES 100.00", "KES 120.00"),
                    listOf("Maize Flour 2kg", "100", "KES 100.00", "KES 120.00"),
                    listOf("Water 200ml", "100", "KES 100.00", "KES 120.00"),
                    listOf("Fanta 300ml", "100", "KES 100.00", "KES 120.00")
                ),
                styling = PdfItemStyle(
                    fontSize = 11f,
                    padding = 8f,
                    borderWidth = 1f,
                    borderColor = 0xFFDDDDDD.toInt(),
                    borderStyle = BorderStyle.SOLID,
                    alternateRowColor = 0xFFF9F9F9.toInt(),
                    cornerRadius = 2f
                )
            )
            spacer(20f)
        }
        
        // Second GRN Section with same border styling
        section {
            keyValuePairs(
                listOf(
                    "GRN NO: 0012111" to "Received On: 13-02-2024 12:30PM"
                ),
                styling = PdfItemStyle(
                    fontSize = 11f,
                    padding = 8f,
                    borderWidth = 2f,
                    borderColor = 0xFFCCCCCC.toInt(),
                    borderStyle = BorderStyle.DASHED,
                    backgroundColor = 0xFFFAFAFA.toInt(),
                    cornerRadius = 4f,
                    margin = 4f
                )
            )
            
            text(
                "Received By: Edward Chella",
                styling = PdfItemStyle(
                    fontSize = 11f, 
                    padding = 6f,
                    margin = 2f
                )
            )
            
            spacer(8f)
            
            table(
                headers = listOf("Product", "New Stock", "Buying Price", "Selling Price"),
                rows = listOf(
                    listOf("Wheat Flour 2kg", "100", "KES 100.00", "KES 120.00"),
                    listOf("Maize Flour 2kg", "100", "KES 100.00", "KES 120.00"),
                    listOf("Water 200ml", "100", "KES 100.00", "KES 120.00"),
                    listOf("Fanta 300ml", "100", "KES 100.00", "KES 120.00")
                ),
                styling = PdfItemStyle(
                    fontSize = 11f,
                    padding = 8f,
                    borderWidth = 1f,
                    borderColor = 0xFFDDDDDD.toInt(),
                    borderStyle = BorderStyle.SOLID,
                    alternateRowColor = 0xFFF9F9F9.toInt(),
                    cornerRadius = 2f
                )
            )
        }
        
        // Footer section with enhanced styling
        footer {
            leftText("Powered by ZED Payments Limited")
            centerText("info@zed.business")
            rightText("v1.0.2")
            additionalItem("Page", "1 of 1")
        }
    }
}

private fun createInvoiceData(): PdfContentData {
    return buildPdfContent {
        header(
            title = "INVOICE",
            subtitle = "ACME Corporation"
        ) {
            dataItem("Invoice Number", "INV-2024-001")
            dataItem("Date", "February 15, 2024")
            dataItem("Due Date", "March 15, 2024")
            dataItem("Bill To", "Client Company Ltd")
        }
        
        section("Items") {
            table(
                headers = listOf("Description", "Qty", "Rate", "Amount"),
                rows = listOf(
                    listOf("Web Development", "1", "$5,000.00", "$5,000.00"),
                    listOf("UI/UX Design", "1", "$2,500.00", "$2,500.00"),
                    listOf("Project Management", "1", "$1,500.00", "$1,500.00")
                )
            )
        }
        
        section {
            keyValuePairs(
                listOf(
                    "Subtotal" to "$9,000.00",
                    "Tax (10%)" to "$900.00",
                    "Total" to "$9,900.00"
                ),
                styling = PdfItemStyle(isBold = true)
            )
        }
        
        footer {
            centerText("Thank you for your business!")
        }
    }
}

private fun createSalesReportData(): PdfContentData {
    return buildPdfContent {
        header(
            title = "SALES REPORT",
            subtitle = "Q1 2024 Performance"
        ) {
            dataItem("Report Period", "January - March 2024")
            dataItem("Generated", "April 1, 2024")
            dataItem("Department", "Sales")
        }
        
        section("Summary") {
            keyValuePairs(
                listOf(
                    "Total Revenue" to "$125,000",
                    "Growth Rate" to "15.2%",
                    "Units Sold" to "1,250",
                    "Average Deal Size" to "$100"
                )
            )
        }
        
        section("Top Products") {
            table(
                headers = listOf("Product", "Units Sold", "Revenue", "Growth"),
                rows = listOf(
                    listOf("Product A", "500", "$50,000", "+12%"),
                    listOf("Product B", "350", "$35,000", "+18%"),
                    listOf("Product C", "400", "$40,000", "+8%")
                )
            )
        }
    }
}

private fun createReceiptData(): PdfContentData {
    return buildPdfContent {
        header(
            title = "RECEIPT",
            subtitle = "SuperMart Store"
        ) {
            dataItem("Store", "SuperMart - Main Branch")
            dataItem("Receipt #", "REC-240215-001")
            dataItem("Date", "February 15, 2024 2:30 PM")
            dataItem("Cashier", "John Doe")
        }
        
        section("Items Purchased") {
            table(
                headers = listOf("Item", "Qty", "Price", "Total"),
                rows = listOf(
                    listOf("Bread", "2", "$2.50", "$5.00"),
                    listOf("Milk", "1", "$3.99", "$3.99"),
                    listOf("Apples", "3 lbs", "$1.99/lb", "$5.97")
                )
            )
        }
        
        section("Payment") {
            keyValuePairs(
                listOf(
                    "Subtotal" to "$14.96",
                    "Tax" to "$1.20",
                    "Total" to "$16.16",
                    "Paid (Cash)" to "$20.00",
                    "Change" to "$3.84"
                )
            )
        }
        
        footer {
            centerText("Thank you for shopping with us!")
        }
    }
}

private fun createFinancialStatementData(): PdfContentData {
    return buildPdfContent {
        header(
            title = "FINANCIAL STATEMENT",
            subtitle = "ABC Company Ltd"
        ) {
            dataItem("Period", "Year Ended December 31, 2023")
            dataItem("Prepared By", "Financial Department")
            dataItem("Date", "January 15, 2024")
        }
        
        section("Income Statement") {
            keyValuePairs(
                listOf(
                    "Revenue" to "$500,000",
                    "Cost of Goods Sold" to "$300,000",
                    "Gross Profit" to "$200,000",
                    "Operating Expenses" to "$120,000",
                    "Net Income" to "$80,000"
                )
            )
        }
        
        section("Balance Sheet") {
            table(
                headers = listOf("Assets", "Amount"),
                rows = listOf(
                    listOf("Cash", "$50,000"),
                    listOf("Accounts Receivable", "$75,000"),
                    listOf("Inventory", "$100,000"),
                    listOf("Equipment", "$200,000"),
                    listOf("Total Assets", "$425,000")
                )
            )
        }
    }
}

private fun createEmployeeReportData(): PdfContentData {
    return buildPdfContent {
        header(
            title = "EMPLOYEE REPORT",
            subtitle = "HR Department Summary"
        ) {
            dataItem("Report Period", "Q1 2024")
            dataItem("Department", "Human Resources")
            dataItem("Prepared By", "HR Manager")
            dataItem("Date", "April 1, 2024")
        }
        
        section("Employee Statistics") {
            keyValuePairs(
                listOf(
                    "Total Employees" to "125",
                    "New Hires" to "8",
                    "Departures" to "3",
                    "Average Tenure" to "2.5 years"
                )
            )
        }
        
        section("Department Breakdown") {
            table(
                headers = listOf("Department", "Employees", "Open Positions"),
                rows = listOf(
                    listOf("Engineering", "45", "5"),
                    listOf("Sales", "30", "3"),
                    listOf("Marketing", "20", "2"),
                    listOf("HR", "15", "1"),
                    listOf("Operations", "15", "0")
                )
            )
        }
    }
}

private fun createSampleReportData(): PdfContentData {
    return buildPdfContent {
        header(
            title = "SAMPLE DOCUMENT",
            subtitle = "AdaptivePdfPro Demo"
        ) {
            dataItem("Generated", "February 2024")
            dataItem("Library", "AdaptivePdfPro")
        }
        
        section("Sample Content") {
            text("This is a sample PDF document generated using the AdaptivePdfPro library.")
            keyValuePairs(
                listOf(
                    "Feature" to "PDF Generation",
                    "Technology" to "Jetpack Compose",
                    "Platform" to "Android"
                )
            )
        }
    }
}

/**
 * Downloads the PDF file to the device's Downloads folder
 */
private fun downloadPdf(context: android.content.Context, file: File) {
    try {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val fileName = "AdaptivePdfPro_${System.currentTimeMillis()}.pdf"
        val destFile = File(downloadsDir, fileName)
        
        // Copy the file to Downloads directory
        file.copyTo(destFile, overwrite = true)
        
        Toast.makeText(
            context,
            "PDF downloaded to Downloads folder: $fileName",
            Toast.LENGTH_LONG
        ).show()
        
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Failed to download PDF: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Shares the PDF file via various apps (WhatsApp, Email, etc.)
 */
private fun sharePdf(context: android.content.Context, file: File, title: String) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "Generated using AdaptivePdfPro library")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share PDF via...")
        context.startActivity(chooserIntent)
        
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Failed to share PDF: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}