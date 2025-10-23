package com.androidstuff.adaptivepdfpro

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.Date

@RunWith(AndroidJUnit4::class)
class PerformanceTest {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun benchmarkPdfConfigCreation() {
        benchmarkRule.measureRepeated {
            PdfConfig.build {
                loadFromAssets("sample.pdf")
                setBranding {
                    title("Performance Test")
                    subtitle("Benchmark")
                    watermark("TEST")
                }
                setTheme {
                    lightTheme()
                    primaryColor(0xFF2196F3)
                }
                setNavigation {
                    enableSwipe(true)
                    showThumbnails(true)
                    showPageSlider(true)
                }
            }
        }
    }

    @Test
    fun benchmarkInvoiceGeneration() {
        benchmarkRule.measureRepeated {
            invoice {
                header("Test Company", "INV-${System.currentTimeMillis()}")
                repeat(50) { index ->
                    addItem("Item $index", BigDecimal("${index + 1}"), BigDecimal("10.00"))
                }
                summary(taxRate = BigDecimal("10"))
            }
        }
    }

    @Test
    fun benchmarkLargeTransactionReport() {
        val transactions = (1..1000).map { index ->
            ContentItem.Transaction(
                id = "T$index",
                date = Date(),
                description = "Transaction $index",
                amount = BigDecimal("${index * 10}"),
                category = "Category ${index % 5}"
            )
        }

        benchmarkRule.measureRepeated {
            transactionReport {
                header("Large Report")
                addTransactions(transactions)
                groupByCategory()
                summary(showTotal = true, showCount = true, showAverage = true)
            }
        }
    }

    @Test
    fun benchmarkComplexReport() {
        benchmarkRule.measureRepeated {
            report {
                header("Complex Report", author = "Test Author")
                
                repeat(10) { sectionIndex ->
                    addSection("Section $sectionIndex") {
                        paragraph("This is paragraph content for section $sectionIndex")
                        
                        table(
                            headers = listOf("Col1", "Col2", "Col3"),
                            rows = (1..20).map { rowIndex ->
                                listOf("Row$rowIndex-Col1", "Row$rowIndex-Col2", "Row$rowIndex-Col3")
                            }
                        )
                        
                        keyValue(*Array(10) { "Key$it" to "Value$it" })
                    }
                }
                
                footer("Generated in performance test")
            }
        }
    }

    @Test
    fun benchmarkThemeCreation() {
        benchmarkRule.measureRepeated {
            repeat(100) {
                com.androidstuff.adaptivepdfpro.theme.ThemeConfig.Builder().apply {
                    lightTheme()
                    primaryColor(0xFF2196F3 + it)
                    backgroundColor(0xFFFFFFFF)
                    enableSystemTheme(false)
                }.build()
            }
        }
    }

    @Test
    fun benchmarkBrandingConfiguration() {
        benchmarkRule.measureRepeated {
            repeat(50) { index ->
                com.androidstuff.adaptivepdfpro.branding.BrandingConfig.Builder().apply {
                    logo(android.R.drawable.ic_dialog_info)
                    title("Title $index")
                    subtitle("Subtitle $index")
                    watermark("WATERMARK $index")
                    header(text = "Header $index", showPageNumber = true)
                    footer(text = "Footer $index", copyright = "© 2024 Test $index")
                }.build()
            }
        }
    }
}