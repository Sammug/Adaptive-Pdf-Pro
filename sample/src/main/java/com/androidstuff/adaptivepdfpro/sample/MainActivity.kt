package com.androidstuff.adaptivepdfpro.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidstuff.adaptivepdfpro.data.*

/**
 * Sample Activity demonstrating AdaptivePdfPro library
 */
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
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
        val context = LocalContext.current
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "AdaptivePdfPro Demo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Choose a PDF template to preview and generate",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // PDF Templates List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getPdfTemplates()) { template ->
                    PdfTemplateItem(
                        template = template,
                        onPreviewClick = {
                            // Navigate to PDF preview activity
                            val intent = Intent(context, com.androidstuff.adaptivepdfpro.sample.PdfPreviewActivity::class.java)
                            intent.putExtra("TEMPLATE_TYPE", template.type)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
    
    @Composable
    private fun PdfTemplateItem(
        template: PdfTemplate,
        onPreviewClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilePresent,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = onPreviewClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Preview")
                }
            }
        }
    }
    
    private fun getPdfTemplates(): List<PdfTemplate> {
        return listOf(
            PdfTemplate(
                type = "goods_received_report",
                title = "Goods Received Report",
                description = "Warehouse inventory report with product details and summaries",
                icon = "📦"
            ),
            PdfTemplate(
                type = "invoice",
                title = "Business Invoice",
                description = "Professional invoice template with company branding",
                icon = "📄"
            ),
            PdfTemplate(
                type = "sales_report",
                title = "Sales Report",
                description = "Monthly sales performance report with charts and metrics",
                icon = "📈"
            ),
            PdfTemplate(
                type = "receipt",
                title = "Sales Receipt",
                description = "Customer receipt template for retail transactions",
                icon = "🧾"
            ),
            PdfTemplate(
                type = "financial_statement",
                title = "Financial Statement",
                description = "Quarterly financial summary with balance sheets",
                icon = "💰"
            ),
            PdfTemplate(
                type = "employee_report",
                title = "Employee Report",
                description = "HR report with employee data and performance metrics",
                icon = "👥"
            )
        )
    }
}

data class PdfTemplate(
    val type: String,
    val title: String,
    val description: String,
    val icon: String
)