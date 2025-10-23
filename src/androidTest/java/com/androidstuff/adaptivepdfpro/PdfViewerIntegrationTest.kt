package com.androidstuff.adaptivepdfpro

import android.content.Context
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.androidstuff.adaptivepdfpro.branding.LogoPosition
import com.androidstuff.adaptivepdfpro.compose.PdfViewer
import com.androidstuff.adaptivepdfpro.compose.PdfContentViewer
import com.androidstuff.adaptivepdfpro.compose.theme.AdaptivePdfTheme
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.core.PdfSource
import com.androidstuff.adaptivepdfpro.data.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.Date

@RunWith(AndroidJUnit4::class)
class PdfViewerIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun testPdfViewerWithAssetPdf() {
        val config = PdfConfig.build {
            loadFromAssets("sample.pdf")
            setBranding {
                title("Test PDF")
                subtitle("Integration Test")
            }
            setNavigation {
                enableSwipe(true)
                showNavigationButtons(true)
            }
        }

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfViewer(config = config)
            }
        }

        // Wait for PDF to load
        composeTestRule.waitForIdle()
        
        // Verify navigation buttons are displayed
        composeTestRule.onNodeWithContentDescription("Previous Page").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Next Page").assertIsDisplayed()
    }

    @Test
    fun testInvoiceGeneration() {
        val invoiceData = invoice {
            header("Test Company", "INV-TEST-001") {
                businessAddress = Address("123 Test St", city = "Test City", state = "TS")
            }
            addItem("Test Service", BigDecimal("10"), BigDecimal("100.00"))
            addItem("Test Product", BigDecimal("5"), BigDecimal("50.00"))
            summary(taxRate = BigDecimal("10"))
            footer(paymentTerms = "Net 30")
        }

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfContentViewer(data = invoiceData)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify invoice content is displayed
        composeTestRule.onNodeWithText("Test Company").assertIsDisplayed()
        composeTestRule.onNodeWithText("INV-TEST-001").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Service").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()
    }

    @Test
    fun testReceiptGeneration() {
        val receiptData = receipt {
            header("Test Store", "R-12345")
            addItem("Coffee", 1, BigDecimal("4.50"))
            addItem("Muffin", 2, BigDecimal("3.00"))
            summary(taxRate = BigDecimal("8.25"))
            footer("Thank you!")
        }

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfContentViewer(data = receiptData)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify receipt content
        composeTestRule.onNodeWithText("Test Store").assertIsDisplayed()
        composeTestRule.onNodeWithText("R-12345").assertIsDisplayed()
        composeTestRule.onNodeWithText("Coffee").assertIsDisplayed()
        composeTestRule.onNodeWithText("Thank you!").assertIsDisplayed()
    }

    @Test
    fun testTransactionReportGeneration() {
        val transactions = listOf(
            ContentItem.Transaction(
                id = "T001",
                date = Date(),
                description = "Sale 1",
                amount = BigDecimal("100.00"),
                category = "Sales"
            ),
            ContentItem.Transaction(
                id = "T002", 
                date = Date(),
                description = "Sale 2",
                amount = BigDecimal("150.00"),
                category = "Sales"
            )
        )

        val reportData = transactionReport {
            header("Test Report")
            addTransactions(transactions)
            summary(showTotal = true, showCount = true)
        }

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfContentViewer(data = reportData)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify report content
        composeTestRule.onNodeWithText("Test Report").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sale 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sale 2").assertIsDisplayed()
    }

    @Test
    fun testNavigationInteractions() {
        val config = PdfConfig.build {
            loadFromAssets("sample.pdf")
            setNavigation {
                enableSwipe(true)
                showNavigationButtons(true)
                showPageSlider(true)
            }
        }

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfViewer(config = config)
            }
        }

        composeTestRule.waitForIdle()

        // Test navigation button interactions
        composeTestRule.onNodeWithContentDescription("Next Page").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithContentDescription("Previous Page").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testThemeApplication() {
        val config = PdfConfig.build {
            loadFromAssets("sample.pdf")
            setTheme {
                darkTheme()
            }
        }

        composeTestRule.setContent {
            AdaptivePdfTheme(themeConfig = config.themeConfig) {
                PdfViewer(config = config)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify theme is applied (check for dark theme indicators)
        // This would depend on specific theme implementation details
    }

    @Test
    fun testBrandingElements() {
        val config = PdfConfig.build {
            loadFromAssets("sample.pdf")
            setBranding {
                logo(android.R.drawable.ic_dialog_info, position = LogoPosition.TOP_LEFT)
                title("Branded PDF", isBold = true)
                subtitle("Test Subtitle")
                watermark("TEST")
                header(text = "Header Text", showPageNumber = true)
                footer(text = "Footer Text", copyright = "© 2024")
            }
        }

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfViewer(config = config)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify branding elements are displayed
        composeTestRule.onNodeWithText("Branded PDF").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Subtitle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Header Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("Footer Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("© 2024").assertIsDisplayed()
    }

    @Test
    fun testDataOverlays() {
        val overlayData = PdfContentData(
            header = DocumentHeader(
                businessName = "Test Business",
                title = "Test Document",
                date = Date()
            ),
            items = listOf(
                ContentItem.KeyValuePairs(
                    pairs = listOf("Key1" to "Value1", "Key2" to "Value2"),
                    title = "Test Data"
                ),
                ContentItem.TableData(
                    headers = listOf("Column 1", "Column 2"),
                    rows = listOf(
                        listOf("Row1Col1", "Row1Col2"),
                        listOf("Row2Col1", "Row2Col2")
                    )
                )
            )
        )

        composeTestRule.setContent {
            AdaptivePdfTheme {
                PdfContentViewer(data = overlayData)
            }
        }

        composeTestRule.waitForIdle()
        
        // Verify overlay content
        composeTestRule.onNodeWithText("Test Data").assertIsDisplayed()
        composeTestRule.onNodeWithText("Key1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Value1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Column 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Row1Col1").assertIsDisplayed()
    }
}