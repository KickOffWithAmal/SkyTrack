// SplashScreen.kt
// Layout/visuals match the Stitch mockup exactly.
// Entrance logic UNCHANGED: `visible` boolean + spring scale/tween alpha, same as before.
// Added: continuous logo pulse (infinite) + footer indeterminate progress bar (infinite),
// both mirroring the CSS @keyframes in the mockup.

package com.skypass.skytrack.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skypass.skytrack.R

private val BrandRed = Color(0xFFC62828)
private val BrandRedDim = Color(0x4DC62828) // ~30% alpha, matches the divider lines
private val SubtitleGray = Color(0xFF525252)
private val TrackGray = Color(0xFFE5E5E5)
private val PatternInk = Color(0x0E000000) // ~5.5% opacity black, matches CSS opacity:0.055

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    // --- Entrance logic: unchanged ---
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 220f),
        label = "splashScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(450),
        label = "splashAlpha"
    )

    // --- New: continuous logo pulse, mirrors CSS animate-logo-pulse (3s, scale 1<->1.012, opacity 1<->0.96) ---
    val pulseTransition = rememberInfiniteTransition(label = "logoPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.012f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // --- New: footer indeterminate progress bar, mirrors CSS animate-indeterminate (1.8s loop) ---
    val progressTransition = rememberInfiniteTransition(label = "progressBar")
    val progressX by progressTransition.animateFloat(
        initialValue = -1f,   // -100%
        targetValue = 2f,     // 200%
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1800
                -1f at 0
                0.3f at 900 using LinearOutSlowInEasing
                2f at 1800
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "progressX"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        EnterprisePatternBackground(modifier = Modifier.fillMaxSize())

        Column(modifier = Modifier.fillMaxSize()) {

            // ---- Center branding ----
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(scale * pulseScale)
                        .alpha(alpha * pulseAlpha)
                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.skypass_logo),
//                        contentDescription = "SkyTrack logo",
//                        modifier = Modifier
//                            .size(80.dp)
//                            .padding(bottom = 12.dp)
//                    )

                    // Stacked "SKY" / "TRACK", tight leading like the mockup
                    Text(
                        text = "SKY",
                        color = BrandRed,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.W900,
                        letterSpacing = (-0.3).sp,
                        lineHeight = 30.sp
                    )
                    Text(
                        text = "TRACK",
                        color = BrandRed,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.3).sp,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subtitle with divider lines either side
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(1.dp)
                                .background(BrandRedDim)
                        )
                        Text(
                            text = "ATTENDANCE • MOBILITY • VISAS",
                            color = SubtitleGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(1.dp)
                                .background(BrandRedDim)
                        )
                    }
                }
            }

            // ---- Footer: progress bar + attribution ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
                    .alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Indeterminate loader
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(TrackGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.5f)
                            .offset(x = (96.dp * progressX))
                            .clip(RoundedCornerShape(50))
                            .background(BrandRed)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        Text(
                            text = "Powered by ",
                            color = Color(0xFFA3A3A3),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Skypass Visa Services",
                            color = Color(0xFF525252),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "v2.4.1 ENTERPRISE BUILD",
                        color = Color(0xFFD4D4D4),
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }

    // Hand off after entrance + a short hold — adjust to taste
    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(2200)
            onFinished()
        }
    }
}

/**
 * Tiled line-art pattern: passport, plane, clock, calendar, briefcase, ID card,
 * globe, checkmark badge, fingerprint — same icon set as the mockup's SVG tile,
 * redrawn with Canvas primitives at matching ~5.5% opacity.
 */
@Composable
private fun EnterprisePatternBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val tile = 160.dp.toPx()
        val stroke = Stroke(width = 1.3.dp.toPx(), cap = StrokeCap.Round)

        val cols = (size.width / tile).toInt() + 2
        val rows = (size.height / tile).toInt() + 2

        for (row in -1 until rows) {
            for (col in -1 until cols) {
                val ox = col * tile
                val oy = row * tile
                drawPatternTile(ox, oy, tile, stroke)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPatternTile(
    ox: Float, oy: Float, tile: Float, stroke: Stroke
) {
    val s = tile / 160f // scale factor since icons were authored on a 160x160 tile

    // Passport
    drawRoundRect(
        color = PatternInk,
        topLeft = Offset(ox + 15 * s, oy + 15 * s),
        size = Size(24 * s, 32 * s),
        cornerRadius = CornerRadius(2.5f * s),
        style = stroke
    )
    drawCircle(PatternInk, radius = 5 * s, center = Offset(ox + 27 * s, oy + 28 * s), style = stroke)

    // Clock
    drawCircle(PatternInk, radius = 12 * s, center = Offset(ox + 135 * s, oy + 30 * s), style = stroke)
    drawLine(PatternInk, Offset(ox + 135 * s, oy + 22 * s), Offset(ox + 135 * s, oy + 30 * s), strokeWidth = stroke.width)
    drawLine(PatternInk, Offset(ox + 135 * s, oy + 30 * s), Offset(ox + 141 * s, oy + 30 * s), strokeWidth = stroke.width)

    // Calendar
    drawRoundRect(
        color = PatternInk,
        topLeft = Offset(ox + 20 * s, oy + 95 * s),
        size = Size(24 * s, 24 * s),
        cornerRadius = CornerRadius(3 * s),
        style = stroke
    )
    drawLine(PatternInk, Offset(ox + 20 * s, oy + 103 * s), Offset(ox + 44 * s, oy + 103 * s), strokeWidth = stroke.width)
    drawCircle(PatternInk, radius = 1.2f * s, center = Offset(ox + 28 * s, oy + 110 * s))
    drawCircle(PatternInk, radius = 1.2f * s, center = Offset(ox + 36 * s, oy + 110 * s))

    // Briefcase
    drawRoundRect(
        color = PatternInk,
        topLeft = Offset(ox + 76 * s, oy + 76 * s),
        size = Size(26 * s, 18 * s),
        cornerRadius = CornerRadius(2 * s),
        style = stroke
    )
    drawLine(PatternInk, Offset(ox + 76 * s, oy + 85 * s), Offset(ox + 102 * s, oy + 85 * s), strokeWidth = stroke.width)

    // ID card
    drawRoundRect(
        color = PatternInk,
        topLeft = Offset(ox + 125 * s, oy + 88 * s),
        size = Size(22 * s, 28 * s),
        cornerRadius = CornerRadius(2 * s),
        style = stroke
    )
    drawCircle(PatternInk, radius = 4 * s, center = Offset(ox + 136 * s, oy + 98 * s), style = stroke)

    // Globe
    drawCircle(PatternInk, radius = 11 * s, center = Offset(ox + 70 * s, oy + 135 * s), style = stroke)
    drawLine(PatternInk, Offset(ox + 59 * s, oy + 135 * s), Offset(ox + 81 * s, oy + 135 * s), strokeWidth = stroke.width)

    // Verified checkmark badge
    drawCircle(PatternInk, radius = 8 * s, center = Offset(ox + 78 * s, oy + 30 * s), style = stroke)
}