package com.androidstuff.adaptivepdfpro.data

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import java.util.Date

/**
 * Main data class for PDF content generation
 */
data class PdfContentData(
    val documentType: DocumentType = DocumentType.CUSTOM,
    val header: DocumentHeader? = null,
    val items: List<ContentItem> = emptyList(),
    val summary: DocumentSummary? = null,
    val footer: DocumentFooter? = null,
    val metadata: DocumentMetadata = DocumentMetadata(),
    val styling: ContentStyling = ContentStyling()
)

/**
 * Document types
 */
enum class DocumentType {
    INVOICE,
    RECEIPT,
    REPORT,
    TRANSACTION_LIST,
    STATEMENT,
    QUOTATION,
    PURCHASE_ORDER,
    DELIVERY_NOTE,
    CUSTOM
}

/**
 * Document header with business information
 */
data class DocumentHeader(
    val businessLogo: Any? = null, // Can be resource ID, Bitmap, or URL
    val businessName: String,
    val businessAddress: Address? = null,
    val businessContact: ContactInfo? = null,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val documentNumber: String? = null,
    val date: Date = Date(),
    val customFields: Map<String, String> = emptyMap()
)

/**
 * Content item that can represent various types of data
 */
sealed class ContentItem {
    /**
     * Transaction item for financial documents
     */
    data class Transaction(
        val id: String,
        val date: Date,
        val description: String,
        val amount: BigDecimal,
        val quantity: Int = 1,
        val unitPrice: BigDecimal? = null,
        val tax: BigDecimal? = null,
        val discount: BigDecimal? = null,
        val category: String? = null,
        val reference: String? = null,
        val notes: String? = null,
        val customFields: Map<String, Any> = emptyMap()
    ) : ContentItem()
    
    /**
     * Product/Service line item
     */
    data class LineItem(
        val id: String,
        val name: String,
        val description: String? = null,
        val quantity: BigDecimal,
        val unit: String? = null,
        val unitPrice: BigDecimal,
        val discount: BigDecimal = BigDecimal.ZERO,
        val discountType: DiscountType = DiscountType.FIXED,
        val tax: BigDecimal = BigDecimal.ZERO,
        val taxType: TaxType = TaxType.PERCENTAGE,
        val total: BigDecimal? = null,
        val imageUrl: String? = null,
        val customFields: Map<String, Any> = emptyMap()
    ) : ContentItem()
    
    /**
     * Table data for reports
     */
    data class TableData(
        val headers: List<String>,
        val rows: List<List<String>>,
        val columnWidths: List<Float>? = null,
        val alignment: List<ColumnAlignment> = emptyList(),
        val showTotals: Boolean = false,
        val totals: List<String>? = null
    ) : ContentItem()
    
    /**
     * Text content for descriptions or notes
     */
    data class TextContent(
        val text: String,
        val type: TextType = TextType.PARAGRAPH,
        val style: TextStyle = TextStyle()
    ) : ContentItem()
    
    /**
     * Chart/Graph data
     */
    data class ChartContent(
        val type: ChartType,
        val data: ChartData,
        val title: String? = null,
        val showLegend: Boolean = true,
        val showValues: Boolean = true
    ) : ContentItem()
    
    /**
     * Key-value pairs for summary information
     */
    data class KeyValuePairs(
        val pairs: List<Pair<String, String>>,
        val title: String? = null,
        val style: KeyValueStyle = KeyValueStyle()
    ) : ContentItem()
    
    /**
     * Image content
     */
    data class ImageContent(
        val source: Any, // URL, Resource ID, or Bitmap
        val caption: String? = null,
        val width: Float? = null,
        val height: Float? = null,
        val alignment: ContentAlignment = ContentAlignment.CENTER
    ) : ContentItem()
    
    /**
     * Separator/Divider
     */
    data class Separator(
        val style: SeparatorStyle = SeparatorStyle.LINE,
        val thickness: Float = 1f,
        @ColorInt val color: Int = 0xFFE0E0E0.toInt()
    ) : ContentItem()
    
    /**
     * Custom view content
     */
    data class CustomContent(
        val data: Any,
        val renderer: ((Any) -> Unit)? = null
    ) : ContentItem()
}

/**
 * Document summary with calculations
 */
data class DocumentSummary(
    val subtotal: BigDecimal? = null,
    val discount: BigDecimal? = null,
    val discountLabel: String = "Discount",
    val tax: BigDecimal? = null,
    val taxLabel: String = "Tax",
    val taxRate: BigDecimal? = null,
    val shipping: BigDecimal? = null,
    val shippingLabel: String = "Shipping",
    val total: BigDecimal? = null,
    val totalLabel: String = "Total",
    val amountPaid: BigDecimal? = null,
    val amountDue: BigDecimal? = null,
    val currency: String = "USD",
    val currencySymbol: String = "$",
    val notes: String? = null,
    val customCalculations: List<CalculationItem> = emptyList()
)

/**
 * Custom calculation item
 */
data class CalculationItem(
    val label: String,
    val value: BigDecimal,
    val type: CalculationType = CalculationType.ADD,
    val showCurrency: Boolean = true
)

enum class CalculationType {
    ADD, SUBTRACT, NONE
}

/**
 * Document footer
 */
data class DocumentFooter(
    val text: String? = null,
    val terms: String? = null,
    val paymentInfo: PaymentInfo? = null,
    val signature: SignatureInfo? = null,
    val customContent: List<FooterContent> = emptyList()
)

/**
 * Payment information
 */
