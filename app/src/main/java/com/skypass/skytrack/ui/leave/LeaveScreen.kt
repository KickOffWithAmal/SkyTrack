package com.skypass.skytrack.ui.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.LeaveDto
import com.skypass.skytrack.ui.components.BottomBar
import com.skypass.skytrack.ui.components.MainTab
import com.skypass.skytrack.ui.components.TopBar
import com.skypass.skytrack.ui.components.WhiteCard
import com.skypass.skytrack.ui.theme.SkyGreen
import com.skypass.skytrack.ui.theme.SkyOrange
import com.skypass.skytrack.ui.theme.SkyRed
import com.skypass.skytrack.ui.theme.SkyRedPale
import com.skypass.skytrack.ui.theme.SkySurfaceMuted
import com.skypass.skytrack.ui.theme.SkyTextSecondary

@Composable
fun LeaveScreen(
    session: Session,
    viewModel: LeaveViewModel,
    onHome: () -> Unit,
    onStatistics: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var from by rememberSaveable { mutableStateOf("") }
    var to by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("Casual") }
    var reason by rememberSaveable { mutableStateOf("") }
    var menu by remember { mutableStateOf(false) }

    LaunchedEffect(session.token) { viewModel.loadHistory(session) }

    if (state.error != null || state.successMessage != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearMessages,
            title = { Text(if (state.successMessage != null) "Request submitted" else "Leave") },
            text = { Text(state.error ?: state.successMessage.orEmpty()) },
            confirmButton = { Button(onClick = viewModel::clearMessages) { Text("OK") } }
        )
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        TopBar("Leave Management", onProfile = onOpenProfile)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Leave Management", style = MaterialTheme.typography.headlineLarge)
                Text("Apply and track employee leaves", color = SkyTextSecondary)
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LeaveBalance("Casual", "—", Modifier.weight(1f))
                    LeaveBalance("Sick", "—", Modifier.weight(1f))
                    LeaveBalance("Earned", "—", Modifier.weight(1f))
                }
            }

            item {
                WhiteCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(14.dp), color = SkyRedPale) {
                                Icon(Icons.Default.CalendarMonth, null, tint = SkyRed, modifier = Modifier.padding(10.dp))
                            }
                            Spacer(Modifier.size(12.dp))
                            Column {
                                Text("Apply for Leave", style = MaterialTheme.typography.titleLarge)
                                Text("Submit a request for approval", color = SkyTextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Text("LEAVE TYPE", style = MaterialTheme.typography.labelMedium, color = SkyTextSecondary)
                        Column {
                            Surface(
                                onClick = { menu = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = SkySurfaceMuted
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("$type Leave", Modifier.weight(1f))
                                    Icon(Icons.Default.ChevronRight, null)
                                }
                            }
                            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                                listOf("Casual", "Sick", "Earned", "Other").forEach {
                                    DropdownMenuItem(text = { Text(it) }, onClick = { type = it; menu = false })
                                }
                            }
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            DateField("From date", from, { from = it }, Modifier.weight(1f))
                            DateField("To date", to, { to = it }, Modifier.weight(1f))
                        }

                        Text("REASON FOR LEAVE", style = MaterialTheme.typography.labelMedium, color = SkyTextSecondary)
                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            modifier = Modifier.fillMaxWidth().height(112.dp),
                            placeholder = { Text("Personal work / family commitment") },
                            shape = RoundedCornerShape(14.dp)
                        )
                        Text("Please provide a clear reason for management approval", style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary)

                        Button(
                            onClick = { viewModel.submit(session, from, to, type, reason) },
                            enabled = !state.isSubmitting,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyRed)
                        ) {
                            if (state.isSubmitting) {
                                androidx.compose.material3.CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Send, null)
                                Spacer(Modifier.size(8.dp))
                                Text("Submit Leave Request")
                            }
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Leave History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    Text("Recent", color = SkyTextSecondary, style = MaterialTheme.typography.labelMedium)
                }
            }

            if (state.isLoading) {
                item { androidx.compose.material3.CircularProgressIndicator(color = SkyRed) }
            } else if (state.leaves.isEmpty()) {
                item {
                    WhiteCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CalendarMonth, null, tint = SkyTextSecondary, modifier = Modifier.size(38.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No leave requests yet", fontWeight = FontWeight.SemiBold)
                            Text("Your submitted requests will appear here.", color = SkyTextSecondary)
                        }
                    }
                }
            } else {
                items(state.leaves, key = { it.requestId.orEmpty() }) { leave -> LeaveCard(leave) }
            }
        }

        BottomBar(MainTab.LEAVE) { tab ->
            when (tab) {
                MainTab.HOME -> onHome()
                MainTab.LEAVE -> Unit
                MainTab.STATISTICS -> onStatistics()
            }
        }
    }
}

@Composable
private fun LeaveBalance(label: String, value: String, modifier: Modifier) {
    WhiteCard(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary)
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text("available", style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary)
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun DateField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = SkyTextSecondary)
        Spacer(Modifier.height(5.dp))
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                shape = RoundedCornerShape(14.dp)
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showDialog = true }
            )
        }
    }

    if (showDialog) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState()
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC") // DatePicker returns UTC millis
                        onValueChange(sdf.format(java.util.Date(millis)))
                    }
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun LeaveCard(leave: LeaveDto) {
    val status = leave.status.orEmpty()
    val statusColor = when (status.lowercase()) {
        "approved" -> SkyGreen
        "pending" -> SkyOrange
        else -> SkyRed
    }
    WhiteCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(10.dp), color = SkySurfaceMuted) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.padding(8.dp), tint = SkyTextSecondary)
                }
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("${leave.type.orEmpty()} Leave", style = MaterialTheme.typography.titleMedium)
                    Text("${leave.from.orEmpty()} → ${leave.to.orEmpty()}", color = SkyTextSecondary)
                }
                Surface(shape = RoundedCornerShape(50), color = if (status.lowercase() == "approved") Color(0xFFDFF5E9) else SkyRedPale) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (status.lowercase() == "approved") Icons.Default.Check else Icons.Default.ErrorOutline, null, tint = statusColor, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.size(4.dp))
                        Text(status.ifBlank { "Pending" }, color = statusColor, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Surface(Modifier.fillMaxWidth(), RoundedCornerShape(14.dp), SkySurfaceMuted) {
                Text(leave.reason.orEmpty(), Modifier.padding(12.dp), color = SkyTextSecondary)
            }
            if (!leave.requestId.isNullOrBlank()) Text("Ref: ${leave.requestId}", style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary)
        }
    }
}
