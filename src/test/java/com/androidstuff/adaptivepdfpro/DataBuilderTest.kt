package com.androidstuff.adaptivepdfpro

import com.androidstuff.adaptivepdfpro.data.*
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.util.Date

class DataBuilderTest {
    
    @Test
    fun `test invoice builder`() {
        val invoiceData = invoice {
            header("My Business", "INV-001") {
                businessAddress = Address("123 Main St", city = "Anytown", state = "CA")
                description = "Monthly service invoice"
            }
            addItem("Consulting", BigDecimal("10"), BigDecimal("100.00"))
            addItem("Development", BigDecimal("20"), BigDecimal("75.00"))
            summary(taxRate = BigDecimal("8.5"))
            footer(
                paymentTerms = "Net 30",
                notes = "Thank you for your business"
            )
        }
        
        assertEquals(DocumentType.INVOICE, invoiceData.documentType)
        assertNotNull(invoiceData.header)
        assertEquals("My Business", invoiceData.header?.businessName)
        assertEquals("INV-001", invoiceData.header?.documentNumber)
        assertEquals(2, invoiceData.items.size)
        assertNotNull(invoiceData.summary)
        assertNotNull(invoiceData.footer)
        
        // Verify calculations
        val expectedSubtotal = BigDecimal("2500.00") // 10*100 + 20*75
        assertEquals(0, expectedSubtotal.compareTo(invoiceData.summary?.subtotal))
    }
    
    @Test
    fun `test receipt builder`() {
        val receiptData = receipt {
            header("Coffee Shop", "R-12345", cashier = "John Doe")
            addItem("Cappuccino", 2, BigDecimal("4.50"))
            addItem("Muffin", 1, BigDecimal("3.25"))
            addPayment(PaymentType.CREDIT_CARD, BigDecimal("12.25"))
            summary(taxRate = BigDecimal("8.25"))
            footer("Thank you! Come again!")
        }
        
        assertEquals(DocumentType.RECEIPT, receiptData.documentType)
        assertEquals("Coffee Shop", receiptData.header?.businessName)
        assertEquals("R-12345", receiptData.header?.documentNumber)
        
        // Count line items (excluding payment info)
        val lineItems = receiptData.items.filterIsInstance<ContentItem.LineItem>()
        assertEquals(2, lineItems.size)
        assertEquals("Cappuccino", lineItems[0].name)
        assertEquals(BigDecimal("2"), lineItems[0].quantity)
        assertEquals(BigDecimal("4.50"), lineItems[0].unitPrice)
    }
    
    @Test
    fun `test transaction report builder`() {
        val reportData = transactionReport {
            header("Monthly Sales", "Sales Report", dateRange = Date() to Date())
            addTransaction(Date(), "Sale #1", BigDecimal("100.00"), "Retail")
            addTransaction(Date(), "Sale #2", BigDecimal("250.00"), "Wholesale")
            addTransaction(Date(), "Refund", BigDecimal("-50.00"), "Returns")
            groupByCategory()
            summary(showTotal = true, showCount = true, showAverage = true)
        }
        
        assertEquals(DocumentType.TRANSACTION_LIST, reportData.documentType)
        assertEquals("Monthly Sales", reportData.header?.title)
        assertNotNull(reportData.summary)
        
        // Verify summary calculations
        val totalCalc = reportData.summary?.customCalculations?.find { it.label == "Total Transactions" }
        assertNotNull(totalCalc)
    }
    
    @Test
    fun `test report builder with sections`() {
        val reportData = report {
            header("Annual Report", author = "Finance Team")
            addSection("Executive Summary") {
                paragraph("This report summarizes our annual performance.")
                keyValue("Revenue" to "$1,000,000", "Growth" to "15%")
            }
            addSection("Financial Data") {
                table(
                    headers = listOf("Quarter", "Revenue", "Profit"),
                    rows = listOf(
                        listOf("Q1", "$250,000", "$50,000"),
                        listOf("Q2", "$275,000", "$55,000"),
                        listOf("Q3", "$300,000", "$60,000"),
                        listOf("Q4", "$325,000", "$65,000")
                    ),
                    showTotals = true,
                    totals = listOf("Total", "$1,150,000", "$230,000")
                )
            }
            footer("© 2024 My Company. All rights reserved.")
        }
        
        assertEquals(DocumentType.REPORT, reportData.documentType)
        assertEquals("Annual Report", reportData.header?.title)
        
        // Verify content structure
        val textItems = reportData.items.filterIsInstance<ContentItem.TextContent>()
        val tableItems = reportData.items.filterIsInstance<ContentItem.TableData>()
        val keyValueItems = reportData.items.filterIsInstance<ContentItem.KeyValuePairs>()
        
        assertTrue(textItems.isNotEmpty())
        assertTrue(tableItems.isNotEmpty())
        assertTrue(keyValueItems.isNotEmpty())
        
        // Verify table structure
        val table = tableItems.first()
        assertEquals(3, table.headers.size)
        assertEquals(4, table.rows.size)
        assertTrue(table.showTotals)
        assertEquals(3, table.totals?.size)
    }
    
    @Test
    fun `test content item types`() {
        // Test Transaction
        val transaction = ContentItem.Transaction(
            id = "T001",
            date = Date(),
            description = "Payment received",
            amount = BigDecimal("500.00"),
            category = "Income"
        )
        assertEquals("T001", transaction.id)
        assertEquals("Payment received", transaction.description)
        assertEquals(0, BigDecimal("500.00").compareTo(transaction.amount))
        
        // Test LineItem
        val lineItem = ContentItem.LineItem(
            id = "L001",
            name = "Product A",
            quantity = BigDecimal("5"),
            unitPrice = BigDecimal("20.00"),
            tax = BigDecimal("10.00")
        )
        assertEquals("L001", lineItem.id)
        assertEquals("Product A", lineItem.name)
        assertEquals(0, BigDecimal("5").compareTo(lineItem.quantity))
        
        // Test TableData
        val tableData = ContentItem.TableData(
            headers = listOf("Name", "Value"),
            rows = listOf(listOf("Item 1", "100"), listOf("Item 2", "200"))
        )
        assertEquals(2, tableData.headers.size)
        assertEquals(2, tableData.rows.size)
        assertEquals("Name", tableData.headers[0])
        
        // Test KeyValuePairs
        val keyValuePairs = ContentItem.KeyValuePairs(
            pairs = listOf("Key1" to "Value1", "Key2" to "Value2"),
            title = "Summary"
        )
        assertEquals(2, keyValuePairs.pairs.size)
        assertEquals("Summary", keyValuePairs.title)
        assertEquals("Key1", keyValuePairs.pairs[0].first)
        assertEquals("Value1", keyValuePairs.pairs[0].second)
    }
    
    @Test
    fun `test document styling`() {
        val styling = ContentStyling(
            colorScheme = ColorScheme.PROFESSIONAL,
            baseFontSize = 14f,
            headerStyle = HeaderStyle(
                backgroundColor = 0xFF2196F3.toInt(),
                textColor = 0xFFFFFFFF.toInt(),
                titleSize = 24f
            ),
            itemStyle = ItemStyle(
                fontSize = 12f,
                useAlternateColors = true
            )
        )
        
        assertEquals(ColorScheme.PROFESSIONAL, styling.colorScheme)
        assertEquals(14f, styling.baseFontSize)
        assertEquals(0xFF2196F3.toInt(), styling.headerStyle.backgroundColor)
        assertEquals(24f, styling.headerStyle.titleSize)
        assertTrue(styling.itemStyle.useAlternateColors)
    }
}