package com.skypass.skytrack.ui.leave

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowRight
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MoneyOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.SupervisorAccount
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.skypass.skytrack.ui.components.SyncLoadingOverlay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.remote.LeaveDto
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

// Stitch Color Palette
private val BrandRed = Color(0xFFEF4444)
private val BrandRedHover = Color(0xFFDC2626)
private val BrandLightRed = Color(0xFFFEF2F2)
private val BrandRedBorder = Color(0xFFFEE2E2)
private val SurfaceBase = Color(0xFFF8FAFC)
private val SurfaceCard = Color(0xFFFFFFFF)
private val DockBg = Color(0xFF1E2530)
private val Slate50 = Color(0xFFF8FAFC)
private val Slate100 = Color(0xFFF1F5F9)
private val Slate200 = Color(0xFFE2E8F0)
private val Slate300 = Color(0xFFCBD5E1)
private val Slate400 = Color(0xFF94A3B8)
private val Slate500 = Color(0xFF64748B)
private val Slate600 = Color(0xFF475569)
private val Slate700 = Color(0xFF334155)
private val Slate800 = Color(0xFF1E293B)
private val Slate900 = Color(0xFF0F172A)
private val Emerald50 = Color(0xFFECFDF5)
private val Emerald500 = Color(0xFF10B981)
private val Emerald600 = Color(0xFF059669)
private val Amber50 = Color(0xFFFFFBEB)
private val Amber600 = Color(0xFFD97706)
private val Teal600 = Color(0xFF0D9488)

@Composable
fun LeaveScreen(
    session: Session,
    viewModel: LeaveViewModel,
    onBack: () -> Unit,
    onNavigateHome: () -> Unit = onBack,
    onNavigateStatistics: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Form State
    var leaveType by remember { mutableStateOf("Casual Leave") }
    val today = remember { LocalDate.now() }
    var currentMonth by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedStartDate by remember { mutableStateOf(today) }
    var selectedEndDate by remember { mutableStateOf(today.plusDays(1)) }
    var isHalfDay by remember { mutableStateOf(false) }
    var selectedSession by remember { mutableStateOf("First Half") }
    var reason by remember { mutableStateOf("") }

    LaunchedEffect(session.token) {
        viewModel.loadHistory(session)
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearMessages,
            title = { Text("SkyTrack", fontWeight = FontWeight.Bold) },
            text = { Text(state.error.orEmpty()) },
            confirmButton = {
                Button(onClick = viewModel::clearMessages) {
                    Text("OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 110.dp) // Room for docked navigation bar
        ) {
            // Top Bar: Back Arrow, Title, Subtitle
            LeaveTopBar(onBack = onBack)

            Spacer(modifier = Modifier.height(14.dp))

            // Employee Context Sub-Header
            EmployeeContextSubHeader(
                name = session.name,
                employeeId = session.employeeId,
                role = session.role
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Leave Balances Overview (3 Horizontal Cards)
            LeaveBalancesSection(currentMonth = currentMonth)

            Spacer(modifier = Modifier.height(14.dp))

            // Main Leave Configuration Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, Slate100),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Step Pill Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 6.dp, height = 16.dp)
                                    .clip(CircleShape)
                                    .background(BrandRed)
                            )
                            Text(
                                text = "Select Leave Type",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandLightRed)
                                .border(1.dp, BrandRedBorder, CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Step 1 of 2",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandRed
                            )
                        }
                    }

                    // Leave Type Selector Group
                    LeaveTypeSelector(
                        selectedType = leaveType,
                        onTypeSelected = { leaveType = it }
                    )

                    // Custom Calendar Container
                    LeaveCalendarSection(
                        currentMonth = currentMonth,
                        onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                        onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                        selectedStartDate = selectedStartDate,
                        selectedEndDate = if (isHalfDay) selectedStartDate else selectedEndDate,
                        isHalfDay = isHalfDay,
                        onDateClicked = { clickedDate ->
                            if (isHalfDay) {
                                selectedStartDate = clickedDate
                                selectedEndDate = clickedDate
                            } else {
                                if (selectedStartDate != selectedEndDate) {
                                    // Start a fresh range
                                    selectedStartDate = clickedDate
                                    selectedEndDate = clickedDate
                                } else {
                                    if (clickedDate < selectedStartDate) {
                                        selectedStartDate = clickedDate
                                    } else {
                                        selectedEndDate = clickedDate
                                    }
                                }
                            }
                        }
                    )

                    // Leave Duration Section & Half Day Switch
                    LeaveDurationToggle(
                        isHalfDay = isHalfDay,
                        onToggle = { isHalfDay = !isHalfDay },
                        selectedSession = selectedSession,
                        onSessionSelected = { selectedSession = it },
                        startDate = selectedStartDate,
                        endDate = if (isHalfDay) selectedStartDate else selectedEndDate
                    )

                    // Reason for Leave Input with Quick Tags
                    LeaveReasonInput(
                        reason = reason,
                        onReasonChange = { if (it.length <= 150) reason = it },
                        onTagSelected = { tag ->
                            reason = if (reason.isBlank()) tag else "$reason, $tag"
                        }
                    )

                    // Approver Handover Note
                    ApproverHandoverCard()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slide-To-Submit Action Slider
            SwipeToSubmitSlider(
                isLoading = state.isSubmitting,
                onSubmit = {
                    val finalFrom = selectedStartDate.toString()
                    val finalTo = (if (isHalfDay) selectedStartDate else selectedEndDate).toString()
                    val finalReason = if (isHalfDay) {
                        if (reason.isBlank()) "Half Day ($selectedSession)" else "$reason ($selectedSession)"
                    } else {
                        if (reason.isBlank()) "Leave Application" else reason
                    }

                    viewModel.submit(
                        session = session,
                        from = finalFrom,
                        to = finalTo,
                        type = leaveType,
                        reason = finalReason
                    )
                }
            )

            // Success Card Feedback
            AnimatedVisibility(visible = state.successMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                SubmissionSuccessCard(message = state.successMessage.orEmpty())
            }

            // Recent Leaves History
            if (state.leaves.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                RecentLeaveHistorySection(leaves = state.leaves)
            }
        }

        // Floating Bottom Navigation Dock (Active on Leaves)
        LeaveBottomNavigationDock(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            onHomeClick = onNavigateHome,
            onAnalyticsClick = onNavigateStatistics,
            onProfileClick = onNavigateProfile
        )
        
        SyncLoadingOverlay(isLoading = state.isSubmitting)
    }
}

