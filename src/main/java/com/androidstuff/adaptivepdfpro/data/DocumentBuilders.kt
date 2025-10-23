package com.androidstuff.adaptivepdfpro.data

import java.math.BigDecimal
import java.util.Date

/**
 * Builder for creating invoices
 */
class InvoiceBuilder {
    private var header: DocumentHeader? = null
    private val items = mutableListOf<ContentItem.LineItem>()
    private var summary: DocumentSummary? = null
    private var footer: DocumentFooter? = null
    private var styling: ContentStyling = ContentStyling()
    
    fun header(
        businessName: String,
        invoiceNumber: String,
        date: Date = Date(),
        init: DocumentHeader.() -> Unit = {}
    ) = apply {
        header = DocumentHeader(
            businessName = businessName,
            title = "INVOICE",
            documentNumber = invoiceNumber,
            date = date
        ).apply(init)
    }
    
    fun addItem(
        name: String,
        quantity: BigDecimal,
        unitPrice: BigDecimal,
        description: String? = null,
        tax: BigDecimal = BigDecimal.ZERO
    ) = apply {
        items.add(
            ContentItem.LineItem(
                id = items.size.toString(),
                name = name,
                description = description,
                quantity = quantity,
                unitPrice = unitPrice,
                tax = tax
            )
        )
    }
    
    fun summary(
        taxRate: BigDecimal? = null,
        discount: BigDecimal? = null,
        shipping: BigDecimal? = null,
        currency: String = "USD"
    ) = apply {
        val subtotal = items.sumOf { 
            it.quantity.multiply(it.unitPrice)
        }
        val tax = if (taxRate != null) {
            subtotal.multiply(taxRate).divide(BigDecimal(100))
        } else {
            items.sumOf { it.tax }
        }
        val total = subtotal.add(tax).subtract(discount ?: BigDecimal.ZERO).add(shipping ?: BigDecimal.ZERO)
        
        summary = DocumentSummary(
            subtotal = subtotal,
            discount = discount,
            tax = tax,
            taxRate = taxRate,
            shipping = shipping,
            total = total,
            currency = currency
        )
    }
    
    fun footer(
        paymentTerms: String? = null,
        bankDetails: BankDetails? = null,
        notes: String? = null
    ) = apply {
        footer = DocumentFooter(
            text = notes,
            paymentInfo = if (paymentTerms != null || bankDetails != null) {
                PaymentInfo(
                    paymentTerms = paymentTerms,
                    bankDetails = bankDetails
                )
            } else null
        )
    }
    
    fun styling(init: ContentStyling.() -> Unit) = apply {
        styling = styling.apply(init)
    }
    
    fun build(): PdfContentData {
        return PdfContentData(
            documentType = DocumentType.INVOICE,
            header = header,
            items = items.toList(),
            summary = summary,
            footer = footer,
            styling = styling
        )
    }
}

/**
 * Builder for creating receipts
 */
class ReceiptBuilder {
    private var header: DocumentHeader? = null
    private val items = mutableListOf<ContentItem>()
    private var summary: DocumentSummary? = null
    private var footer: DocumentFooter? = null
    
    fun header(
        storeName: String,
        receiptNumber: String,
        date: Date = Date(),
        cashier: String? = null
    ) = apply {
        val customFields = mutableMapOf<String, String>()
        cashier?.let { customFields["Cashier"] = it }
        
        header = DocumentHeader(
            businessName = storeName,
            title = "RECEIPT",
            documentNumber = receiptNumber,
            date = date,
            customFields = customFields
        )
    }
    
    fun addItem(
        description: String,
        quantity: Int,
        unitPrice: BigDecimal
    ) = apply {
        items.add(
            ContentItem.LineItem(
                id = items.size.toString(),
                name = description,
                quantity = BigDecimal(quantity),
                unitPrice = unitPrice
            )
        )
    }
    
    fun addPayment(
        method: PaymentType,
        amount: BigDecimal,
        reference: String? = null
    ) = apply {
        items.add(
            ContentItem.KeyValuePairs(
                pairs = listOf(
                    "Payment Method" to method.name.replace("_", " "),
                    "Amount" to amount.toString()
                ).plus(
                    reference?.let { listOf("Reference" to it) } ?: emptyList()
                ),
                title = "Payment Details"
            )
        )
    }
    
