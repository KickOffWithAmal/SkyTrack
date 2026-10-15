package com.skypass.skytrack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SkyTrackDialogType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

/**
 * Enterprise SkyTrack Common Popup / Dialog adhering to the app's White and Red theme.
 */
@Composable
fun SkyTrackDialog(
    title: String,
    message: String,
    icon: ImageVector? = null,
    type: SkyTrackDialogType = SkyTrackDialogType.INFO,
    confirmText: String = "OK",
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = { onDismiss?.invoke() ?: onConfirm() }
) {
    val brandRed = Color(0xFFC62828)
    val redBadgeBg = Color(0xFFFEF2F2)
    val slate900 = Color(0xFF0F172A)
    val slate600 = Color(0xFF475569)
    val slate500 = Color(0xFF64748B)
    val borderColor = Color(0xFFE2E8F0)

    val resolvedIcon = icon ?: when (type) {
        SkyTrackDialogType.ERROR -> Icons.Rounded.ErrorOutline
        SkyTrackDialogType.WARNING -> Icons.Rounded.WarningAmber
        SkyTrackDialogType.SUCCESS -> Icons.Rounded.CheckCircle
        SkyTrackDialogType.INFO -> Icons.Rounded.Info
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(redBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = resolvedIcon,
                    contentDescription = null,
                    tint = brandRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = slate900,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = message,
                color = slate600,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            if (dismissText != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onDismiss?.invoke() ?: onDismissRequest() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = slate500
                        ),
                        border = BorderStroke(1.dp, borderColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text(
                            text = dismissText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brandRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text(
                            text = confirmText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = confirmText,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White
    )
}
