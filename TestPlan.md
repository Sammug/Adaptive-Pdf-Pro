# AdaptivePdfPro Testing Guide

## 📋 Complete Testing Strategy

### **1. Unit Tests**
**Location:** `src/test/java/`

#### Core Configuration Tests (`PdfConfigTest.kt`)
- ✅ Builder pattern validation
- ✅ Source type verification (Uri, File, URL, Asset)
- ✅ Branding configuration
- ✅ Theme configuration
- ✅ Navigation settings
- ✅ Default values testing
- ✅ Error handling for missing sources

#### Data Generation Tests (`DataBuilderTest.kt`)  
- ✅ Invoice builder with calculations
- ✅ Receipt generation
- ✅ Transaction report creation
- ✅ Business report with sections
- ✅ Content item types validation
- ✅ Styling configuration
- ✅ Builder pattern consistency

**Run Unit Tests:**
```bash
./gradlew test
./gradlew testDebugUnitTest
```

---

### **2. Integration Tests**  
**Location:** `src/androidTest/java/`

#### PDF Viewer Integration (`PdfViewerIntegrationTest.kt`)
- ✅ Asset PDF loading
- ✅ Invoice generation UI
- ✅ Receipt generation UI  
- ✅ Transaction report UI
- ✅ Navigation interactions
- ✅ Theme application
- ✅ Branding elements display
- ✅ Data overlay rendering

**Run Integration Tests:**
```bash
./gradlew connectedAndroidTest
./gradlew connectedDebugAndroidTest
```

---

### **3. Performance Tests**
**Location:** `src/androidTest/java/`

#### Benchmark Tests (`PerformanceTest.kt`)
- ✅ PDF config creation speed
- ✅ Invoice generation performance
- ✅ Large transaction report (1000+ items)
- ✅ Complex report generation  
- ✅ Theme creation benchmarks
- ✅ Branding configuration speed

**Run Performance Tests:**
```bash
./gradlew connectedBenchmarkAndroidTest
```

---

### **4. Sample App Testing**
**Location:** `src/main/java/.../sample/MainActivity.kt`

#### Interactive Test Features
- **PDF Viewer Tests**
  - Pick PDF file from device
  - Load asset PDF
  - Load URL PDF
  
- **Data Generation Tests**  
  - Generate sample invoice
  - Generate sample receipt
  - Generate transaction report
  - Generate business report
  
- **Theme Tests**
  - Light theme
  - Dark theme  
  - Sepia theme
  - Custom theme
  
- **Branding Tests**
  - All branding features
  - Watermark only
  - Header & footer only

**Build Sample App:**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

---

## 🚀 **How to Test the Library**

### **Quick Start Testing**

1. **Add a test PDF to assets:**
```
src/main/assets/sample.pdf
```

2. **Basic Usage Test:**
```kotlin
// In your Activity
PdfViewer.with(this)
    .loadFromAssets("sample.pdf")
    .setBranding {
        title("Test PDF")
        logo(R.drawable.your_logo)
    }
    .show()
```

3. **Data Generation Test:**
```kotlin
// Generate invoice from data
val invoiceData = invoice {
    header("My Company", "INV-001")
    addItem("Service", BigDecimal("10"), BigDecimal("100"))
    summary(taxRate = BigDecimal("10"))
}

setContent {
    PdfContentViewer(data = invoiceData)
}
```

---

### **Complete Testing Scenarios**

#### **Scenario 1: PDF Viewing**
```kotlin
@Test
fun testPdfViewing() {
    // Test different sources
    testAssetPdf()
    testFilePdf() 
    testUrlPdf()
    testUriPdf()
    
    // Test navigation
    testSwipeNavigation()
    testThumbnails()
    testSearch()
    testBookmarks()
    
    // Test zoom/pan
    testZoomControls()
    testPinchToZoom()
    testDoubleTap()
}
```

#### **Scenario 2: Data-Driven Generation**
```kotlin
@Test
fun testDataGeneration() {
    // Test business documents
    testInvoiceGeneration()
    testReceiptGeneration()
    testTransactionReport()
    testBusinessReport()
    
    // Test complex data
    testLargeDatasets()
    testComplexTables()
    testChartGeneration()
}
```