    fun summary(
        taxRate: BigDecimal? = null,
        currency: String = "USD"
    ) = apply {
        val lineItems = items.filterIsInstance<ContentItem.LineItem>()
        val subtotal = lineItems.sumOf { 
            it.quantity.multiply(it.unitPrice)
        }
        val tax = taxRate?.let {
            subtotal.multiply(it).divide(BigDecimal(100))
        }
        val total = subtotal.add(tax ?: BigDecimal.ZERO)
        
        summary = DocumentSummary(
            subtotal = subtotal,
            tax = tax,
            taxRate = taxRate,
            total = total,
            currency = currency
        )
    }
    
    fun footer(thankYouMessage: String = "Thank you for your purchase!") = apply {
        footer = DocumentFooter(
            text = thankYouMessage
        )
    }
    
    fun build(): PdfContentData {
        return PdfContentData(
            documentType = DocumentType.RECEIPT,
            header = header,
            items = items,
            summary = summary,
            footer = footer
        )
    }
}

/**
 * Builder for creating transaction reports
 */
class TransactionReportBuilder {
    private var header: DocumentHeader? = null
    private val transactions = mutableListOf<ContentItem.Transaction>()
    private var summary: DocumentSummary? = null
    private var groupBy: ((ContentItem.Transaction) -> String)? = null
    
    fun header(
        title: String,
        subtitle: String? = null,
        dateRange: Pair<Date, Date>? = null
    ) = apply {
        val customFields = mutableMapOf<String, String>()
        dateRange?.let {
            customFields["Period"] = "${formatDate(it.first)} - ${formatDate(it.second)}"
        }
        
        header = DocumentHeader(
            businessName = "",
            title = title,
            subtitle = subtitle,
            date = Date(),
            customFields = customFields
        )
    }
    
    fun addTransaction(
        date: Date,
        description: String,
        amount: BigDecimal,
        category: String? = null,
        reference: String? = null
    ) = apply {
        transactions.add(
            ContentItem.Transaction(
                id = transactions.size.toString(),
                date = date,
                description = description,
                amount = amount,
                category = category,
                reference = reference
            )
        )
    }
    
    fun addTransactions(transactions: List<ContentItem.Transaction>) = apply {
        this.transactions.addAll(transactions)
    }
    
    fun groupByCategory() = apply {
        groupBy = { it.category ?: "Uncategorized" }
    }
    
    fun groupByDate() = apply {
        groupBy = { formatDate(it.date) }
    }
    
    fun summary(
        showTotal: Boolean = true,
        showCount: Boolean = true,
        showAverage: Boolean = false,
        currency: String = "USD"
    ) = apply {
        val total = transactions.sumOf { it.amount }
        val customCalcs = mutableListOf<CalculationItem>()
        
        if (showCount) {
            customCalcs.add(
                CalculationItem(
                    label = "Total Transactions",
                    value = BigDecimal(transactions.size),
                    type = CalculationType.NONE,
                    showCurrency = false
                )
            )
        }
        
        if (showAverage && transactions.isNotEmpty()) {
            customCalcs.add(
                CalculationItem(
                    label = "Average",
                    value = total.divide(BigDecimal(transactions.size), 2, java.math.RoundingMode.HALF_UP),
                    type = CalculationType.NONE,
                    showCurrency = true
                )
            )
        }
        
        summary = DocumentSummary(
            total = if (showTotal) total else null,
            currency = currency,
            customCalculations = customCalcs
        )
    }
    
    fun build(): PdfContentData {
        val items = mutableListOf<ContentItem>()
        
        if (groupBy != null) {
            // Group transactions
            val grouped = transactions.groupBy { groupBy!!(it) }
            grouped.forEach { (group, groupTransactions) ->
                items.add(
                    ContentItem.TextContent(
                        text = group,
                        type = TextType.HEADING3,
                        style = TextStyle(isBold = true)
                    )
                )
                items.addAll(groupTransactions)
                items.add(
                    ContentItem.KeyValuePairs(
                        pairs = listOf(
                            "Subtotal" to groupTransactions.sumOf { it.amount }.toString()
                        )
                    )
                )
                items.add(ContentItem.Separator())
            }
        } else {
            items.addAll(transactions)
        }
        
        return PdfContentData(
            documentType = DocumentType.TRANSACTION_LIST,
            header = header,
            items = items,
            summary = summary
        )
    }
    
