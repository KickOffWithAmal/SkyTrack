package com.skypass.skytrack.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BrandColor = Color(0xFFF24E1E)
val InputBgColor = Color(0xFFF4F5F7)
val LabelDarkColor = Color(0xFF1E232A)
val Slate400 = Color(0xFF94A3B8)
val Slate800 = Color(0xFF1E293B)

@Composable
fun LoginScreen(
    state: LoginState,
    onLogin: (String, String) -> Unit,
    onSuccess: () -> Unit,
    onClearError: () -> Unit
) {
    var employeeId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val loading = state is LoginState.Loading

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            modifier = Modifier
                .padding(top = 48.dp, bottom = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SKYTRACK",
                color = BrandColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Login",
                color = LabelDarkColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CustomTextField(
                label = "Employee ID",
                value = employeeId,
                onValueChange = {
                    employeeId = it.uppercase()
                    onClearError()
                },
                placeholder = "e.g. SK001",
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                label = "Password",
                value = password,
                onValueChange = {
                    password = it
                    onClearError()
                },
                placeholder = "Enter password",
                isPassword = true,
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (state is LoginState.Error) {
                Text(
                    text = state.message,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { onLogin(employeeId, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = BrandColor.copy(alpha = 0.5f)
                    ),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandColor,
                    disabledContainerColor = BrandColor.copy(alpha = 0.5f)
                ),
                enabled = !loading
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // SkyTrack Global Loading Dialog
        com.skypass.skytrack.ui.components.SkyTrackLoadingOverlay(visible = loading)

        // Footer
        Text(
            text = "SECURED BY SKYTRACK",
            color = Slate400,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Slate800,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(InputBgColor, RoundedCornerShape(16.dp))
                .border(
                    width = if (isFocused) 2.dp else 0.dp,
                    color = if (isFocused) BrandColor.copy(alpha = 0.2f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && !isFocused) {
                Text(
                    text = placeholder,
                    color = Slate400,
                    fontSize = 15.sp
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = TextStyle(
                    color = Slate800,
                    fontSize = if (isPassword) 18.sp else 15.sp,
                    letterSpacing = if (isPassword) 4.sp else 0.sp
                ),
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    capitalization = if (!isPassword) KeyboardCapitalization.Characters else KeyboardCapitalization.None,
                    keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
                ),
                cursorBrush = SolidColor(BrandColor),
                enabled = enabled
            )
        }
    }
}
