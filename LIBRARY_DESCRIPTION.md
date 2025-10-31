# AdaptivePdfPro - Comprehensive Android PDF Library

[![JitPack](https://jitpack.io/v/samdavid/AdaptivePdfPro.svg)](https://jitpack.io/#samdavid/AdaptivePdfPro)
[![API](https://img.shields.io/badge/API-29%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=29)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.14-blue)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**A powerful, feature-rich Android library for PDF viewing with extensive customization, branding, and data-driven generation capabilities. Built with Jetpack Compose for modern Android development.**

## 🚀 Key Features

### PDF Viewing & Navigation
- 📄 **Multiple PDF Sources**: Uri, File, URL, Assets, ByteArray
- 🔍 **Zoom & Pan**: Smooth gestures with pinch-to-zoom and double-tap
- 📱 **Navigation**: Swipe navigation, page thumbnails, jump to page
- 🔍 **Search**: Full-text search with highlighting
- 🔖 **Bookmarks**: Save and navigate to bookmarks
- 📊 **Page Slider**: Visual page navigation control

### Enterprise Branding
- 🏢 **Logo Integration**: Flexible positioning (header, watermark, footer)
- 🎨 **Custom Watermarks**: Opacity, rotation, and positioning control
- 📋 **Headers & Footers**: Branded page headers and footers with page numbers
- 🏷️ **Title & Subtitle**: Custom document titles with styling
- 💼 **Professional Styling**: Enterprise-grade customization options

### Data-Driven PDF Generation
- 🧾 **Invoice Generation**: Create invoices from business data arrays
- 🧾 **Receipt Creation**: Generate receipts with payment information
- 📊 **Transaction Reports**: Build reports with grouping and summaries  
- 📈 **Business Reports**: Construct multi-section professional reports
- 📋 **Content Types**: Tables, charts, key-value pairs, and text sections

### Advanced Theming
- 🌙 **Theme Presets**: Light, Dark, Sepia, High Contrast themes
- 🎨 **Custom Colors**: Full color scheme customization
- 📱 **Material 3**: Seamless Material Design integration
- 🔄 **Runtime Switching**: Change themes on-the-fly
- 📄 **Per-Document**: Individual theme settings per PDF

### Jetpack Compose First
- ⚡ **100% Compose**: Modern declarative UI implementation
- 🏗️ **Architecture**: MVVM with Repository pattern
- 🔄 **Reactive**: StateFlow for reactive programming
- 🧩 **Modular**: Clean, composable components

## 💼 Perfect For

- **Enterprise Applications** - Comprehensive branding and customization
- **Business Document Viewers** - Invoice, receipt, and report generation
- **E-commerce Apps** - Transaction history and receipt generation
- **Financial Applications** - Statement viewing and report generation
- **Document Management** - Professional PDF viewing with branding

## 🛠 Technical Specifications

| Feature | Details |
|---------|---------|
| **Language** | 100% Kotlin |
| **UI Framework** | Jetpack Compose |
| **Architecture** | MVVM + Repository Pattern |
| **Min SDK** | Android 10 (API 29) |
| **Target SDK** | Android 14 (API 34) |
| **PDF Rendering** | AndroidPdfViewer |
| **Dependencies** | Material 3, Compose BOM, Coroutines |

## 📦 Installation

Add JitPack repository to your project `build.gradle`:

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app `build.gradle`:

```kotlin
dependencies {
    implementation 'com.github.samdavid:AdaptivePdfPro:1.0.0'
}
```

## 🚀 Quick Start

### Basic PDF Viewing

```kotlin
// Simple PDF from assets
PdfViewer.with(this)
    .loadFromAssets("sample.pdf")
    .show()

// PDF with custom branding
PdfViewer.with(this)
    .load(pdfUri)
    .setBranding {
        logo(R.drawable.company_logo, position = LogoPosition.TOP_LEFT)
        title("Company Document")
        subtitle("Confidential")
        watermark("DRAFT", opacity = 0.3f)
    }
    .setTheme {
        darkTheme()
        primaryColor(0xFF1976D2)
    }
    .show()
```

### Data-Driven PDF Generation

```kotlin
// Generate invoice from data
val invoiceData = invoice {
    header("My Business", "INV-001") {
        businessAddress = Address("123 Main St", city = "Anytown", state = "CA")
    }
    addItem("Consulting", BigDecimal("10"), BigDecimal("150.00"))
    addItem("Development", BigDecimal("20"), BigDecimal("75.00"))
    summary(taxRate = BigDecimal("8.5"))
    footer(paymentTerms = "Net 30", notes = "Thank you for your business")
}

// Display in Compose
@Composable
fun InvoiceScreen() {
    PdfContentViewer(data = invoiceData)
}
```

### Advanced Configuration

```kotlin
val config = PdfConfig.build {
    loadFromUrl("https://example.com/document.pdf")
    
    setBranding {
        logo(R.drawable.logo, position = LogoPosition.TOP_LEFT, size = 48.dp)
        title("Professional Document", isBold = true, color = 0xFF1976D2)
        watermark("CONFIDENTIAL", opacity = 0.15f, rotation = -45f)
        header(text = "Internal Use Only", showPageNumber = true)
        footer(text = "© 2024 My Company", copyright = "All Rights Reserved")
    }
    
    setTheme {
        customTheme()
        primaryColor(0xFF2196F3)
        backgroundColor(0xFFF5F5F5)
        enableSystemTheme(true)
    }
    
    setNavigation {
        enableSwipe(true)
        swipeDirection(SwipeDirection.HORIZONTAL)
        showThumbnails(true)
        showPageSlider(true)
        showNavigationButtons(true)
    }
}

PdfViewer(config = config)
```

## 📊 Data-Driven Examples

### Receipt Generation

```kotlin
val receiptData = receipt {
    header("Coffee Shop", "R-12345", cashier = "John Doe")
    addItem("Grande Latte", 2, BigDecimal("4.95"))
    addItem("Muffin", 1, BigDecimal("3.25"))
    addPayment(PaymentType.CREDIT_CARD, BigDecimal("13.15"))
    summary(taxRate = BigDecimal("8.25"))
    footer("Thank you! Come again!")
}

ReceiptViewer(data = receiptData)
```

### Transaction Report

```kotlin
val reportData = transactionReport {
    header("Monthly Sales Report")
    addTransactions(transactionList)
    groupByCategory()
    summary(showTotal = true, showCount = true, showAverage = true)
}

TransactionReportViewer(data = reportData)
```

### Business Report

```kotlin
val businessReport = report {
    header("Q4 2024 Report", author = "Finance Team")
    
    addSection("Executive Summary") {
        paragraph("Strong growth across all segments...")
        keyValue("Revenue" to "$1,250,000", "Growth" to "18%")
    }
    
    addSection("Financial Data") {
        table(
            headers = listOf("Month", "Revenue", "Profit"),
            rows = listOf(
                listOf("Oct", "$400K", "$80K"),
                listOf("Nov", "$450K", "$90K"),
                listOf("Dec", "$400K", "$85K")
            ),
            showTotals = true
        )
    }
    
    footer("Generated by AdaptivePdfPro")
}

PdfContentViewer(data = businessReport)
```

## 🎨 Theming & Customization

### Theme Configuration

```kotlin
// Predefined themes
.setTheme { lightTheme() }
.setTheme { darkTheme() }
.setTheme { sepiaTheme() }
.setTheme { highContrastTheme() }

// Custom theme
.setTheme {
    customTheme()
    primaryColor(0xFF9C27B0)
    backgroundColor(0xFFF3E5F5)
    textColor(0xFF4A148C)
    enableSystemTheme(true)
}
```

### Branding Options

```kotlin
setBranding {
    // Logo configuration
    logo(
        drawable = R.drawable.company_logo,
        position = LogoPosition.TOP_LEFT,
        size = 64.dp,
        opacity = 1.0f
    )
    
    // Title and subtitle
    title(
        text = "Document Title",
        color = 0xFF1976D2,
        isBold = true,
        size = 18.sp
    )
    
    subtitle(
        text = "Subtitle Text",
        color = 0xFF757575,
        size = 14.sp
    )
    
    // Watermark
    watermark(
        text = "CONFIDENTIAL",
        opacity = 0.2f,
        rotation = -30f,
        color = 0xFF9E9E9E
    )
    
    // Headers and footers
    header(
        text = "Header Text",
        showPageNumber = true,
        showDate = true,
        backgroundColor = 0xFF2196F3,
        textColor = 0xFFFFFFFF
    )
    
    footer(
        text = "Footer Text",
        copyright = "© 2024 Company Name",
        showPageNumber = true
    )
}
```

## 🧪 Testing

The library includes comprehensive testing:

### Run Tests

```bash
# Unit tests
./gradlew test

# Integration tests  
./gradlew connectedAndroidTest

# Performance benchmarks
./gradlew connectedBenchmarkAndroidTest
```

### Test Coverage

- ✅ **Unit Tests**: Core functionality, data builders, configuration
- ✅ **Integration Tests**: PDF operations, UI interactions, theming
- ✅ **Performance Tests**: Rendering speed, memory usage, large datasets
- ✅ **Compose Tests**: UI components, navigation, user interactions

## 📱 Sample App

The library includes a comprehensive sample app demonstrating all features:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Sample Features:
- PDF viewing from various sources
- Data generation examples
- Theme switching demos  
- Branding customization
- Interactive testing scenarios

## 🏗️ Architecture

```
AdaptivePdfPro/
├── core/                 # Core PDF rendering and configuration
├── compose/              # Jetpack Compose components
├── data/                 # Data models and document builders
├── branding/             # Branding and customization
├── theme/                # Theming system
├── navigation/           # Navigation and interaction logic
└── sample/              # Sample application
```

### Design Patterns:
- **Builder Pattern**: Configuration and initialization
- **MVVM**: Separation of concerns
- **Repository Pattern**: Data management
- **Composition**: Compose-first architecture

## 🔧 Configuration Options

| Feature | Options |
|---------|---------|
| **PDF Sources** | Uri, File, URL, Assets, ByteArray |
| **Navigation** | Swipe (horizontal/vertical), thumbnails, page slider |
| **Zoom** | Pinch, double-tap, fit-to-width/height/page |
| **Themes** | Light, Dark, Sepia, High Contrast, Custom |
| **Branding** | Logo, watermark, headers, footers, titles |
| **Search** | Full-text, highlighting, case-sensitive |
| **Export** | Share, save, image export |

## 📊 Performance

### Benchmarks (Typical Results):
- **PDF Config Creation**: < 1ms
- **Invoice Generation (50 items)**: < 10ms
- **Large Report (1000+ items)**: < 100ms  
- **Theme Switching**: < 5ms
- **Memory Usage**: < 50MB for typical PDFs

### Optimization Features:
- Lazy loading and progressive rendering
- Intelligent page caching
- Memory management and recycling
- Background processing
- Hardware acceleration support

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines and submit pull requests.

### Development Setup:
1. Clone the repository
2. Open in Android Studio
3. Run tests: `./gradlew test`
4. Run sample app: `./gradlew assembleDebug`

## 📄 License

```
MIT License

Copyright (c) 2024 Sam David

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 📞 Support

- **Documentation**: [Full API Documentation](https://github.com/samdavid/AdaptivePdfPro/wiki)
- **Issues**: [GitHub Issues](https://github.com/samdavid/AdaptivePdfPro/issues)
- **Examples**: [Sample Code](https://github.com/samdavid/AdaptivePdfPro/tree/main/src/main/java/com/androidstuff/adaptivepdfpro/sample)
- **Testing Guide**: [TestPlan.md](TestPlan.md)

---

**Transform your PDF viewing experience with AdaptivePdfPro - where functionality meets beautiful design.** ⭐

*Made with ❤️ using Jetpack Compose*