    private fun formatDate(date: Date): String {
        return java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(date)
    }
}

/**
 * Builder for creating reports with tables
 */
class ReportBuilder {
    private var header: DocumentHeader? = null
    private val sections = mutableListOf<ReportSection>()
    private var footer: DocumentFooter? = null
    
    data class ReportSection(
        val title: String?,
        val content: List<ContentItem>
    )
    
    fun header(
        title: String,
        subtitle: String? = null,
        author: String? = null,
        date: Date = Date()
    ) = apply {
        val customFields = mutableMapOf<String, String>()
        author?.let { customFields["Author"] = it }
        
        header = DocumentHeader(
            businessName = "",
            title = title,
            subtitle = subtitle,
            date = date,
            customFields = customFields
        )
    }
    
    fun addSection(
        title: String? = null,
        init: SectionBuilder.() -> Unit
    ) = apply {
        val builder = SectionBuilder()
        builder.init()
        sections.add(ReportSection(title, builder.items))
    }
    
    inner class SectionBuilder {
        val items = mutableListOf<ContentItem>()
        
        fun paragraph(text: String) {
            items.add(
                ContentItem.TextContent(
                    text = text,
                    type = TextType.PARAGRAPH
                )
            )
        }
        
        fun heading(text: String, level: Int = 2) {
            items.add(
                ContentItem.TextContent(
                    text = text,
                    type = when (level) {
                        1 -> TextType.HEADING1
                        2 -> TextType.HEADING2
                        3 -> TextType.HEADING3
                        else -> TextType.HEADING2
                    }
                )
            )
        }
        
        fun table(
            headers: List<String>,
            rows: List<List<String>>,
            showTotals: Boolean = false,
            totals: List<String>? = null
        ) {
            items.add(
                ContentItem.TableData(
                    headers = headers,
                    rows = rows,
                    showTotals = showTotals,
                    totals = totals
                )
            )
        }
        
        fun keyValue(vararg pairs: Pair<String, String>) {
            items.add(
                ContentItem.KeyValuePairs(
                    pairs = pairs.toList()
                )
            )
        }
        
        fun chart(
            type: ChartType,
            values: List<Float>,
            labels: List<String>,
            title: String? = null
        ) {
            items.add(
                ContentItem.ChartContent(
                    type = type,
                    data = ChartData(
                        type = type,
                        values = values,
                        labels = labels
                    ),
                    title = title
                )
            )
        }
        
        fun separator() {
            items.add(ContentItem.Separator())
        }
    }
    
    fun footer(text: String? = null) = apply {
        footer = DocumentFooter(text = text)
    }
    
    fun build(): PdfContentData {
        val allItems = mutableListOf<ContentItem>()
        
        sections.forEach { section ->
            section.title?.let {
                allItems.add(
                    ContentItem.TextContent(
                        text = it,
                        type = TextType.HEADING2,
                        style = TextStyle(isBold = true)
                    )
                )
            }
            allItems.addAll(section.content)
        }
        
        return PdfContentData(
            documentType = DocumentType.REPORT,
            header = header,
            items = allItems,
            footer = footer
        )
    }
}

/**
 * Extension functions for easy PDF generation
 */
fun invoice(init: InvoiceBuilder.() -> Unit): PdfContentData {
    return InvoiceBuilder().apply(init).build()
}

fun receipt(init: ReceiptBuilder.() -> Unit): PdfContentData {
    return ReceiptBuilder().apply(init).build()
}

fun transactionReport(init: TransactionReportBuilder.() -> Unit): PdfContentData {
    return TransactionReportBuilder().apply(init).build()
}

fun report(init: ReportBuilder.() -> Unit): PdfContentData {
    return ReportBuilder().apply(init).build()
}