// -------------------------------------------------------------
// TOP BAR
// -------------------------------------------------------------
@Composable
private fun LeaveTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, Slate100),
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Slate700,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = "TIME OFF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Apply Leave",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    letterSpacing = (-0.4).sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// EMPLOYEE CONTEXT SUB-HEADER
// -------------------------------------------------------------
@Composable
private fun EmployeeContextSubHeader(
    name: String,
    employeeId: String,
    role: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, Slate100.copy(alpha = 0.9f)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pulse Green Dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Emerald500)
                )

                Text(
                    text = if (name.isNotBlank()) name else "Amal Ramachandran",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Slate100)
                        .border(1.dp, Slate200.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (employeeId.isNotBlank()) employeeId else "SK001",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate600
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Slate50)
                    .border(1.dp, Slate200.copy(alpha = 0.6f), CircleShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (role.isNotBlank()) role else "Visa Ops",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate500
                )
            }
        }
    }
}

// -------------------------------------------------------------
// LEAVE BALANCES OVERVIEW (3 CARDS)
// -------------------------------------------------------------
@Composable
private fun LeaveBalancesSection(currentMonth: YearMonth) {
    val monthName = remember(currentMonth) {
        currentMonth.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Casual
        BalanceCard(
            title = "CASUAL",
            icon = Icons.Rounded.BeachAccess,
            iconTint = BrandRed,
            count = "04",
            countColor = BrandRed,
            statusLabel = "Available",
            footerLeft = "Pending",
            footerRight = "1 Req",
            footerBadgeBg = Amber50,
            footerBadgeText = Amber600,
            minWidth = 145
        )

        // Card 2: Sick
        BalanceCard(
            title = "SICK",
            icon = Icons.Rounded.Medication,
            iconTint = Teal600,
            count = "07",
            countColor = Slate900,
            statusLabel = "Available",
            footerLeft = "Pending",
            footerRight = "0 Req",
            footerBadgeBg = Emerald50,
            footerBadgeText = Emerald600,
            minWidth = 145
        )

        // Card 3: Loss of Pay
        BalanceCard(
            title = "LOP",
            icon = Icons.Rounded.EventBusy,
            iconTint = Slate400,
            count = "00",
            countColor = Slate900,
            statusLabel = "Taken ($monthName)",
            footerLeft = "Quota",
            footerRight = "Standard",
            footerBadgeBg = Color.Transparent,
            footerBadgeText = Slate500,
            minWidth = 135
        )
    }
}

