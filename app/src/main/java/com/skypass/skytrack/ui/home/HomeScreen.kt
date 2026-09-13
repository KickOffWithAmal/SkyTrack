package com.skypass.skytrack.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.AttendanceDto
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// -----------------------------------------------------------------------------
// Stitch-inspired palette
// -----------------------------------------------------------------------------

private val BrandRed = Color(0xFFE53935)
private val BrandDarkRed = Color(0xFFC62828)
private val BrandLightRed = Color(0xFFFFEBEE)
private val BrandVeryLightRed = Color(0xFFFFF5F5)

private val SurfaceBase = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)

private val Slate900 = Color(0xFF0F172A)
private val Slate700 = Color(0xFF334155)
private val Slate500 = Color(0xFF64748B)
private val Slate400 = Color(0xFF94A3B8)
private val Slate300 = Color(0xFFCBD5E1)
private val Slate200 = Color(0xFFE2E8F0)
private val Slate100 = Color(0xFFF1F5F9)

private val DockDark = Color(0xFF1E2530)

private val AmberBg = Color(0xFFFFF8E1)
private val Amber = Color(0xFFF59E0B)

private val BlueBg = Color(0xFFEFF6FF)
private val Blue = Color(0xFF2563EB)

private val GreenBg = Color(0xFFECFDF5)
private val Green = Color(0xFF16A34A)


// -----------------------------------------------------------------------------
// HOME SCREEN
// -----------------------------------------------------------------------------

@Composable
fun HomeScreen(
    session: Session,
    viewModel: HomeViewModel,
    onLogout: () -> Unit,
    onOpenLeave: () -> Unit,
    onOpenMonthlyStatistics: () -> Unit,
    onOpenProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    var showLogoutConfirm by remember {
        mutableStateOf(false)
    }

    // -------------------------------------------------------------------------
    // EXISTING LOGIC - UNCHANGED
    // -------------------------------------------------------------------------

    LaunchedEffect(session.token) {
        viewModel.loadToday(session)
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = {
                Text("SkyTrack")
            },
            text = {
                Text(state.error.orEmpty())
            },
            confirmButton = {
                Button(
                    onClick = viewModel::clearError
                ) {
                    Text("OK")
                }
            }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = {
                showLogoutConfirm = false
            },
            title = {
                Text("Sign out?")
            },
            text = {
                Text("Are you sure you want to sign out?")
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showLogoutConfirm = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandRed
                    )
                ) {
                    Text("Sign Out")
                }
            }
        )
    }

    val attendance = state.attendance

    val started = !attendance?.login.isNullOrBlank()
    val ended = !attendance?.logout.isNullOrBlank()

    // -------------------------------------------------------------------------
    // UI
    // -------------------------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 20.dp,
                bottom = 110.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // -------------------------------------------------------------
            // Attendance header
            // -------------------------------------------------------------

            item {
                AttendanceHeader(
                    onLogout = {
                        showLogoutConfirm = true
                    }
                )
            }

            // -------------------------------------------------------------
            // Weekly calendar
            // -------------------------------------------------------------

            item {
                WeeklyDateStrip()
            }

            // -------------------------------------------------------------
            // Employee greeting
            // -------------------------------------------------------------

            item {
                EmployeeGreeting(
                    session = session
                )
            }

            // -------------------------------------------------------------
            // Main active-session UI
            // -------------------------------------------------------------

            item {
                ActiveSessionCard(
                    attendance = attendance,
                    started = started,
                    ended = ended,
                    actionLoading = state.isActionLoading
                )
            }

            // -------------------------------------------------------------
            // Three metrics
            // -------------------------------------------------------------

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {

                    AttendanceMetric(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.AccessTime,
                        iconBackground = AmberBg,
                        iconTint = Amber,
                        value = attendance?.login ?: "--:--",
                        label = "Check-In"
                    )

                    AttendanceMetric(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.ExitToApp,
                        iconBackground = BrandLightRed,
                        iconTint = BrandRed,
                        value = attendance?.logout ?: "--:--",
                        label = "Check-Out"
                    )

                    AttendanceMetric(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.BarChart,
                        iconBackground = BlueBg,
                        iconTint = Blue,
                        value = attendance?.hours ?: "--:--",
                        label = "Total Hours"
                    )
                }
            }

            // -------------------------------------------------------------
            // Checkout section
            // -------------------------------------------------------------

            item {
                CheckoutSection(
                    attendance = attendance,
                    started = started,
                    ended = ended,
                    actionLoading = state.isActionLoading,
                    onCheckout = {
                        // EXISTING LOGIC - UNCHANGED
                        viewModel.endWork(session)
                    }
                )
            }

            // -------------------------------------------------------------
            // Existing navigation entry points
            //
            // These remain so Leave is still accessible without changing
            // navigation/business logic.
            // -------------------------------------------------------------

            item {
                QuickActions(
                    onLeave = onOpenLeave,
                    onStatistics = onOpenMonthlyStatistics
                )
            }
        }

        // ---------------------------------------------------------------------
        // Stitch-style floating dock
        //
        // Existing callbacks are reused.
        // ---------------------------------------------------------------------

        ActiveBottomDock(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            presenceActive = started && !ended,
            presenceLoading = state.isActionLoading,
            onPresenceClick = {
                // EXISTING LOGIC - UNCHANGED
                if (!started) {
                    viewModel.startWork(session)
                } else if (!ended) {
                    viewModel.endWork(session)
                }
            },
            onReports = onOpenMonthlyStatistics,
            onProfile = onOpenProfile
        )
    }
}


