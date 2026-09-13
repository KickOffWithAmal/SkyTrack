package com.skypass.skytrack

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.AccessAlarm
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.FreeBreakfast
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen
import androidx.lifecycle.lifecycleScope

import com.skypass.skytrack.data.local.Session
import com.skypass.skytrack.data.repository.AuthRepository
import com.skypass.skytrack.ui.components.MainTab
import com.skypass.skytrack.ui.home.HomeScreen
import com.skypass.skytrack.ui.home.HomeViewModel
import com.skypass.skytrack.ui.leave.LeaveScreen
import com.skypass.skytrack.ui.leave.LeaveViewModel
import com.skypass.skytrack.ui.login.LoginScreen
import com.skypass.skytrack.ui.login.LoginViewModel
import com.skypass.skytrack.ui.monthly.MonthlyStatisticsScreen
import com.skypass.skytrack.ui.monthly.MonthlyStatisticsViewModel
import com.skypass.skytrack.ui.splash.SplashScreen
import com.skypass.skytrack.ui.theme.SkyTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepository: AuthRepository

    private val loginViewModel: LoginViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val leaveViewModel: LeaveViewModel by viewModels()
    private val monthlyViewModel: MonthlyStatisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SkyTrackTheme {
                val session by authRepository.session.collectAsState(initial = null)
                var splashVisible by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2000)
                    splashVisible = false
                }

                if (splashVisible) {
                    SplashScreen()
                } else if (session == null) {
                    LoginScreen(
                        state = loginViewModel.state.collectAsState().value,
                        onLogin = loginViewModel::login,
                        onSuccess = {},
                        onClearError = loginViewModel::clearError
                    )
                } else {
                    val currentSession = session!!

                    MainApp(
                        session = currentSession,
                        homeViewModel = homeViewModel,
                        leaveViewModel = leaveViewModel,
                        monthlyViewModel = monthlyViewModel,
                        onLogout = {
                            lifecycleScope.launch { authRepository.logout() }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainApp(
    session: Session,
    homeViewModel: HomeViewModel,
    leaveViewModel: LeaveViewModel,
    monthlyViewModel: MonthlyStatisticsViewModel,
    onLogout: () -> Unit
) {
    var tab by remember { mutableStateOf(MainTab.HOME) }
    var showProfile by remember { mutableStateOf(false) }

    when (tab) {
        MainTab.HOME -> HomeScreen(
            session = session,
            viewModel = homeViewModel,
            onLogout = onLogout,
            onOpenLeave = { tab = MainTab.LEAVE },
            onOpenMonthlyStatistics = { tab = MainTab.STATISTICS },
            onOpenProfile = { showProfile = true }
        )
        MainTab.LEAVE -> LeaveScreen(
            session = session,
            viewModel = leaveViewModel,
            onHome = { tab = MainTab.HOME },
            onStatistics = { tab = MainTab.STATISTICS },
            onOpenProfile = { showProfile = true }
        )
        MainTab.STATISTICS -> MonthlyStatisticsScreen(
            session = session,
            viewModel = monthlyViewModel,
            onHome = { tab = MainTab.HOME },
            onLeave = { tab = MainTab.LEAVE },
            onOpenProfile = { showProfile = true }
        )
    }

    if (showProfile) {
        AlertDialog(
            onDismissRequest = { showProfile = false },
            title = { Text("Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(session.name, style = MaterialTheme.typography.titleLarge)
                    Text("Employee ID: ${session.employeeId}")
                    Text("Role: ${session.role}")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showProfile = false }) { Text("Close") }
            },
            confirmButton = {
                Button(onClick = {
                    showProfile = false
                    onLogout()
                }) {
                    IconExit()
                    Spacer(Modifier.size(6.dp))
                    Text("Sign Out")
                }
            }
        )
    }
}

@Composable
private fun IconExit() {
    androidx.compose.material3.Icon(
        Icons.Default.ExitToApp,
        contentDescription = null,
        modifier = Modifier.size(18.dp)
    )
}

// ---- Brand colors, sampled from the icon ----
private val BgBlack = Color(0xFF0A0A0A)
private val GoldStart = Color(0xFFD9B36A)
private val GoldEnd = Color(0xFF8B6B3D)
private val SilverStart = Color(0xFFE8E8E8)
private val SilverEnd = Color(0xFFB8C4CC)
private val SubtitleGray = Color(0xFF9A9A9A)

//@Composable
//private fun SplashScreen() {
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { visible = true }
//
//    val scale by androidx.compose.animation.core.animateFloatAsState(
//        targetValue = if (visible) 1f else 0.72f,
//        animationSpec = androidx.compose.animation.core.spring(
//            dampingRatio = 0.72f,
//            stiffness = 220f
//        ),
//        label = "splashScale"
//    )
//    val alpha by androidx.compose.animation.core.animateFloatAsState(
//        targetValue = if (visible) 1f else 0f,
//        animationSpec = androidx.compose.animation.core.tween(500),
//        label = "splashAlpha"
//    )
//
//    Box(
//        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Surface(
//                modifier = Modifier.scale(scale),
//                shape = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
//                color = Color.White,
//                shadowElevation = 10.dp
//            ) {
//                Box(
//                    Modifier.padding(22.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Image(
//                        painter = painterResource(R.drawable.skytrack_logo),
//                        contentDescription = "SkyTrack logo",
//                        modifier = Modifier.size(82.dp)
//                    )
//                }
//            }
//            Spacer(Modifier.height(18.dp))
//            Text("SkyTrack", style = MaterialTheme.typography.displayLarge, modifier = Modifier.scale(scale))
//            Text(
//                "Employee Attendance & Leave",
//                color = com.skypass.skytrack.ui.theme.SkyTextSecondary,
//                modifier = Modifier.scale(scale)
//            )
//        }
//    }
//}
// Requires: implementation("androidx.compose.material:material-icons-extended")
// Requires: implementation("androidx.compose.material:material-icons-extended")
