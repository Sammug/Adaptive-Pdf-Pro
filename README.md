# AdaptivePdfPro

A comprehensive Android library for PDF viewing, generation, and manipulation with extensive customization options including branding, theming, navigation, and data overlay features.

## About

AdaptivePdfPro is a feature-rich Android PDF library that provides:
- 📄 Advanced PDF viewing with smooth rendering
- 🎨 Extensive theming and branding customization
- 🧭 Multiple navigation modes and controls
- 📊 Data overlay capabilities for dynamic content
- 🎯 Built with Kotlin and Jetpack Compose
- 🚀 Optimized performance with lazy loading

## Installation

### Step 1: Add JitPack Repository

Add the JitPack repository to your root `build.gradle` or `settings.gradle`:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

For newer projects using `settings.gradle`:
```gradle
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the Dependency

Add the dependency to your app module's `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.Sammug:Adaptive-Pdf-Pro:1.0.0'
}
```

## Setup & Usage

### Basic PDF Viewing

```kotlin
import com.androidstuff.adaptivepdfpro.core.PdfViewer

// Simple PDF viewing
PdfViewer.with(context)
    .load(pdfUri) // URI, File, or Asset path
    .show()
```

### Using Composable

```kotlin
import com.androidstuff.adaptivepdfpro.compose.PdfViewerComposable

@Composable
fun MyScreen() {
    PdfViewerComposable(
        pdfUri = myPdfUri,
        config = PdfConfig.Builder()
            .setInitialPage(0)
            .setScrollDirection(ScrollDirection.VERTICAL)
            .build()
    )
}
```

### Advanced Configuration

```kotlin
val config = PdfConfig.Builder()
    .setInitialPage(0)
    .setScrollDirection(ScrollDirection.VERTICAL)
    .setZoomEnabled(true)
    .setPinchToZoomEnabled(true)
    .setDoubleTapToZoomEnabled(true)
    .setMaxZoom(3.0f)
    .setMinZoom(0.5f)
    .setFitToWidthEnabled(true)
    .build()

val brandingConfig = BrandingConfig.Builder()
    .setHeaderLogo(R.drawable.logo)
    .setLogoPosition(LogoPosition.TOP_LEFT)
    .setLogoSize(40.dp)
    .setTitle("Document Title")
    .setSubtitle("Company Name")
    .setWatermarkEnabled(true)
    .setWatermarkText("CONFIDENTIAL")
    .build()

val navigationConfig = NavigationConfig.Builder()
    .setNavigationMode(NavigationMode.SWIPE)
    .setShowPageIndicator(true)
    .setShowBottomBar(true)
    .setShowThumbnails(true)
    .setPageTransitionAnimation(PageAnimation.SLIDE)
    .build()

val themeConfig = ThemeConfig.Builder()
    .setPrimaryColor(Color.Blue)
    .setBackgroundColor(Color.White)
    .setToolbarColor(Color.DarkGray)
    .setPageIndicatorColor(Color.Blue)
    .setUseDarkMode(false)
    .build()

// Apply all configurations
PdfViewer.with(context)
    .load(pdfUri)
    .setConfig(config)
    .setBranding(brandingConfig)
    .setNavigation(navigationConfig)
    .setTheme(themeConfig)
    .show()
```

### PDF Generation

```kotlin
import com.androidstuff.adaptivepdfpro.compose.PdfGenerator

val pdfGenerator = PdfGenerator(context)

// Generate PDF from composable content
pdfGenerator.generatePdf(
    content = { 
        // Your composable content here
        MyComposableContent()
    },
    fileName = "generated_document.pdf",
    pageSize = PageSize.A4
)
```

### Data Overlay

```kotlin
val dataConfig = PageDataConfig.Builder()
    .addOverlayData(
        pageNumber = 1,
        data = listOf(
            DataItem("Title", "Value"),
            DataItem("Date", "2024-01-01")
        )
    )
    .setOverlayPosition(OverlayPosition.TOP_RIGHT)
    .setOverlayStyle(
        backgroundColor = Color.White.copy(alpha = 0.9f),
        textColor = Color.Black,
        borderColor = Color.Gray
    )
    .build()

PdfViewer.with(context)
    .load(pdfUri)
    .setDataConfig(dataConfig)
    .show()
```

### Event Listeners

```kotlin
PdfViewer.with(context)
    .load(pdfUri)
    .setOnPageChangeListener { page, pageCount ->
        Log.d("PDF", "Page $page of $pageCount")
    }
    .setOnLoadCompleteListener { pages ->
        Log.d("PDF", "Loaded $pages pages")
    }
    .setOnErrorListener { error ->
        Log.e("PDF", "Error: ${error.message}")
    }
    .show()
```

## Features

### Core Features
- ✅ PDF rendering with PdfRenderer API
- ✅ Smooth scrolling and zooming
- ✅ Page navigation (swipe, buttons, slider)
- ✅ Thumbnail grid view
- ✅ Search functionality
- ✅ Text selection and copy
- ✅ Bookmark support

### Customization
- ✅ Custom branding (logos, titles, watermarks)
- ✅ Theming support (colors, fonts, styles)
- ✅ Header/footer customization
- ✅ Page indicator styles
- ✅ Navigation bar customization

### Advanced Features
- ✅ Data overlay on pages
- ✅ PDF generation from Compose
- ✅ Export pages as images
- ✅ Share functionality
- ✅ Download from URL
- ✅ Password-protected PDFs
- ✅ Night mode support

## Requirements

- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.0+
- **Compose BOM**: 2024.02.00+

## Contributing

We welcome contributions! Here's how you can help:

### How to Contribute

1. **Fork the Repository**
   ```bash
   git clone https://github.com/Sammug/Adaptive-Pdf-Pro.git
   cd Adaptive-Pdf-Pro
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make Your Changes**
   - Follow the existing code style
   - Add/update tests as needed
   - Update documentation if required

4. **Test Your Changes**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

5. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "feat: Add your feature description"
   ```
   
   Follow conventional commits:
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes
   - `refactor:` Code refactoring
   - `test:` Test additions/changes
   - `chore:` Maintenance tasks

6. **Push and Create Pull Request**
   ```bash
   git push origin feature/your-feature-name
   ```
   Then create a Pull Request on GitHub.

### Development Setup

1. Clone the repository
2. Open in Android Studio (Arctic Fox or newer)
3. Sync project with Gradle files
4. Run the sample app to test changes

### Code Style

- Use Kotlin coding conventions
- Follow MVVM architecture pattern
- Write meaningful commit messages
- Add KDoc comments for public APIs
- Ensure code passes lint checks

### Reporting Issues

- Use GitHub Issues to report bugs
- Provide detailed reproduction steps
- Include device/OS information
- Attach relevant logs or screenshots

## License

```
MIT License

Copyright (c) 2024 AdaptivePdfPro

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

## Support

For support, please:
- Check the [documentation](https://github.com/Sammug/Adaptive-Pdf-Pro/wiki)
- Search [existing issues](https://github.com/Sammug/Adaptive-Pdf-Pro/issues)
- Create a new issue if needed

## Acknowledgments

This library uses:
- Android PDF Renderer API
- Jetpack Compose
- Kotlin Coroutines
- AndroidX Libraries