// -----------------------------------------------------------------------------
// HEADER
// -----------------------------------------------------------------------------

@Composable
private fun AttendanceHeader(
    onLogout: () -> Unit
) {
    val today = remember {
        LocalDate.now()
    }

    val monthName = remember(today) {
        today.month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            shape = RoundedCornerShape(15.dp),
            color = CardWhite,
            shadowElevation = 1.dp
        ) {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                tint = BrandRed,
                modifier = Modifier
                    .size(40.dp)
                    .padding(10.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "ATTENDANCE",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Text(
                text = "$monthName ${today.year}",
                color = Slate900,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            onClick = onLogout,
            shape = CircleShape,
            color = CardWhite,
            shadowElevation = 2.dp
        ) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Sign out",
                tint = BrandRed,
                modifier = Modifier
                    .size(42.dp)
                    .padding(11.dp)
            )
        }
    }
}


// -----------------------------------------------------------------------------
// WEEKLY DATE STRIP
// -----------------------------------------------------------------------------

@Composable
private fun WeeklyDateStrip() {

    val today = remember {
        LocalDate.now()
    }

    val weekStart = today.minusDays(
        (today.dayOfWeek.value - 1).toLong()
    )

    val days = remember(today) {
        (0..6).map {
            weekStart.plusDays(it.toLong())
        }
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(days) { date ->

            val selected = date == today

            Surface(
                modifier = Modifier
                    .width(46.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                shadowElevation = if (selected) 1.dp else 0.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (selected) {
                                Modifier.background(
                                    BrandVeryLightRed,
                                    RoundedCornerShape(16.dp)
                                )
                            } else {
                                Modifier
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (selected) {
                            BrandRed
                        } else {
                            Slate900
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = date.dayOfWeek
                            .getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault()
                            )
                            .uppercase(),
                        color = if (selected) {
                            BrandRed
                        } else {
                            Slate400
                        },
                        fontSize = 10.sp,
                        fontWeight = if (selected) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        }
                    )
                }
            }
        }
    }
}


// -----------------------------------------------------------------------------
// GREETING
// -----------------------------------------------------------------------------

@Composable
private fun EmployeeGreeting(
    session: Session
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Good day,",
            color = Slate400,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = session.name,
                color = Slate900,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Slate100
            ) {

                Text(
                    text = session.employeeId,
                    color = Slate500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    )
                )
            }
        }
    }
}


// -----------------------------------------------------------------------------
// ACTIVE SESSION / CIRCULAR TIMER UI
// -----------------------------------------------------------------------------

