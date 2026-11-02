package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhError
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhSurface
import com.example.ui.theme.TokokuhTextPrimary
import com.example.ui.theme.TokokuhTextSecondary
import com.example.ui.theme.TokokuhWhite
import java.io.File

/**
 * Standard explicit colors for all OutlinedTextField components across TOKOKUH RESTO.
 * Guarantees high-contrast, fully visible inputs, crisp labels (#222222 text, #666666 inactive label,
 * #720000 focused label, #777777 placeholder, #BDBDBD border, #720000 focused border, #FFFFFF background).
 */
@Composable
fun tokokuhTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFF222222),
    unfocusedTextColor = Color(0xFF222222),
    disabledTextColor = Color(0xFF777777),
    focusedContainerColor = Color(0xFFFFFFFF),
    unfocusedContainerColor = Color(0xFFFFFFFF),
    disabledContainerColor = Color(0xFFF7F7F7),
    focusedBorderColor = Color(0xFF720000),
    unfocusedBorderColor = Color(0xFFBDBDBD),
    disabledBorderColor = Color(0xFFE0E0E0),
    focusedLabelColor = Color(0xFF720000),
    unfocusedLabelColor = Color(0xFF666666),
    disabledLabelColor = Color(0xFF888888),
    cursorColor = Color(0xFF720000),
    focusedPlaceholderColor = Color(0xFF777777),
    unfocusedPlaceholderColor = Color(0xFF777777),
    disabledPlaceholderColor = Color(0xFF9E9E9E),
    focusedLeadingIconColor = Color(0xFF720000),
    unfocusedLeadingIconColor = Color(0xFF666666),
    focusedTrailingIconColor = Color(0xFF720000),
    unfocusedTrailingIconColor = Color(0xFF666666)
)

/**
 * Primary action button: #720000 container with explicit #FFFFFF white text and icon.
 * Minimum 48dp height to meet Android accessibility guidelines.
 */
@Composable
fun TokokuhPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = TokokuhMaroonPrimary,
            contentColor = TokokuhWhite,
            disabledContainerColor = TokokuhBorder,
            disabledContentColor = TokokuhTextSecondary
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TokokuhWhite,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = TokokuhWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/**
 * Secondary action button: #FFFFFF container, #720000 text, 1dp maroon border.
 */
@Composable
fun TokokuhSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = TokokuhWhite,
            contentColor = TokokuhMaroonPrimary
        ),
        border = BorderStroke(1.dp, TokokuhMaroonPrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TokokuhMaroonPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = TokokuhMaroonPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/**
 * Accent button: #FFC107 container, #4D0000 text and icon.
 */
@Composable
fun TokokuhAccentButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = TokokuhAccentYellow,
            contentColor = TokokuhMaroonDark
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TokokuhMaroonDark,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = TokokuhMaroonDark,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/**
 * Danger / Destructive action button: #D32F2F container, #FFFFFF text and icon.
 */
@Composable
fun TokokuhDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = TokokuhError,
            contentColor = TokokuhWhite
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TokokuhWhite,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = TokokuhWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/**
 * Informative Empty State for tables, menu, categories, inventory, staff, expenses, shifts, etc.
 * Features an icon in a circular container, bold title, descriptive message, and an optional call-to-action button.
 */
@Composable
fun TokokuhEmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    actionTestTag: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TokokuhBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(TokokuhSurface)
                    .border(1.dp, TokokuhBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TokokuhMaroonPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TokokuhTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                fontSize = 13.sp,
                color = TokokuhTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            if (actionText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(20.dp))
                TokokuhPrimaryButton(
                    text = actionText,
                    onClick = onActionClick,
                    testTag = actionTestTag
                )
            }
        }
    }
}

/**
 * Shared Receipt Header:
 * If the restaurant has uploaded a custom logo ([profile.logoPath]), displays the restaurant's logo
 * centered at the VERY TOP of the receipt before the restaurant name.
 * Otherwise, displays the official TOKOKUH RESTO logo badge on a maroon surface.
 */
@Composable
fun ReceiptHeaderView(
    profile: RestaurantProfileEntity?,
    modifier: Modifier = Modifier
) {
    val logoPath = profile?.logoPath
    val logoBitmap = remember(logoPath) {
        if (!logoPath.isNullOrBlank()) {
            val file = File(logoPath)
            if (file.exists()) {
                try {
                    BitmapFactory.decodeFile(file.absolutePath)
                } catch (e: Exception) {
                    null
                }
            } else null
        } else null
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (logoBitmap != null) {
            // Prominent custom restaurant logo at the very top of receipt
            Image(
                bitmap = logoBitmap.asImageBitmap(),
                contentDescription = "Logo Restoran",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(68.dp)
                    .padding(bottom = 8.dp)
            )
        } else {
            // Default official TOKOKUH RESTO header on maroon background
            Surface(
                color = TokokuhMaroonPrimary,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                        contentDescription = "TOKOKUH RESTO",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            text = profile?.name ?: "TOKOKUH RESTO",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TokokuhTextPrimary,
            textAlign = TextAlign.Center
        )

        val tagline = profile?.brandTagline
        if (!tagline.isNullOrBlank()) {
            Text(
                text = tagline,
                fontSize = 11.sp,
                color = TokokuhTextSecondary,
                textAlign = TextAlign.Center
            )
        }

        val address = profile?.address
        if (!address.isNullOrBlank()) {
            Text(
                text = address,
                fontSize = 11.sp,
                color = TokokuhTextSecondary,
                textAlign = TextAlign.Center
            )
        }

        val phone = profile?.phone
        if (!phone.isNullOrBlank()) {
            Text(
                text = "Telp: $phone",
                fontSize = 11.sp,
                color = TokokuhTextSecondary,
                textAlign = TextAlign.Center
            )
        }

        val receiptHeader = profile?.receiptHeader
        if (!receiptHeader.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = receiptHeader,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TokokuhMaroonPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
