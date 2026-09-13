package com.skypass.skytrack.ui.monthly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.MonthlyAttendanceDto
import com.skypass.skytrack.data.remote.MonthlyAttendanceSummary
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MonthlyStatisticsScreen(
    session: Session,
    viewModel: MonthlyStatisticsViewModel,
    onHome: () -> Unit,
    onLeave: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(session.token) { viewModel.load(session) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        TopBar("Statistics", onProfile = onOpenProfile)

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MonthSwitcher(
                    summary = state.summary,
                    onPrevious = { viewModel.loadPreviousMonth(session) },
                    onNext = { viewModel.loadNextMonth(session) }
                )
            }

            if (state.isLoading) {
                item {
                    Row(Modifier.fillMaxWidth().padding(28.dp), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(color = SkyRed)
                    }
                }
            } else if (state.error != null) {
                item {
                    WhiteCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ErrorOutline, null, tint = SkyRed, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(state.error.orEmpty(), color = SkyTextSecondary)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.load(session) }, colors = ButtonDefaults.buttonColors(containerColor = SkyRed)) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            } else {
                state.summary?.let { summary ->
                    item { SummaryGrid(summary) }
                    item { WorkHoursCard(summary) }
                    item { DistributionCard(summary) }
                    item {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Daily Attendance", style = MaterialTheme.typography.titleLarge)
                                Text("Operational attendance log", style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary)
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = SkySurfaceMuted) {
                                Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.FilterList, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.size(4.dp))
                                    Text("Filter", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                    items(state.attendance) { record -> AttendanceRow(record) }
                }
            }
        }

        BottomBar(MainTab.STATISTICS) { tab ->
            when (tab) {
                MainTab.HOME -> onHome()
                MainTab.LEAVE -> onLeave()
                MainTab.STATISTICS -> Unit
            }
        }
    }
}

@Composable
private fun MonthSwitcher(
    summary: MonthlyAttendanceSummary?,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val monthText = summary?.month?.let {
        runCatching {
            LocalDate.parse("$it-01").format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }.getOrNull()
    } ?: "Loading..."

    WhiteCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPrevious,
                colors = ButtonDefaults.buttonColors(containerColor = SkyRed),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.ChevronLeft, null)
                Spacer(Modifier.size(3.dp))
                Text("Previous")
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(monthText, style = MaterialTheme.typography.titleLarge)
                summary?.month?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary) }
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(containerColor = SkyRed),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("Next")
                Spacer(Modifier.size(3.dp))
                Icon(Icons.Default.ChevronRight, null)
            }
        }
    }
}

@Composable
private fun SummaryGrid(summary: MonthlyAttendanceSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Working Days", summary.workingDays.toString(), Icons.Default.CalendarMonth, SkyTextSecondary, Modifier.weight(1f))
            StatCard("Present", summary.presentDays.toString(), Icons.Default.CheckCircle, SkyGreen, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Absent", summary.absentDays.toString(), Icons.Default.ErrorOutline, SkyRed, Modifier.weight(1f))
            StatCard("Incomplete", summary.incompleteDays.toString(), Icons.Default.WarningAmber, SkyOrange, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier
) {
    WhiteCard(modifier) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, Modifier.weight(1f), color = SkyTextSecondary, style = MaterialTheme.typography.bodyMedium)
                Surface(shape = RoundedCornerShape(50), color = if (accent == SkyRed) SkyRedPale else SkySurfaceMuted) {
                    Icon(icon, null, tint = accent, modifier = Modifier.padding(8.dp).size(18.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.displayLarge)
        }
    }
}

@Composable
private fun WorkHoursCard(summary: MonthlyAttendanceSummary) {
    WhiteCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(8.dp), RoundedCornerShape(4.dp), SkyRed) {}
                Spacer(Modifier.size(8.dp))
                Text("Work Hours Summary", style = MaterialTheme.typography.titleLarge)
            }
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Total Logged", color = SkyTextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(summary.totalHours, color = SkyRed, style = MaterialTheme.typography.headlineLarge)
                }
                Column(Modifier.weight(1f)) {
                    Text("Daily Average", color = SkyTextSecondary, style = MaterialTheme.typography.bodySmall)
                    Text(summary.averageHours, style = MaterialTheme.typography.headlineLarge)
                }
            }
            Text("Monday–Saturday are working days; Sunday is the weekly off.", style = MaterialTheme.typography.bodySmall, color = SkyTextSecondary)
        }
    }
}

@Composable
private fun DistributionCard(summary: MonthlyAttendanceSummary) {
    val total = (summary.presentDays + summary.incompleteDays + summary.absentDays).coerceAtLeast(1)
    val presentWeight = summary.presentDays.toFloat() / total
    val incompleteWeight = summary.incompleteDays.toFloat() / total
    WhiteCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Attendance Distribution", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Text("$total tracked days", color = SkyTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Row(Modifier.fillMaxWidth().height(12.dp)) {
                Surface(Modifier.weight(presentWeight).fillMaxSize(), RoundedCornerShape(8.dp), SkyRed) {}
                if (summary.incompleteDays > 0) Surface(Modifier.weight(incompleteWeight).fillMaxSize(), RoundedCornerShape(8.dp), Color(0xFFFF6B61)) {}
                if (summary.absentDays > 0) Surface(Modifier.weight(summary.absentDays.toFloat()/total).fillMaxSize(), RoundedCornerShape(8.dp), SkyRedPale) {}
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Legend("Present (${summary.presentDays})", SkyRed)
                Legend("Incomplete (${summary.incompleteDays})", Color(0xFFFF6B61))
                Legend("Absent (${summary.absentDays})", SkyRedPale)
            }
        }
    }
}

@Composable
private fun Legend(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(8.dp), RoundedCornerShape(4.dp), color) {}
        Spacer(Modifier.size(5.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun AttendanceRow(record: MonthlyAttendanceDto) {
    val status = record.status.lowercase()
    val color = when {
        status.contains("present") -> SkyGreen
        status.contains("incomplete") -> SkyOrange
        else -> SkyRed
    }
    WhiteCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = SkySurfaceMuted) {
                Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(record.date.takeLast(2), fontWeight = FontWeight.Bold)
                    Text(
                        runCatching { LocalDate.parse(record.date).format(DateTimeFormatter.ofPattern("EEE")) }.getOrDefault(""),
                        style = MaterialTheme.typography.labelMedium,
                        color = SkyTextSecondary
                    )
                }
            }
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(record.date, fontWeight = FontWeight.SemiBold)
                Text(
                    "In: ${record.login.ifBlank { "--:--" }}  •  Out: ${record.logout.ifBlank { "--:--" }}",
                    color = SkyTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(shape = RoundedCornerShape(50), color = if (status.contains("present")) SkySurfaceMuted else SkyRedPale) {
                    Text(record.status.ifBlank { "—" }, Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = color, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(3.dp))
                Text(record.hours.ifBlank { "00:00" }, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