@Composable
private fun ActiveSessionCard(
    attendance: AttendanceDto?,
    started: Boolean,
    ended: Boolean,
    actionLoading: Boolean
) {

    val rotationTransition = rememberInfiniteTransition(
        label = "sessionRing"
    )

    val rotation by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 9000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    val pulseTransition = rememberInfiniteTransition(
        label = "sessionPulse"
    )

    val pulse by pulseTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringPulse"
    )

    val ringColor = when {
        ended -> Slate300
        started -> BrandRed
        else -> Slate200
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {

        // -------------------------------------------------------------
        // Decorative dashed outer ring
        // -------------------------------------------------------------

        Box(
            modifier = Modifier
                .size(250.dp)
                .rotate(rotation)
                .scale(
                    if (started) 1f else pulse
                ),
            contentAlignment = Alignment.Center
        ) {

            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                drawCircle(
                    color = ringColor.copy(
                        alpha = if (started) 0.45f else 0.30f
                    ),
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 8f)
                        )
                    )
                )
            }
        }

        // -------------------------------------------------------------
        // Main white circle
        // -------------------------------------------------------------

        Surface(
            modifier = Modifier.size(194.dp),
            shape = CircleShape,
            color = CardWhite,
            shadowElevation = 8.dp
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                // Inner dashed circle

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {

                    drawCircle(
                        color = BrandRed.copy(alpha = 0.16f),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(7f, 7f)
                            )
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    SessionStatusPill(
                        started = started,
                        ended = ended
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = when {
                            ended -> "Completed"
                            started -> "WORKING"
                            else -> "READY"
                        },
                        color = when {
                            started && !ended -> BrandRed
                            else -> Slate900
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = when {
                            ended -> {
                                "Total ${attendance?.hours ?: "--:--"}"
                            }

                            started -> {
                                "Checked in at ${attendance?.login ?: "--:--"}"
                            }

                            else -> {
                                "Ready to check in"
                            }
                        },
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (actionLoading) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        CircularProgressIndicator(
                            modifier = Modifier.size(17.dp),
                            color = BrandRed,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}


// -----------------------------------------------------------------------------
// STATUS PILL
// -----------------------------------------------------------------------------

@Composable
private fun SessionStatusPill(
    started: Boolean,
    ended: Boolean
) {

    val background: Color
    val foreground: Color
    val label: String

    when {
        ended -> {
            background = Slate100
            foreground = Slate500
            label = "COMPLETED"
        }

        started -> {
            background = BrandLightRed
            foreground = BrandRed
            label = "ACTIVE WORKING"
        }

        else -> {
            background = GreenBg
            foreground = Green
            label = "READY"
        }
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        foreground,
                        CircleShape
                    )
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text(
                text = label,
                color = foreground,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}


// -----------------------------------------------------------------------------
// METRIC CARD
// -----------------------------------------------------------------------------

@Composable
private fun AttendanceMetric(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBackground: Color,
    iconTint: Color,
    value: String,
    label: String
) {

    Surface(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(18.dp),
        color = CardWhite,
        shadowElevation = 1.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(11.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        iconBackground,
                        RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(15.dp)
                )
            }

            Column {

                Text(
                    text = value,
                    color = Slate900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = label,
                    color = Slate400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


// -----------------------------------------------------------------------------
// CHECKOUT SECTION
// -----------------------------------------------------------------------------

@Composable
private fun CheckoutSection(
    attendance: AttendanceDto?,
    started: Boolean,
    ended: Boolean,
    actionLoading: Boolean,
    onCheckout: () -> Unit
) {

    if (!started || ended) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 4.dp,
                bottom = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            shape = RoundedCornerShape(9.dp),
            color = Slate900,
            shadowElevation = 3.dp
        ) {

            Text(
                text = "End your work session when finished",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 8.dp
                )
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Surface(
            onClick = {
                if (!actionLoading) {
                    onCheckout()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Slate200,
            shadowElevation = 0.dp
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 15.dp
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (actionLoading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Slate500,
                        strokeWidth = 2.dp
                    )

                } else {

                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = if (actionLoading) {
                        "Checking out..."
                    } else {
                        "Check-Out"
                    },
                    color = Slate500,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


// -----------------------------------------------------------------------------
// QUICK ACTIONS
// -----------------------------------------------------------------------------

@Composable
private fun QuickActions(
    onLeave: () -> Unit,
    onStatistics: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Surface(
            onClick = onLeave,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp
        ) {

            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.EventNote,
                    contentDescription = null,
                    tint = BrandRed,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Leave",
                    color = Slate900,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Surface(
            onClick = onStatistics,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = CardWhite,
            shadowElevation = 1.dp
        ) {

            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = null,
                    tint = BrandRed,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Statistics",
                    color = Slate900,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


// -----------------------------------------------------------------------------
// STITCH-STYLE BOTTOM DOCK
// -----------------------------------------------------------------------------

@Composable
private fun ActiveBottomDock(
    modifier: Modifier,
    presenceActive: Boolean,
    presenceLoading: Boolean,
    onPresenceClick: () -> Unit,
    onReports: () -> Unit,
    onProfile: () -> Unit
) {

    Surface(
        modifier = modifier
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 18.dp
            ),
        shape = RoundedCornerShape(28.dp),
        color = DockDark,
        shadowElevation = 12.dp
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // HOME

            DockIcon(
                icon = Icons.Filled.Home,
                label = "Home",
                active = true,
                onClick = {}
            )

            // PRESENCE

            Surface(
                onClick = onPresenceClick,
                shape = RoundedCornerShape(50),
                color = BrandRed
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 15.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (presenceLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Icon(
                            imageVector = if (presenceActive) {
                                Icons.Filled.Stop
                            } else {
                                Icons.Filled.PlayArrow
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = if (presenceActive) {
                            "Check-Out"
                        } else {
                            "Presence"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // REPORTS

            DockIcon(
                icon = Icons.Filled.BarChart,
                label = "Reports",
                active = false,
                onClick = onReports
            )

            // PROFILE

            DockIcon(
                icon = Icons.Filled.Person,
                label = "Profile",
                active = false,
                onClick = onProfile
            )
        }
    }
}


// -----------------------------------------------------------------------------
// DOCK ICON
// -----------------------------------------------------------------------------

@Composable
private fun DockIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) {
                Color.White
            } else {
                Color(0xFF9CA3AF)
            },
            modifier = Modifier.size(20.dp)
        )
    }
}