package com.skypass.skytrack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skypass.skytrack.BuildConfig
import com.skypass.skytrack.data.repository.AppUpdateInfo

/**
 * Enterprise SkyTrack in-app update dialog adhering to the app's White and Red design system.
 */
@Composable
fun AppUpdateDialog(
    updateInfo: AppUpdateInfo,
    onUpdateClick: (releaseUrl: String) -> Unit,
    onDismiss: () -> Unit
) {
    val brandRed = Color(0xFFC62828)
    val redBadgeBg = Color(0xFFFEF2F2)
    val slate900 = Color(0xFF0F172A)
    val slate700 = Color(0xFF334155)
    val slate600 = Color(0xFF475569)
    val slate500 = Color(0xFF64748B)
    val slate400 = Color(0xFF94A3B8)
    val borderColor = Color(0xFFE2E8F0)
    val notesBg = Color(0xFFF8FAFC)

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(redBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.SystemUpdate,
                    contentDescription = "Update Available",
                    tint = brandRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Update Available",
                    fontWeight = FontWeight.Bold,
                    color = slate900,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Version Chips Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current installed chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Text(
                            text = "Installed: v${BuildConfig.VERSION_NAME}",
                            color = slate500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // New available version chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = redBadgeBg,
                        border = BorderStroke(1.dp, brandRed.copy(alpha = 0.2f))
                    ) {
                        Text(
                            text = "Latest: v${updateInfo.versionName}",
                            color = brandRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = "A new version of SkyTrack is available on GitHub. Update now to enjoy the latest features, enhancements, and fixes.",
                    color = slate600,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Release notes section
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = notesBg,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 160.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = "Release Notes",
                            fontWeight = FontWeight.SemiBold,
                            color = slate700,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val notes = updateInfo.releaseNotes?.trim()
                        if (!notes.isNullOrBlank()) {
                            Text(
                                text = notes,
                                color = slate600,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        } else {
                            Text(
                                text = "• General performance improvements\n• Bug fixes and stability enhancements",
                                color = slate400,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Later button
                OutlinedButton(
                    onClick = onDismiss,
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
                        text = "Later",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                // Update button
                Button(
                    onClick = {
                        onUpdateClick(updateInfo.releaseUrl)
                    },
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Update",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White
    )
}