#### **Scenario 3: Customization**
```kotlin
@Test  
fun testCustomization() {
    // Test branding
    testLogoPositioning()
    testWatermarks()
    testHeadersFooters()
    
    // Test themes
    testThemePresets()
    testCustomColors()
    testRuntimeThemeSwitch()
    
    // Test navigation
    testNavigationModes()
    testCustomButtons()
}
```

#### **Scenario 4: Performance**
```kotlin
@Test
fun testPerformance() {
    // Test large files
    testLargePdfHandling()
    testMemoryUsage()
    testScrollingPerformance()
    
    // Test data generation speed
    testLargeInvoices()
    testBulkReports()
    testComplexBranding()
}
```

---

### **Device Testing Matrix**

| **Test Type** | **Devices** | **Android Versions** |
|---------------|-------------|---------------------|
| **Unit Tests** | Emulator/Device | API 29+ |
| **Integration Tests** | Physical Device | API 29, 31, 34 |
| **Performance Tests** | High-end Device | API 31+ |
| **UI Tests** | Multiple Screen Sizes | API 29+ |

---

### **Test Data Setup**

#### **Sample PDFs**
Place in `src/main/assets/`:
- `sample.pdf` - Simple 5-page PDF
- `large.pdf` - 100+ page PDF  
- `complex.pdf` - PDF with forms/annotations

#### **Sample Data**
```kotlin
val testTransactions = listOf(
    ContentItem.Transaction(
        id = "T001", date = Date(), 
        description = "Sale", amount = BigDecimal("100"),
        category = "Revenue"
    ),
    // ... more test data
)

val testLineItems = listOf(
    ContentItem.LineItem(
        id = "L001", name = "Product A",
        quantity = BigDecimal("5"), 
        unitPrice = BigDecimal("20")
    ),
    // ... more line items
)
```

---

### **Automated Testing Pipeline**

```yaml
# CI/CD Testing
name: AdaptivePdfPro Tests
on: [push, pull_request]

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - name: Unit Tests
        run: ./gradlew test
        
  integration-tests:  
    runs-on: macos-latest
    steps:
      - name: Integration Tests
        run: ./gradlew connectedAndroidTest
        
  performance-tests:
    runs-on: macos-latest  
    steps:
      - name: Performance Tests
        run: ./gradlew connectedBenchmarkAndroidTest
```

---

## ✅ **Testing Checklist**

### **Core Features**
- [ ] PDF rendering from all source types
- [ ] Navigation (swipe, buttons, slider)
- [ ] Zoom/pan functionality
- [ ] Search within PDF
- [ ] Thumbnails grid
- [ ] Bookmarks management

### **Data Generation**  
- [ ] Invoice creation from data arrays
- [ ] Receipt generation
- [ ] Transaction reports
- [ ] Business reports with sections
- [ ] Chart/table rendering
- [ ] Summary calculations

### **Customization**
- [ ] Logo positioning and sizing
- [ ] Watermark overlay
- [ ] Header/footer configuration
- [ ] Theme switching
- [ ] Color customization
- [ ] Font/styling options

### **Performance**
- [ ] Large PDF handling (100+ pages)
- [ ] Memory usage optimization
- [ ] Smooth scrolling/rendering
- [ ] Fast data generation
- [ ] Theme switching speed

### **Edge Cases**
- [ ] Corrupted PDF handling
- [ ] Network connectivity issues
- [ ] Memory constraints
- [ ] Orientation changes
- [ ] Background/foreground transitions

---

## 📊 **Expected Test Results**

### **Performance Benchmarks**
- PDF Config Creation: < 1ms
- Invoice Generation (50 items): < 10ms  
- Large Report (1000+ items): < 100ms
- Theme Switching: < 5ms
- Memory Usage: < 50MB for typical PDFs

### **Quality Gates**
- Unit Test Coverage: > 90%
- Integration Test Pass Rate: 100%
- Performance Regression: < 5%
- Memory Leaks: 0
- Crash Rate: < 0.1%

The testing suite provides comprehensive coverage of all library features with both automated and manual testing approaches for production-ready quality assurance.