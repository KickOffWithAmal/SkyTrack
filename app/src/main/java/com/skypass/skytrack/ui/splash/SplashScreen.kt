package com.skypass.skytrack.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skypass.skytrack.R
import kotlinx.coroutines.delay

val ShortStackFontFamily = FontFamily(
    Font(R.font.short_stack, FontWeight.Normal)
)

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 0.92f,
            animationSpec = tween(durationMillis = 1400, easing = LinearEasing)
        )
        delay(200)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
        delay(250)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)) // surface-container-lowest
    ) {
        // Ambient Atmospheric Backlight - Top
//        Box(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .offset(y = (-128).dp) // -top-32
//                .size(320.dp) // w-80 h-80
//                .clip(CircleShape)
//                .background(Color(0xFFFFDAD6).copy(alpha = 0.3f)) // primary-fixed/30
//                .blur(radius = 64.dp) // blur-3xl
//        )

        // Ambient Atmospheric Backlight - Bottom
//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .offset(y = 96.dp) // -bottom-24
//                .size(384.dp) // w-96 h-96
//                .clip(CircleShape)
//                .background(Color(0xFFFFDAD6).copy(alpha = 0.2f)) // primary-fixed/20
//                .blur(radius = 64.dp) // blur-3xl
//        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp) // px-space-xl py-space-xl
                .windowInsetsPadding(WindowInsets.safeDrawing), // pt-safe pb-safe
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Spacing Anchor
            Spacer(modifier = Modifier.height(24.dp))

            // Center Hero Brand Unit
            Column(
                modifier = Modifier.offset(y = (-32).dp), // -mt-8
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Distinctive App Logo Mark
                Image(
                    painter = painterResource(id = R.drawable.skytrack_logo),
                    contentDescription = "SkyTrack Logo",
                    modifier = Modifier
                        .size(110.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Fit
                )

                // SkyTrack Title
                Text(
                    text = "SkyTrack",
                    fontFamily = ShortStackFontFamily,
                    color = Color(0xFF1A1B1E), // on-surface
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Normal, // The user used Short Stack font which isn't bundled. This falls back cleanly.
                    letterSpacing = 0.sp,
                    modifier = Modifier.padding(bottom = 20.dp) // mb-5
                )

                // Sleek Precision Crimson Progress Loader
                Box(
                    modifier = Modifier
                        .width(176.dp) // w-44
                        .height(4.dp) // h-1
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE9E7EB)) // surface-container-high
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress.value)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFC62828)) // primary-container
                    )
                }

                // Sub-status Micro-label
                Row(
                    modifier = Modifier.padding(top = 12.dp), // mt-3
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp) // gap-1.5
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "alpha")
                    val alphaPulse by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ), label = "alphaPulse"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp) // w-1.5 h-1.5
                            .clip(CircleShape)
                            .background(Color(0xFFC62828).copy(alpha = alphaPulse)) // primary-container
                    )
                    Text(
                        text = "SYNCING WORKSPACE",
                        color = Color(0xFF5B403D).copy(alpha = 0.7f), // on-surface-variant/70
                        fontSize = 10.sp, // label-sm
                        fontWeight = FontWeight.SemiBold, // label-sm
                        letterSpacing = 1.6.sp // tracking-wider
                    )
                }
            }

            // Bottom Institutional Endorsement
            Column(
                modifier = Modifier.padding(bottom = 8.dp), // mb-2
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp) // gap-1.5
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // gap-2
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp) // w-1.5 h-1.5
                            .clip(CircleShape)
                            .background(Color(0xFFA20513)) // primary
                    )
                    Text(
                        text = "SkyTrack",
                        color = Color(0xFF5B403D), // on-surface-variant
                        fontSize = 12.sp, // label-md
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp // tracking-wide
                    )
                    Text(
                        text = "•",
                        color = Color(0xFF8F706C), // outline
                        fontSize = 12.sp // body-sm
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "powered by",
                            color = Color(0xFF5B403D), // on-surface-variant
                            fontSize = 12.sp // body-sm
                        )
                        Text(
                            text = "Skypass Visa Services",
                            color = Color(0xFF1A1B1E), // on-surface
                            fontSize = 12.sp, // label-md
                            fontWeight = FontWeight.SemiBold // label-md
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // gap-1
                ) {
                    Icon(
                        imageVector = Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFF8F706C).copy(alpha = 0.8f), // outline/80
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "SECURE ENTERPRISE MOBILITY",
                        color = Color(0xFF8F706C).copy(alpha = 0.8f), // outline/80
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold, // label-sm
                        letterSpacing = 2.sp // tracking-widest
                    )
                }
            }
        }
    }
}
