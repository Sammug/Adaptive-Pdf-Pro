package com.androidstuff.adaptivepdfpro.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androidstuff.adaptivepdfpro.branding.*

/**
 * Composable for displaying branding overlays on PDF
 */
@Composable
fun BrandingOverlay(
    config: BrandingConfig,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Logos
        config.logos.forEach { logo ->
            LogoOverlay(
                logo = logo,
                modifier = Modifier.align(
                    when (logo.position) {
                        LogoPosition.TOP_LEFT -> Alignment.TopStart
                        LogoPosition.TOP_CENTER -> Alignment.TopCenter
                        LogoPosition.TOP_RIGHT -> Alignment.TopEnd
                        LogoPosition.BOTTOM_LEFT -> Alignment.BottomStart
                        LogoPosition.BOTTOM_CENTER -> Alignment.BottomCenter
                        LogoPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                        LogoPosition.CENTER -> Alignment.Center
                        LogoPosition.CUSTOM -> Alignment.Center
                    }
                )
            )
        }
        
        // Watermark
        config.watermark?.let { watermark ->
            WatermarkOverlay(
                watermark = watermark,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Title and Subtitle
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            config.title?.let { title ->
                Text(
                    text = title.text,
                    color = Color(title.color),
                    fontSize = title.textSize.sp,
                    fontWeight = if (title.isBold) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(
                        top = title.marginTop.dp,
                        bottom = title.marginBottom.dp
                    )
                )
            }
            config.subtitle?.let { subtitle ->
                Text(
                    text = subtitle.text,
                    color = Color(subtitle.color),
                    fontSize = subtitle.textSize.sp,
                    fontWeight = if (subtitle.isItalic) FontWeight.Light else FontWeight.Normal,
                    modifier = Modifier.padding(
                        top = subtitle.marginTop.dp,
                        bottom = subtitle.marginBottom.dp
                    )
                )
            }
        }
    }
}

/**
 * Logo overlay component
 */
@Composable
fun LogoOverlay(
    logo: LogoConfig,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(
                horizontal = logo.marginHorizontal.dp,
                vertical = logo.marginVertical.dp
            )
            .size(logo.size.dp.dp)
            .alpha(logo.opacity)
            .then(
                if (logo.clickAction != null) {
                    Modifier.clickable { logo.clickAction.invoke() }
                } else {
                    Modifier
                }
            )
    ) {
        logo.resourceId?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        logo.bitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Watermark overlay component
 */
@Composable
fun WatermarkOverlay(
    watermark: WatermarkConfig,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (watermark.repeat) {
            // Repeating watermark pattern
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(watermark.spacing.dp)
            ) {
                repeat(5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(watermark.spacing.dp)
                    ) {
                        repeat(3) {
                            WatermarkText(watermark)
                        }
                    }
                }
            }
        } else {
            // Single watermark
            WatermarkText(watermark)
        }
    }
}

@Composable
private fun WatermarkText(watermark: WatermarkConfig) {
    Text(
        text = watermark.text,
        color = Color(watermark.color),
        fontSize = watermark.textSize.sp,
        modifier = Modifier
            .rotate(watermark.rotation)
            .alpha(watermark.opacity)
    )
}

/**
 * PDF Header component
 */
@Composable
fun PdfHeader(
    config: HeaderConfig,
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(config.height.dp)
            .background(Color(config.backgroundColor))
            .padding(horizontal = 16.dp),
        contentAlignment = when (config.alignment) {
            TextAlignment.LEFT -> Alignment.CenterStart
            TextAlignment.CENTER -> Alignment.Center
            TextAlignment.RIGHT -> Alignment.CenterEnd
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.text?.let {
                Text(
                    text = it,
                    color = Color(config.textColor),
                    fontSize = config.textSize.sp
                )
            }
            
            if (config.showPageNumber) {
                Text(
                    text = "Page ${currentPage + 1}",
                    color = Color(config.textColor),
                    fontSize = config.textSize.sp
                )
            }
            
            if (config.showDate) {
                Text(
                    text = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                        .format(java.util.Date()),
                    color = Color(config.textColor),
                    fontSize = config.textSize.sp
                )
            }
        }
    }
}

/**
 * PDF Footer component
 */
@Composable
fun PdfFooter(
    config: FooterConfig,
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(config.height.dp)
            .background(Color(config.backgroundColor))
            .padding(horizontal = 16.dp),
        contentAlignment = when (config.alignment) {
            TextAlignment.LEFT -> Alignment.CenterStart
            TextAlignment.CENTER -> Alignment.Center
            TextAlignment.RIGHT -> Alignment.CenterEnd
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.text?.let {
                Text(
                    text = it,
                    color = Color(config.textColor),
                    fontSize = config.textSize.sp
                )
            }
            
            if (config.showPageNumber) {
                val pageText = if (config.showTotalPages) {
                    "Page ${currentPage + 1} of $pageCount"
                } else {
                    "Page ${currentPage + 1}"
                }
                Text(
                    text = pageText,
                    color = Color(config.textColor),
                    fontSize = config.textSize.sp
                )
            }
            
            config.copyright?.let {
                Text(
                    text = it,
                    color = Color(config.textColor),
                    fontSize = config.textSize.sp
                )
            }
        }
    }
}