data class PaymentInfo(
    val methods: List<PaymentMethod> = emptyList(),
    val bankDetails: BankDetails? = null,
    val paymentTerms: String? = null,
    val dueDate: Date? = null,
    val instructions: String? = null
)

/**
 * Payment method
 */
data class PaymentMethod(
    val type: PaymentType,
    val details: String,
    val icon: Any? = null
)

enum class PaymentType {
    CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CHECK, PAYPAL, CRYPTO, OTHER
}

/**
 * Bank details
 */
data class BankDetails(
    val bankName: String,
    val accountName: String,
    val accountNumber: String,
    val routingNumber: String? = null,
    val swift: String? = null,
    val iban: String? = null,
    val additionalInfo: Map<String, String> = emptyMap()
)

/**
 * Signature information
 */
data class SignatureInfo(
    val signatureImage: Any? = null,
    val signerName: String? = null,
    val signerTitle: String? = null,
    val date: Date? = null,
    val showLine: Boolean = true
)

/**
 * Footer content
 */
data class FooterContent(
    val type: FooterContentType,
    val content: String,
    val alignment: ContentAlignment = ContentAlignment.CENTER
)

enum class FooterContentType {
    TEXT, LEGAL, COPYRIGHT, WEBSITE, EMAIL, PHONE, SOCIAL_MEDIA
}

/**
 * Document metadata
 */
data class DocumentMetadata(
    val author: String? = null,
    val subject: String? = null,
    val keywords: List<String> = emptyList(),
    val creator: String = "AdaptivePdfPro",
    val producer: String = "AdaptivePdfPro Library",
    val creationDate: Date = Date(),
    val modificationDate: Date = Date(),
    val customProperties: Map<String, String> = emptyMap()
)

/**
 * Address information
 */
data class Address(
    val street1: String,
    val street2: String? = null,
    val city: String,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null
) {
    fun formatted(): String {
        return buildString {
            append(street1)
            street2?.let { append("\n$it") }
            append("\n$city")
            state?.let { append(", $it") }
            postalCode?.let { append(" $it") }
            country?.let { append("\n$it") }
        }
    }
}

/**
 * Contact information
 */
data class ContactInfo(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val fax: String? = null,
    val website: String? = null,
    val additionalInfo: Map<String, String> = emptyMap()
)

/**
 * Content styling configuration
 */
data class ContentStyling(
    val colorScheme: ColorScheme = ColorScheme.DEFAULT,
    val fontFamily: String? = null,
    val baseFontSize: Float = 12f,
    val lineHeight: Float = 1.5f,
    val margins: Margins = Margins(),
    val headerStyle: HeaderStyle = HeaderStyle(),
    val itemStyle: ItemStyle = ItemStyle(),
    val summaryStyle: SummaryStyle = SummaryStyle()
)

/**
 * Color schemes
 */
enum class ColorScheme {
    DEFAULT,
    PROFESSIONAL,
    MODERN,
    CLASSIC,
    VIBRANT,
    MINIMAL,
    CUSTOM
}

/**
 * Margins
 */
data class Margins(
    val top: Float = 20f,
    val right: Float = 20f,
    val bottom: Float = 20f,
    val left: Float = 20f
)

/**
 * Header styling
 */
data class HeaderStyle(
    @ColorInt val backgroundColor: Int = 0xFFF5F5F5.toInt(),
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    val titleSize: Float = 24f,
    val subtitleSize: Float = 16f,
    val logoSize: Float = 64f,
    val showBorder: Boolean = true,
    @ColorInt val borderColor: Int = 0xFFE0E0E0.toInt()
)

/**
 * Item styling
 */
data class ItemStyle(
    @ColorInt val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    @ColorInt val alternateBackgroundColor: Int = 0xFFF9F9F9.toInt(),
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    @ColorInt val borderColor: Int = 0xFFE0E0E0.toInt(),
    val fontSize: Float = 11f,
    val padding: Float = 8f,
    val showBorders: Boolean = true,
    val useAlternateColors: Boolean = true
)

/**
 * Summary styling
 */
data class SummaryStyle(
    @ColorInt val backgroundColor: Int = 0xFFF5F5F5.toInt(),
    @ColorInt val textColor: Int = 0xFF000000.toInt(),
    @ColorInt val totalBackgroundColor: Int = 0xFF2196F3.toInt(),
    @ColorInt val totalTextColor: Int = 0xFFFFFFFF.toInt(),
    val fontSize: Float = 12f,
    val totalFontSize: Float = 14f,
    val showBorder: Boolean = true,
    @ColorInt val borderColor: Int = 0xFFE0E0E0.toInt()
)

/**
 * Text style
 */
data class TextStyle(
    @ColorInt val color: Int = 0xFF000000.toInt(),
    val size: Float = 12f,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val alignment: ContentAlignment = ContentAlignment.LEFT
)

/**
 * Key-value style
 */
data class KeyValueStyle(
    @ColorInt val keyColor: Int = 0xFF666666.toInt(),
    @ColorInt val valueColor: Int = 0xFF000000.toInt(),
    val keySize: Float = 11f,
    val valueSize: Float = 12f,
    val spacing: Float = 8f,
    val showSeparator: Boolean = false
)

/**
 * Enums
 */
enum class DiscountType { FIXED, PERCENTAGE }
enum class TaxType { FIXED, PERCENTAGE }
enum class TextType { HEADING1, HEADING2, HEADING3, PARAGRAPH, CAPTION, LABEL }
enum class ContentAlignment { LEFT, CENTER, RIGHT, JUSTIFY }
enum class ColumnAlignment { LEFT, CENTER, RIGHT }
enum class SeparatorStyle { LINE, DASHED, DOTTED, DOUBLE }