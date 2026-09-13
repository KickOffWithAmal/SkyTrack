//package com.skypass.skytrack.ui.login
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.systemBarsPadding
//import androidx.compose.foundation.gestures.detectHorizontalDragGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Badge
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material.icons.filled.Fingerprint
//import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Checkbox
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.IntOffset
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.skypass.skytrack.ui.components.SkyTrackLogo
//import com.skypass.skytrack.ui.theme.SkyRed
//import com.skypass.skytrack.ui.theme.SkyRedPale
//import com.skypass.skytrack.ui.theme.SkyTextSecondary
//import kotlinx.coroutines.launch
//import kotlin.math.roundToInt
//
//@Composable
//fun LoginScreen(
//    state: LoginState,
//    onLogin: (String, String) -> Unit,
//    onSuccess: () -> Unit,
//    onClearError: () -> Unit
//) {
//    var employeeId by rememberSaveable { mutableStateOf("") }
//    var password by rememberSaveable { mutableStateOf("") }
//    var passwordVisible by rememberSaveable { mutableStateOf(false) }
//    var verified by rememberSaveable { mutableStateOf(true) }
//    val loading = state is LoginState.Loading
//
//    LaunchedEffect(state) {
//        if (state is LoginState.Success) onSuccess()
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.background)
//            .padding(horizontal = 20.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(Modifier.height(44.dp))
//        SkyTrackLogo(size = 88.dp)
//        Spacer(Modifier.height(14.dp))
//        Text("SkyTrack", style = MaterialTheme.typography.displayLarge)
//        Text(
//            "Employee Attendance & Leave",
//            style = MaterialTheme.typography.bodyLarge,
//            color = SkyTextSecondary
//        )
//        Spacer(Modifier.height(10.dp))
//        Surface(
//            shape = RoundedCornerShape(50),
//            color = MaterialTheme.colorScheme.surfaceVariant
//        ) {
//            Text(
//                "SKYPASS VISA SERVICES",
//                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
//                style = MaterialTheme.typography.labelMedium,
//                color = SkyTextSecondary
//            )
//        }
//
//        Spacer(Modifier.height(28.dp))
//
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(22.dp),
//            colors = CardDefaults.cardColors(containerColor = Color.White),
//            elevation = CardDefaults.cardElevation(1.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(22.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(Modifier.weight(1f)) {
//                        Text("Welcome back", style = MaterialTheme.typography.headlineLarge)
//                        Spacer(Modifier.height(4.dp))
//                        Text(
//                            "Sign in to continue to your workspace",
//                            color = SkyTextSecondary
//                        )
//                    }
//                    Surface(
//                        shape = RoundedCornerShape(50),
//                        color = SkyRedPale
//                    ) {
//                        Text(
//                            "●  Secure",
//                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
//                            color = SkyRed,
//                            style = MaterialTheme.typography.labelMedium
//                        )
//                    }
//                }
//
//                Spacer(Modifier.height(24.dp))
//
//                Text("Employee ID", fontWeight = FontWeight.SemiBold)
//                Spacer(Modifier.height(7.dp))
//                OutlinedTextField(
//                    value = employeeId,
//                    onValueChange = {
//                        employeeId = it.uppercase()
//                        onClearError()
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    placeholder = { Text("Enter your employee ID") },
//                    leadingIcon = { Icon(Icons.Default.Badge, null) },
//                    singleLine = true,
//                    enabled = !loading,
//                    shape = RoundedCornerShape(14.dp)
//                )
//
//                Spacer(Modifier.height(14.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("Password", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
//                    Text("Forgot password?", color = SkyRed, style = MaterialTheme.typography.labelLarge)
//                }
//                Spacer(Modifier.height(7.dp))
//                OutlinedTextField(
//                    value = password,
//                    onValueChange = {
//                        password = it
//                        onClearError()
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    placeholder = { Text("Enter your password") },
//                    leadingIcon = { Icon(Icons.Default.Lock, null) },
//                    trailingIcon = {
//                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                            Icon(
//                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
//                                contentDescription = "Toggle password visibility"
//                            )
//                        }
//                    },
//                    singleLine = true,
//                    enabled = !loading,
//                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                    shape = RoundedCornerShape(14.dp)
//                )
//
//                Spacer(Modifier.height(10.dp))
//
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Checkbox(checked = verified, onCheckedChange = { verified = it })
//                    Text("Keep me verified on this device")
//                    Spacer(Modifier.weight(1f))
//                    Icon(Icons.Default.Fingerprint, null, tint = SkyTextSecondary)
//                }
//
//                AnimatedVisibility(state is LoginState.Error) {
//                    if (state is LoginState.Error) {
//                        Text(
//                            state.message,
//                            color = MaterialTheme.colorScheme.error,
//                            modifier = Modifier.padding(bottom = 10.dp)
//                        )
//                    }
//                }
//
//                SwipeToLogin(
//                    enabled = !loading,
//                    loading = loading,
//                    onSwipe = { onLogin(employeeId, password) }
//                )
//
//                Spacer(Modifier.height(8.dp))
//                Text(
//                    "Swipe right to continue",
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
//                    color = SkyTextSecondary,
//                    style = MaterialTheme.typography.bodySmall
//                )
//
//                Spacer(Modifier.height(8.dp))
//
//                Button(
//                    onClick = { onLogin(employeeId, password) },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !loading,
//                    shape = RoundedCornerShape(14.dp)
//                ) {
//                    Text("Use button instead")
//                }
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//        Surface(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(18.dp),
//            color = MaterialTheme.colorScheme.surfaceVariant
//        ) {
//            Row(
//                modifier = Modifier.padding(14.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(Icons.Default.Lock, null, tint = SkyRed)
//                Spacer(Modifier.size(12.dp))
//                Column(Modifier.weight(1f)) {
//                    Text("Secure employee access", fontWeight = FontWeight.SemiBold)
//                    Text("Your session is protected by SkyTrack", color = SkyTextSecondary, style = MaterialTheme.typography.bodySmall)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SwipeToLogin(
//    enabled: Boolean,
//    loading: Boolean,
//    onSwipe: () -> Unit
//) {
//    var widthPx by remember { mutableFloatStateOf(1f) }
//    var drag by remember { mutableFloatStateOf(0f) }
//    val scope = rememberCoroutineScope()
//    val thumbSize = 60.dp
//    val density = LocalDensity.current
//    val maxDrag = with(density) { 60.dp.toPx() }
//    val progress = (drag / maxDrag).coerceIn(0f, 1f)
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(62.dp)
//            .clip(RoundedCornerShape(32.dp))
//            .background(SkyRedPale)
//            .pointerInput(enabled, widthPx) {
//                if (enabled) {
//                    detectHorizontalDragGestures(
//                        onHorizontalDrag = { _, amount ->
//                            drag = (drag + amount).coerceIn(0f, (widthPx - maxDrag - 8f).coerceAtLeast(0f))
//                        },
//                        onDragEnd = {
//                            if (drag > (widthPx - maxDrag) * 0.62f) {
//                                onSwipe()
//                                scope.launch {
//                                    drag = 0f
//                                }
//                            } else {
//                                scope.launch {
//                                    val start = drag
//                                    repeat(8) { step ->
//                                        drag = start * (1f - (step + 1) / 8f)
//                                        kotlinx.coroutines.delay(15)
//                                    }
//                                    drag = 0f
//                                }
//                            }
//                        }
//                    )
//                }
//            }
//            .then(Modifier)
//    ) {
//        androidx.compose.foundation.layout.BoxWithConstraints(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            LaunchedEffect(maxWidth) {
//                widthPx = with(density) { maxWidth.toPx() }
//            }
//        }
//
//        Text(
//            if (loading) "Signing in..." else "Swipe to Sign In  »",
//            modifier = Modifier.align(Alignment.Center),
//            color = SkyRed,
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold
//        )
//
//        Surface(
//            modifier = Modifier
//                .offset { IntOffset(drag.roundToInt(), 0) }
//                .padding(4.dp)
//                .size(54.dp),
//            shape = CircleShape,
//            color = SkyRed,
//            shadowElevation = 4.dp
//        ) {
//            Box(contentAlignment = Alignment.Center) {
//                if (loading) {
//                    androidx.compose.material3.CircularProgressIndicator(
//                        color = Color.White,
//                        strokeWidth = 2.dp,
//                        modifier = Modifier.size(22.dp)
//                    )
//                } else {
//                    Icon(Icons.Default.ChevronRight, null, tint = Color.White, modifier = Modifier.size(30.dp))
//                }
//            }
//        }
//    }
//}



