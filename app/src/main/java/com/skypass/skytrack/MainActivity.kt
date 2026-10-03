package com.skypass.skytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.skypass.skytrack.data.repository.AuthRepository
import com.skypass.skytrack.ui.home.HomeScreen
import com.skypass.skytrack.ui.home.HomeViewModel
import com.skypass.skytrack.ui.leave.LeaveScreen
import com.skypass.skytrack.ui.leave.LeaveViewModel
import com.skypass.skytrack.ui.login.LoginScreen
import com.skypass.skytrack.ui.login.LoginViewModel
import com.skypass.skytrack.ui.monthly.MonthlyStatisticsScreen
import com.skypass.skytrack.ui.monthly.MonthlyStatisticsViewModel
import com.skypass.skytrack.ui.profile.ChangePasswordScreen
import com.skypass.skytrack.ui.profile.ProfileScreen
import com.skypass.skytrack.ui.profile.ProfileViewModel
import com.skypass.skytrack.ui.splash.SplashScreen
import com.skypass.skytrack.ui.theme.SkyTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private val loginViewModel: LoginViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val leaveViewModel: LeaveViewModel by viewModels()
    private val monthlyStatisticsViewModel: MonthlyStatisticsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SkyTrackTheme {
                Surface(modifier = Modifier.fillMaxSize()) {

                    val session by authRepository.session
                        .collectAsState(initial = null)

                    var showLeave by remember {
                        mutableStateOf(false)
                    }

                    var showMonthlyStatistics by remember {
                        mutableStateOf(false)
                    }

                    var showProfile by remember {
                        mutableStateOf(false)
                    }

                    var showChangePassword by remember {
                        mutableStateOf(false)
                    }

                    var showSplash by remember {
                        mutableStateOf(true)
                    }

                    when {
                        showSplash -> {
                            SplashScreen(
                                onFinished = {
                                    showSplash = false
                                }
                            )
                        }
                        session == null -> {
                            showLeave = false
                            showMonthlyStatistics = false
                            showProfile = false
                            showChangePassword = false

                            val rememberedAccount by loginViewModel.rememberedAccount.collectAsState()

                            LoginScreen(
                                state = loginViewModel.state.collectAsState().value,
                                onLogin = loginViewModel::login,
                                onSuccess = {},
                                onClearError = loginViewModel::clearError,
                                rememberedAccount = rememberedAccount
                            )
                        }

                        showChangePassword -> {
                            ChangePasswordScreen(
                                session = session!!,
                                onBack = {
                                    showChangePassword = false
                                },
                                onNavigateHome = {
                                    showChangePassword = false
                                    showProfile = false
                                    showLeave = false
                                    showMonthlyStatistics = false
                                },
                                onNavigateLeaves = {
                                    showChangePassword = false
                                    showProfile = false
                                    showMonthlyStatistics = false
                                    showLeave = true
                                },
                                onNavigateStatistics = {
                                    showChangePassword = false
                                    showProfile = false
                                    showLeave = false
                                    showMonthlyStatistics = true
                                }
                            )
                        }

                        showProfile -> {
                            ProfileScreen(
                                session = session!!,
                                viewModel = profileViewModel,
                                onLogout = {
                                    lifecycleScope.launch {
                                        authRepository.logout()
                                    }
                                },
                                onNavigateHome = {
                                    showProfile = false
                                    showLeave = false
                                    showMonthlyStatistics = false
                                },
                                onNavigateLeaves = {
                                    showProfile = false
                                    showMonthlyStatistics = false
                                    showLeave = true
                                },
                                onNavigateStatistics = {
                                    showProfile = false
                                    showLeave = false
                                    showMonthlyStatistics = true
                                },
                                onChangePasswordClick = {
                                    showChangePassword = true
                                }
                            )
                        }

                        showMonthlyStatistics -> {
                            MonthlyStatisticsScreen(
                                viewModel = monthlyStatisticsViewModel,
                                session = session!!,
                                onBack = {
                                    showMonthlyStatistics = false
                                },
                                onOpenLeave = {
                                    showMonthlyStatistics = false
                                    showLeave = true
                                },
                                onOpenProfile = {
                                    showMonthlyStatistics = false
                                    showLeave = false
                                    showProfile = true
                                }
                            )
                        }

                        showLeave -> {
                            LeaveScreen(
                                session = session!!,
                                viewModel = leaveViewModel,
                                onBack = {
                                    showLeave = false
                                },
                                onNavigateHome = {
                                    showLeave = false
                                },
                                onNavigateStatistics = {
                                    showLeave = false
                                    showMonthlyStatistics = true
                                },
                                onNavigateProfile = {
                                    showLeave = false
                                    showMonthlyStatistics = false
                                    showProfile = true
                                }
                            )
                        }

                        else -> {
                            HomeScreen(
                                session = session!!,
                                viewModel = homeViewModel,
                                onLogout = {
                                    lifecycleScope.launch {
                                        authRepository.logout()
                                    }
                                },
                                onOpenLeave = {
                                    showLeave = true
                                },
                                onOpenMonthlyStatistics = {
                                    showMonthlyStatistics = true
                                },
                                onOpenProfile = {
                                    showProfile = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
