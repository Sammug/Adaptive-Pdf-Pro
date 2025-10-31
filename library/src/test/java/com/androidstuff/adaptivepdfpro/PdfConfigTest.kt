package com.androidstuff.adaptivepdfpro

import com.androidstuff.adaptivepdfpro.branding.BrandingConfig
import com.androidstuff.adaptivepdfpro.branding.LogoPosition
import com.androidstuff.adaptivepdfpro.branding.LogoSize
import com.androidstuff.adaptivepdfpro.core.PdfConfig
import com.androidstuff.adaptivepdfpro.core.PdfSource
import com.androidstuff.adaptivepdfpro.core.ViewerConfig
import com.androidstuff.adaptivepdfpro.navigation.NavigationConfig
import com.androidstuff.adaptivepdfpro.navigation.SwipeDirection
import com.androidstuff.adaptivepdfpro.theme.ThemeConfig
import com.androidstuff.adaptivepdfpro.theme.ThemeMode
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class PdfConfigTest {
    
    @Test
    fun `test PdfConfig builder with all options`() {
        val config = PdfConfig.build {
            load("https://example.com/test.pdf")
            setBranding {
                logo(resId = 123456, position = LogoPosition.TOP_LEFT) // Using dummy resource ID for testing
                title("Test Document", color = 0xFF000000.toInt())
                subtitle("Test Subtitle")
                watermark("CONFIDENTIAL")
            }
            setTheme {
                primaryColor(0xFF2196F3.toInt())
                darkTheme()
            }
            setNavigation {
                enableSwipe(true)
                swipeDirection(SwipeDirection.HORIZONTAL)
                showThumbnails(true)
                showPageSlider(true)
            }
            setViewer {
                enableZoom(true)
                setZoomLevels(0.5f, 1.5f, 3.0f)
                nightMode(false)
            }
        }
        
        // Verify source
        assertTrue(config.source is PdfSource.Url)
        assertEquals("https://example.com/test.pdf", (config.source as PdfSource.Url).url)
        
        // Verify branding
        assertEquals(1, config.brandingConfig.logos.size)
        assertEquals(LogoPosition.TOP_LEFT, config.brandingConfig.logos[0].position)
        assertNotNull(config.brandingConfig.title)
        assertNotNull(config.brandingConfig.subtitle)
        assertNotNull(config.brandingConfig.watermark)
        
        // Verify theme
        assertEquals(ThemeMode.DARK, config.themeConfig.themeMode)
        
        // Verify navigation
        assertTrue(config.navigationConfig.enableSwipe)
        assertEquals(SwipeDirection.HORIZONTAL, config.navigationConfig.swipeDirection)
        assertTrue(config.navigationConfig.showThumbnails)
        assertTrue(config.navigationConfig.showPageSlider)
        
        // Verify viewer
        assertTrue(config.viewerConfig.enableZoom)
        assertEquals(0.5f, config.viewerConfig.minZoom)
        assertEquals(3.0f, config.viewerConfig.maxZoom)
        assertFalse(config.viewerConfig.nightMode)
    }
    
    @Test
    fun `test PdfConfig with file source`() {
        val testFile = File("/test/path/document.pdf")
        val config = PdfConfig.build {
            load(testFile)
        }
        
        assertTrue(config.source is PdfSource.File)
        assertEquals(testFile, (config.source as PdfSource.File).file)
    }
    
    @Test
    fun `test PdfConfig with asset source`() {
        val config = PdfConfig.build {
            loadFromAssets("sample.pdf")
        }
        
        assertTrue(config.source is PdfSource.Asset)
        assertEquals("sample.pdf", (config.source as PdfSource.Asset).assetPath)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `test PdfConfig without source throws exception`() {
        PdfConfig.build {
            // No source specified
        }
    }
    
    @Test
    fun `test default configurations`() {
        val config = PdfConfig.build {
            loadFromAssets("test.pdf")
        }
        
        // Test defaults
        assertTrue(config.brandingConfig.logos.isEmpty())
        assertNull(config.brandingConfig.watermark)
        assertEquals(ThemeMode.LIGHT, config.themeConfig.themeMode)
        assertTrue(config.navigationConfig.enableSwipe)
        assertTrue(config.viewerConfig.enableZoom)
        assertTrue(config.downloadConfig.enabled)
        assertTrue(config.sharingConfig.enabled)
    }
    
    @Test
    fun `test BrandingConfig builder`() {
        val branding = BrandingConfig.Builder().apply {
            logo(resId = 789012, size = LogoSize.LARGE) // Using dummy resource ID for testing
            title("My Document", color = 0xFF000000.toInt(), isBold = true)
            watermark("DRAFT", opacity = 0.3f)
            header(text = "Header Text", showPageNumber = true)
            footer(text = "Footer Text", copyright = "© 2024")
        }.build()
        
        assertEquals(1, branding.logos.size)
        assertEquals(LogoSize.LARGE, branding.logos[0].size)
        assertEquals("My Document", branding.title?.text)
        assertTrue(branding.title?.isBold == true)
        assertEquals("DRAFT", branding.watermark?.text)
        assertEquals(0.3f, branding.watermark?.opacity)
        assertEquals("Header Text", branding.header?.text)
        assertEquals("Footer Text", branding.footer?.text)
        assertEquals("© 2024", branding.footer?.copyright)
    }
}