package com.skypass.skytrack.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Palette from the mockup ----
private val Brand = Color(0xFFF24E1E)
private val BrandHover = Color(0xFFE03D0D)
private val InputBg = Color(0xFFF4F5F7)
private val LabelDark = Color(0xFF1E232A)

@Composable
fun LoginScreen(
    state: LoginState,
    onLogin: (String, String) -> Unit,
    onSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    // ---- State & logic: unchanged from the original ----
    var employeeId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val loading = state is LoginState.Loading

    LaunchedEffect(state) {
        if (state is LoginState.Success) onSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---- Header ----
        Spacer(Modifier.height(48.dp))
        Text(
            "SKYTRACK",
            color = Brand,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Login",
            color = LabelDark,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(32.dp))

        // ---- Form ----
        Column(
            modifier = Modifier.widthIn(max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(150.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Employee ID", color = Color(0xFF1E293B), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                TextField(
                    value = employeeId,
                    onValueChange = {
                        employeeId = it.uppercase()
                        onClearError()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("e.g. SK001", color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    enabled = !loading,
                    shape = RoundedCornerShape(16.dp),
                    colors = flatFieldColors()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Password", color = Color(0xFF1E293B), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        onClearError()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("Enter password", color = Color(0xFF94A3B8)) },
                    singleLine = true,
                    enabled = !loading,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = flatFieldColors()
                )
            }

            AnimatedVisibility(state is LoginState.Error) {
                if (state is LoginState.Error) {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onLogin(employeeId, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !loading,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brand,
                    disabledContainerColor = Brand.copy(alpha = 0.5f)
                )
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.height(20.dp)
                    )
                } else {
                    Text("Login", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // ---- Footer ----
        Text(
            "SECURED BY SKYTRACK",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
        )
    }
}

@Composable
private fun flatFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = InputBg,
    unfocusedContainerColor = InputBg,
    disabledContainerColor = InputBg,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    cursorColor = Brand
)