@Composable
private fun BalanceCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    count: String,
    countColor: Color,
    statusLabel: String,
    footerLeft: String,
    footerRight: String,
    footerBadgeBg: Color,
    footerBadgeText: Color,
    minWidth: Int
) {
    Surface(
        modifier = Modifier.width(minWidth.dp),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, Slate100),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate400,
                    letterSpacing = 0.8.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Count
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = count,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = countColor,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "days",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate400,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Text(
                text = statusLabel,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Slate100)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = footerLeft,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate400
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(footerBadgeBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = footerRight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = footerBadgeText
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LEAVE TYPE SELECTOR
// -------------------------------------------------------------
@Composable
private fun LeaveTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LeaveTypeOptionButton(
            title = "Casual (CL)",
            icon = Icons.Rounded.BeachAccess,
            isSelected = selectedType.contains("Casual", ignoreCase = true),
            onClick = { onTypeSelected("Casual Leave") },
            modifier = Modifier.weight(1f)
        )
        LeaveTypeOptionButton(
            title = "Sick (SL)",
            icon = Icons.Rounded.MedicalServices,
            isSelected = selectedType.contains("Sick", ignoreCase = true),
            onClick = { onTypeSelected("Sick Leave") },
            modifier = Modifier.weight(1f)
        )
        LeaveTypeOptionButton(
            title = "Loss of Pay",
            icon = Icons.Rounded.MoneyOff,
            isSelected = selectedType.contains("Loss", ignoreCase = true) || selectedType.contains("LOP", ignoreCase = true),
            onClick = { onTypeSelected("Loss of Pay") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LeaveTypeOptionButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) BrandRed else Slate50,
        animationSpec = tween(150),
        label = "btnBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Slate600,
        animationSpec = tween(150),
        label = "btnContent"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) BrandRed else Slate100,
        animationSpec = tween(150),
        label = "btnBorder"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------------------------------------------
// CUSTOM CALENDAR MATRIX
// -------------------------------------------------------------
@Composable
private fun LeaveCalendarSection(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    selectedStartDate: LocalDate,
    selectedEndDate: LocalDate,
    isHalfDay: Boolean,
    onDateClicked: (LocalDate) -> Unit
) {
    val monthTitle = remember(currentMonth) {
        currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Slate50)
            .border(1.dp, Slate100, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Month Header with Prev/Next buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .shadow(1.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceCard)
                            .border(1.dp, Slate100, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = "Month",
                            tint = BrandRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = monthTitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        onClick = onPreviousMonth,
                        modifier = Modifier.size(28.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, Slate100),
                        shadowElevation = 0.5.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.ChevronLeft,
                                contentDescription = "Prev",
                                tint = Slate700,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        onClick = onNextMonth,
                        modifier = Modifier.size(28.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, Slate100),
                        shadowElevation = 0.5.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = "Next",
                                tint = Slate700,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekday Headers: M, T, W, T, F, S, S (Sunday in Red)
            val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEachIndexed { index, dayLetter ->
                    Text(
                        text = dayLetter,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 6) BrandRed else Slate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Days Matrix (7 columns)
            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1 = Mon ... 7 = Sun
            val daysInMonth = currentMonth.lengthOfMonth()
            val prevMonth = currentMonth.minusMonths(1)
            val daysInPrevMonth = prevMonth.lengthOfMonth()

            val totalLeading = firstDayOfWeek - 1
            val totalCells = ((totalLeading + daysInMonth + 6) / 7) * 7

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (row in 0 until (totalCells / 7)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - totalLeading + 1

                            if (dayNumber in 1..daysInMonth) {
                                val thisDate = currentMonth.atDay(dayNumber)
                                val isStart = thisDate == selectedStartDate
                                val isEnd = thisDate == selectedEndDate
                                val isInRange = !isHalfDay && thisDate > selectedStartDate && thisDate < selectedEndDate
                                val isSelected = isStart || isEnd
                                val isSunday = (col == 6)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                        .padding(horizontal = if (isSelected || isInRange) 0.5.dp else 2.dp)
                                        .clip(
                                            when {
                                                isStart && isEnd -> RoundedCornerShape(12.dp)
                                                isStart -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                                                isEnd -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                                                isInRange -> RoundedCornerShape(0.dp)
                                                else -> RoundedCornerShape(8.dp)
                                            }
                                        )
                                        .background(
                                            when {
                                                isSelected -> BrandRed
                                                isInRange -> BrandLightRed
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { onDateClicked(thisDate) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected || isInRange) FontWeight.Bold else FontWeight.SemiBold,
                                        color = when {
                                            isSelected -> Color.White
                                            isInRange -> BrandRed
                                            isSunday -> BrandRed
                                            else -> Slate700
                                        }
                                    )
                                }
                            } else {
                                // Muted Overflow Days
                                val overflowDay = if (dayNumber <= 0) {
                                    daysInPrevMonth + dayNumber
                                } else {
                                    dayNumber - daysInMonth
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = overflowDay.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate300
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LEAVE DURATION & HALF DAY SWITCH
// -------------------------------------------------------------
@Composable
private fun LeaveDurationToggle(
    isHalfDay: Boolean,
    onToggle: () -> Unit,
    selectedSession: String,
    onSessionSelected: (String) -> Unit,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val durationText = remember(startDate, endDate, isHalfDay) {
        if (isHalfDay) {
            "0.5 Day (Half Day)"
        } else {
            val days = ChronoUnit.DAYS.between(startDate, endDate) + 1
            if (days <= 1) "1 Day" else "$days Days"
        }
    }

    val selectedDatesFormatted = remember(startDate, endDate, isHalfDay) {
        val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
        if (isHalfDay || startDate == endDate) {
            startDate.format(formatter)
        } else {
            "${startDate.format(formatter)} – ${endDate.format(formatter)}"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timelapse,
                    contentDescription = "Duration",
                    tint = Slate400,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "LEAVE DURATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 0.8.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Half Day",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate600
                )

                // Custom Pill Switch
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 20.dp)
                        .clip(CircleShape)
                        .background(if (isHalfDay) BrandRed else Slate200)
                        .clickable(onClick = onToggle)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = if (isHalfDay) 20.dp else 0.dp)
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }
        }

        // Half Day Session Pills
        AnimatedVisibility(visible = isHalfDay) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SessionOptionButton(
                    title = "First Half (Morning)",
                    icon = Icons.Rounded.WbSunny,
                    isSelected = selectedSession == "First Half",
                    onClick = { onSessionSelected("First Half") },
                    modifier = Modifier.weight(1f)
                )
                SessionOptionButton(
                    title = "Second Half (Afternoon)",
                    icon = Icons.Rounded.Bedtime,
                    isSelected = selectedSession == "Second Half",
                    onClick = { onSessionSelected("Second Half") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Summary Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Slate200.copy(alpha = 0.7f))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrandRed)
                )
                Text(
                    text = "Selected: ",
                    fontSize = 12.sp,
                    color = Slate700
                )
                Text(
                    text = selectedDatesFormatted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandRed
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isHalfDay) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Slate100)
                            .border(1.dp, Slate200.copy(alpha = 0.7f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = selectedSession,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate600
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BrandLightRed)
                        .border(1.dp, BrandRedBorder, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = durationText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandRed
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionOptionButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) BrandRed else SurfaceCard,
        border = BorderStroke(1.dp, if (isSelected) BrandRed else Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else Slate400,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Slate600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -------------------------------------------------------------
// REASON FOR LEAVE WITH QUICK TAGS
// -------------------------------------------------------------
@Composable
private fun LeaveReasonInput(
    reason: String,
    onReasonChange: (String) -> Unit,
    onTagSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REASON FOR LEAVE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "${reason.length}/150",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Slate400
            )
        }

        // Quick Tags Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Family Event", "Medical checkup", "Personal matter").forEach { tag ->
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Slate100)
                        .clickable { onTagSelected(tag) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "+ $tag",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                }
            }
        }

        // Multiline Text Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Slate50)
                .border(1.dp, Slate200.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (reason.isEmpty()) {
                Text(
                    text = "E.g., Attending family wedding, medical checkup at Aster Medcity...",
                    fontSize = 12.sp,
                    color = Slate400
                )
            }
            BasicTextField(
                value = reason,
                onValueChange = onReasonChange,
                modifier = Modifier.fillMaxSize(),
                textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = Slate800,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(BrandRed)
            )
        }
    }
}

// -------------------------------------------------------------
// APPROVER HANDOVER NOTE
// -------------------------------------------------------------
@Composable
private fun ApproverHandoverCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Slate50,
        border = BorderStroke(1.dp, Slate100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate200.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.SupervisorAccount,
                    contentDescription = "Approver",
                    tint = Slate700,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reporting Manager: Dinesh Pillai",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate800,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Request routed automatically to Dubai HQ Desk",
                    fontSize = 11.sp,
                    color = Slate400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------
// INTERACTIVE SWIPE-TO-SUBMIT SLIDER
// -------------------------------------------------------------
@Composable
private fun SwipeToSubmitSlider(
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dragOffsetX = remember { Animatable(0f) }
    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    val knobSizeDp = 48.dp
    val knobSizePx = with(LocalDensity.current) { knobSizeDp.toPx() }
    val maxDragDistance = (trackWidthPx - knobSizePx - 8f).coerceAtLeast(0f)
    var isSubmitted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Slate200.copy(alpha = 0.7f))
            .border(1.dp, Slate200, RoundedCornerShape(28.dp))
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Dynamic expanding fill
        Box(
            modifier = Modifier
                .height(48.dp)
                .width(
                    with(LocalDensity.current) {
                        (dragOffsetX.value + knobSizePx).toDp()
                    }
                )
                .clip(RoundedCornerShape(24.dp))
                .background(if (isSubmitted) Emerald500.copy(alpha = 0.2f) else BrandRed.copy(alpha = 0.15f))
        )

        // Center Label
        val progress = if (maxDragDistance > 0f) dragOffsetX.value / maxDragDistance else 0f
        val labelAlpha = (1f - progress * 1.5f).coerceIn(0f, 1f)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSubmitted) {
                Text(
                    text = "Leave Requested",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald600
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.offset(x = 12.dp)
                ) {
                    Text(
                        text = "Slide to Submit Request",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500.copy(alpha = labelAlpha)
                    )
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = Slate400.copy(alpha = labelAlpha),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Draggable Knob
        val knobBg = if (isSubmitted) Emerald600 else BrandRed
        val knobIcon = if (isSubmitted) Icons.Rounded.TaskAlt else Icons.Rounded.KeyboardDoubleArrowRight

        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffsetX.value.roundToInt(), 0) }
                .size(knobSizeDp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(knobBg)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (dragOffsetX.value >= maxDragDistance * 0.75f) {
                                coroutineScope.launch {
                                    dragOffsetX.animateTo(
                                        targetValue = maxDragDistance,
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                    )
                                    isSubmitted = true
                                    onSubmit()
                                }
                            } else {
                                coroutineScope.launch {
                                    dragOffsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                    )
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val next = (dragOffsetX.value + dragAmount.x).coerceIn(0f, maxDragDistance)
                            coroutineScope.launch {
                                dragOffsetX.snapTo(next)
                            }
                        }
                    )
                }
                .clickable {
                    // Fallback on tap
                    coroutineScope.launch {
                        dragOffsetX.animateTo(maxDragDistance)
                        isSubmitted = true
                        onSubmit()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = knobIcon,
                contentDescription = "Submit",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// SUBMISSION SUCCESS CARD
// -------------------------------------------------------------
@Composable
private fun SubmissionSuccessCard(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Emerald50,
        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.TaskAlt,
                contentDescription = "Success",
                tint = Emerald600,
                modifier = Modifier.size(22.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Leave Request Submitted!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF064E3B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (message.isNotBlank()) message else "Reference ID: #LR-2026-088. Approver notification sent via SkyTrack dispatch.",
                    fontSize = 11.sp,
                    color = Color(0xFF065F46)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// RECENT LEAVE HISTORY SECTION
// -------------------------------------------------------------
@Composable
private fun RecentLeaveHistorySection(leaves: List<LeaveDto>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "RECENT REQUESTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Slate400,
            letterSpacing = 0.8.sp
        )

        leaves.take(4).forEach { leave ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, Slate100),
                shadowElevation = 0.5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "${leave.from.orEmpty()} → ${leave.to.orEmpty()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "${leave.type.orEmpty()} • ${leave.reason.orEmpty()}",
                            fontSize = 11.sp,
                            color = Slate500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    val status = leave.status.orEmpty().lowercase()
                    val (badgeBg, badgeText) = when {
                        status.contains("approv") -> Emerald50 to Emerald600
                        status.contains("reject") -> BrandLightRed to BrandRed
                        else -> Amber50 to Amber600
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = leave.status.orEmpty().ifBlank { "Pending" },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// FLOATING BOTTOM NAVIGATION DOCK (ACTIVE ON LEAVES)
// -------------------------------------------------------------
@Composable
fun LeaveBottomNavigationDock(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    com.skypass.skytrack.ui.components.SkyTrackBottomNavigationDock(
        currentTab = com.skypass.skytrack.ui.components.SkyTrackNavTab.LEAVES,
        modifier = modifier,
        onHomeClick = onHomeClick,
        onLeavesClick = { /* Already on Leaves */ },
        onAnalyticsClick = onAnalyticsClick,
        onProfileClick = onProfileClick
    )
}
