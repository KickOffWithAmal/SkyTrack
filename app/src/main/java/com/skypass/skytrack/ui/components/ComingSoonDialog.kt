package com.skypass.skytrack.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.runtime.Composable

@Composable
fun ComingSoonDialog(
    title: String = "Notifications",
    message: String = "This feature is coming soon.",
    onDismiss: () -> Unit
) {
    SkyTrackDialog(
        title = title,
        message = message,
        icon = Icons.Rounded.Notifications,
        confirmText = "Got it",
        onConfirm = onDismiss,
        onDismissRequest = onDismiss
    )
}
