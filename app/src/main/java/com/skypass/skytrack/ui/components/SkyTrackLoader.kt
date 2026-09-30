package com.skypass.skytrack.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

private val Crimson = Color(0xFFC62828)
private val DarkRed = Color(0xFFA20513)
private val BrandRed = Color(0xFFE53935)
private val LightRed = Color(0xFFFEE2E2)
private val SoftBorderRed = Color(0xFFFECACA)

private val Slate100 = Color(0xFFF1F5F9)
private val Slate200 = Color(0xFFE2E8F0)
private val Slate300 = Color(0xFFCBD5E1)
private val Slate400 = Color(0xFF94A3B8)
private val Slate500 = Color(0xFF64748B)
private val Slate600 = Color(0xFF475569)
private val Slate700 = Color(0xFF334155)
private val Slate800 = Color(0xFF1E293B)
private val Slate900 = Color(0xFF0F172A)

data class LandmarkSlideData(
    val cityCountry: String,
    val airportCode: String,
    val landmarkName: String
)

val LandmarkSlides = listOf(
    LandmarkSlideData("Paris, France", "CDG", "Eiffel Tower"),
    LandmarkSlideData("Tokyo, Japan", "HND", "Mt. Fuji & Pagoda"),
    LandmarkSlideData("New York, USA", "JFK", "Statue of Liberty"),
    LandmarkSlideData("Dubai, UAE", "DXB", "Burj Khalifa"),
    LandmarkSlideData("Rome, Italy", "FCO", "The Colosseum")
)

/**
 * Common SkyTrack Loading Dialog with Tourist Destination Landmark Carousel.
 * Displays during network operations, data fetching, or UI rendering lags.
 */
@Composable
fun SkyTrackLoadingOverlay(
    visible: Boolean,
    onDismissRequest: () -> Unit = {}
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF595D6E).copy(alpha = 0.45f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            SkyTrackLoaderCard()
        }
    }
}

@Composable
fun SkyTrackLoaderCard() {
    var currentSlideIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2800)
            currentSlideIndex = (currentSlideIndex + 1) % LandmarkSlides.size
        }
    }

    val slide = LandmarkSlides[currentSlideIndex]

    Box(
        modifier = Modifier
            .width(260.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .clip(RoundedCornerShape(24.dp))
//            .background(Color.White)
//            .border(1.dp, Slate100, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row: City badge and Airport code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // City Country Pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, LightRed, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Crimson)
                    )
                    Text(
                        text = slide.cityCountry,
                        color = Slate800,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Airport Code Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Slate200, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = slide.airportCode,
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Landmark Illustration Stage
            AnimatedContent(
                targetState = currentSlideIndex,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                            scaleIn(initialScale = 0.93f, animationSpec = tween(400)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(300, easing = LinearEasing)) +
                                    scaleOut(targetScale = 0.93f, animationSpec = tween(300))
                        )
                },
                label = "landmarkCarousel"
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (index) {
                        0 -> ParisEiffelTowerIllustration()
                        1 -> TokyoFujiPagodaIllustration()
                        2 -> NewYorkLibertyIllustration()
                        3 -> DubaiBurjKhalifaIllustration()
                        else -> RomeColosseumIllustration()
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Landmark Name Subtitle
            Text(
                text = slide.landmarkName,
                color = Slate500,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Carousel Progress Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LandmarkSlides.indices.forEach { dotIndex ->
                    val isSelected = dotIndex == currentSlideIndex
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 22.dp else 6.dp,
                        animationSpec = tween(350, easing = FastOutSlowInEasing),
                        label = "dotWidth"
                    )
                    val dotColor by animateColorAsState(
                        targetValue = if (isSelected) Crimson else Slate200,
                        animationSpec = tween(300),
                        label = "dotColor"
                    )
                    Box(
                        modifier = Modifier
                            .height(5.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}

/**
 * Slide 1: Paris (Eiffel Tower + Sun + Clouds)
 */
@Composable
private fun ParisEiffelTowerIllustration() {
    val cloudTransition = rememberInfiniteTransition(label = "clouds")
    val cloudOffsetA by cloudTransition.animateFloat(
        initialValue = -4f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudA"
    )
    val cloudOffsetB by cloudTransition.animateFloat(
        initialValue = 4f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudB"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val centerX = w / 2f

        // Warm Sun Accent (Top Right)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFDE68A), Color(0xFFFECACA).copy(alpha = 0.4f), Color.Transparent),
                center = Offset(w * 0.78f, h * 0.22f),
                radius = 32f
            ),
            center = Offset(w * 0.78f, h * 0.22f),
            radius = 24f
        )

        // Floating Clouds (Left)
        drawCloud(Offset(w * 0.16f + cloudOffsetA, h * 0.26f), scale = 0.85f)
        // Floating Cloud (Right)
        drawCloud(Offset(w * 0.80f + cloudOffsetB, h * 0.38f), scale = 0.7f)

        // Ground Baseline
        drawLine(
            color = Slate200,
            start = Offset(w * 0.12f, h * 0.95f),
            end = Offset(w * 0.88f, h * 0.95f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Eiffel Tower Spire
        drawLine(
            color = DarkRed,
            start = Offset(centerX, h * 0.08f),
            end = Offset(centerX, h * 0.22f),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Top Tier Dome
        val topTierPath = Path().apply {
            moveTo(centerX - 4.dp.toPx(), h * 0.22f)
            lineTo(centerX + 4.dp.toPx(), h * 0.22f)
            lineTo(centerX + 7.dp.toPx(), h * 0.34f)
            lineTo(centerX - 7.dp.toPx(), h * 0.34f)
            close()
        }
        drawPath(topTierPath, Crimson)

        // Top Platform Rect
        drawRoundRect(
            color = DarkRed,
            topLeft = Offset(centerX - 9.dp.toPx(), h * 0.34f),
            size = Size(18.dp.toPx(), 3.dp.toPx()),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
        )

        // Mid Tier Section
        val midTierPath = Path().apply {
            moveTo(centerX - 8.dp.toPx(), h * 0.37f)
            lineTo(centerX + 8.dp.toPx(), h * 0.37f)
            lineTo(centerX + 15.dp.toPx(), h * 0.60f)
            lineTo(centerX - 15.dp.toPx(), h * 0.60f)
            close()
        }
        drawPath(midTierPath, BrandRed)

        // Mid Tier Lattice Crosses (White)
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = Offset(centerX - 10.dp.toPx(), h * 0.40f),
            end = Offset(centerX + 10.dp.toPx(), h * 0.58f),
            strokeWidth = 1.2.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = Offset(centerX + 10.dp.toPx(), h * 0.40f),
            end = Offset(centerX - 10.dp.toPx(), h * 0.58f),
            strokeWidth = 1.2.dp.toPx()
        )

        // Mid Platform Rect
        drawRoundRect(
            color = DarkRed,
            topLeft = Offset(centerX - 20.dp.toPx(), h * 0.60f),
            size = Size(40.dp.toPx(), 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Bottom Arch & Main Legs
        val bottomPath = Path().apply {
            moveTo(centerX - 18.dp.toPx(), h * 0.64f)
            lineTo(centerX + 18.dp.toPx(), h * 0.64f)
            lineTo(centerX + 34.dp.toPx(), h * 0.95f)
            lineTo(centerX + 23.dp.toPx(), h * 0.95f)
            lineTo(centerX + 16.dp.toPx(), h * 0.76f)
            // Central inner arch curve
            cubicTo(
                centerX + 12.dp.toPx(), h * 0.70f,
                centerX - 12.dp.toPx(), h * 0.70f,
                centerX - 16.dp.toPx(), h * 0.76f
            )
            lineTo(centerX - 23.dp.toPx(), h * 0.95f)
            lineTo(centerX - 34.dp.toPx(), h * 0.95f)
            close()
        }
        drawPath(bottomPath, Crimson)

        // Arch Highlight Stroke
        val archLine = Path().apply {
            moveTo(centerX - 16.dp.toPx(), h * 0.95f)
            cubicTo(
                centerX - 12.dp.toPx(), h * 0.72f,
                centerX + 12.dp.toPx(), h * 0.72f,
                centerX + 16.dp.toPx(), h * 0.95f
            )
        }
        drawPath(
            archLine,
            Color.White.copy(alpha = 0.75f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

/**
 * Slide 2: Tokyo (Mount Fuji + Pagoda + Torii Gate + Rising Sun)
 */
@Composable
private fun TokyoFujiPagodaIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Rising Red Sun Accent (Top Left)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(BrandRed, Crimson),
                start = Offset(w * 0.16f, h * 0.12f),
                end = Offset(w * 0.32f, h * 0.32f)
            ),
            center = Offset(w * 0.24f, h * 0.22f),
            radius = 22f
        )

        // Mt. Fuji Mountain Silhouette
        val fujiPath = Path().apply {
            moveTo(w * 0.08f, h * 0.92f)
            lineTo(w * 0.38f, h * 0.36f)
            quadraticTo(w * 0.48f, h * 0.33f, w * 0.58f, h * 0.36f)
            lineTo(w * 0.88f, h * 0.92f)
            close()
        }
        drawPath(fujiPath, Color(0xFFE2E8F0).copy(alpha = 0.75f))

        // Mt. Fuji Snowcap (White)
        val snowPath = Path().apply {
            moveTo(w * 0.38f, h * 0.36f)
            quadraticTo(w * 0.48f, h * 0.33f, w * 0.58f, h * 0.36f)
            lineTo(w * 0.62f, h * 0.46f)
            lineTo(w * 0.56f, h * 0.50f)
            lineTo(w * 0.50f, h * 0.45f)
            lineTo(w * 0.44f, h * 0.51f)
            lineTo(w * 0.34f, h * 0.46f)
            close()
        }
        drawPath(snowPath, Color.White)

        // Ground Baseline
        drawLine(
            color = Slate200,
            start = Offset(w * 0.08f, h * 0.95f),
            end = Offset(w * 0.92f, h * 0.95f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Torii Gate on Left
        val toriiX = w * 0.22f
        // Curved top beam
        val toriiBeam = Path().apply {
            moveTo(toriiX - 16.dp.toPx(), h * 0.68f)
            quadraticTo(toriiX, h * 0.65f, toriiX + 16.dp.toPx(), h * 0.68f)
        }
        drawPath(toriiBeam, Crimson, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        // Straight sub-beam
        drawLine(DarkRed, Offset(toriiX - 12.dp.toPx(), h * 0.73f), Offset(toriiX + 12.dp.toPx(), h * 0.73f), strokeWidth = 2.dp.toPx())
        // Vertical pillars
        drawLine(Crimson, Offset(toriiX - 9.dp.toPx(), h * 0.68f), Offset(toriiX - 9.dp.toPx(), h * 0.95f), strokeWidth = 2.5.dp.toPx())
        drawLine(Crimson, Offset(toriiX + 9.dp.toPx(), h * 0.68f), Offset(toriiX + 9.dp.toPx(), h * 0.95f), strokeWidth = 2.5.dp.toPx())

        // Japanese Pagoda (Right Center)
        val pagodaX = w * 0.66f

        // Spire
        drawLine(DarkRed, Offset(pagodaX, h * 0.16f), Offset(pagodaX, h * 0.32f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)

        // Tier 1 Roof
        drawCurvedRoof(pagodaX, h * 0.35f, widthDp = 24.dp.toPx(), color = Crimson)
        drawRect(Slate100, Offset(pagodaX - 5.dp.toPx(), h * 0.36f), Size(10.dp.toPx(), 7.dp.toPx()))

        // Tier 2 Roof
        drawCurvedRoof(pagodaX, h * 0.48f, widthDp = 34.dp.toPx(), color = Crimson)
        drawRect(Slate300, Offset(pagodaX - 7.dp.toPx(), h * 0.49f), Size(14.dp.toPx(), 8.dp.toPx()))

        // Tier 3 Roof
        drawCurvedRoof(pagodaX, h * 0.63f, widthDp = 44.dp.toPx(), color = DarkRed)
        drawRect(Slate100, Offset(pagodaX - 9.dp.toPx(), h * 0.64f), Size(18.dp.toPx(), 10.dp.toPx()))

        // Base Tier Structure
        drawCurvedRoof(pagodaX, h * 0.78f, widthDp = 52.dp.toPx(), color = Crimson)
        drawRoundRect(
            color = Slate800,
            topLeft = Offset(pagodaX - 13.dp.toPx(), h * 0.79f),
            size = Size(26.dp.toPx(), 16.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

/**
 * Slide 3: New York (Statue of Liberty + NYC Skyline)
 */
@Composable
private fun NewYorkLibertyIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Background Skyline Rectangles (Slate200 / Slate300)
        drawRect(Slate200, Offset(w * 0.10f, h * 0.58f), Size(w * 0.08f, h * 0.37f))
        drawRect(Slate200, Offset(w * 0.20f, h * 0.42f), Size(w * 0.10f, h * 0.53f))
        drawRect(Slate200, Offset(w * 0.32f, h * 0.52f), Size(w * 0.08f, h * 0.43f))
        drawRect(Slate200, Offset(w * 0.68f, h * 0.46f), Size(w * 0.11f, h * 0.49f))
        drawRect(Slate200, Offset(w * 0.81f, h * 0.60f), Size(w * 0.09f, h * 0.35f))

        // Empire-style Spire Antennas
        drawLine(Slate300, Offset(w * 0.25f, h * 0.30f), Offset(w * 0.25f, h * 0.42f), strokeWidth = 2.dp.toPx())
        drawLine(Slate300, Offset(w * 0.735f, h * 0.34f), Offset(w * 0.735f, h * 0.46f), strokeWidth = 2.dp.toPx())

        // Ground baseline
        drawLine(
            color = Slate200,
            start = Offset(w * 0.08f, h * 0.95f),
            end = Offset(w * 0.92f, h * 0.95f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Statue of Liberty (Center)
        val libX = w * 0.50f

        // Pedestal Stack
        val pedestalPath = Path().apply {
            moveTo(libX - 14.dp.toPx(), h * 0.95f)
            lineTo(libX - 11.dp.toPx(), h * 0.78f)
            lineTo(libX + 11.dp.toPx(), h * 0.78f)
            lineTo(libX + 14.dp.toPx(), h * 0.95f)
            close()
        }
        drawPath(pedestalPath, Color(0xFF94A3B8))
        drawRoundRect(Color(0xFF64748B), Offset(libX - 15.dp.toPx(), h * 0.88f), Size(30.dp.toPx(), 4.dp.toPx()), CornerRadius(1.dp.toPx(), 1.dp.toPx()))
        drawRoundRect(Color(0xFF475569), Offset(libX - 18.dp.toPx(), h * 0.92f), Size(36.dp.toPx(), 5.dp.toPx()), CornerRadius(1.dp.toPx(), 1.dp.toPx()))

        // Robe & Body (Crimson)
        val bodyPath = Path().apply {
            moveTo(libX - 6.dp.toPx(), h * 0.52f)
            lineTo(libX + 6.dp.toPx(), h * 0.52f)
            lineTo(libX + 9.dp.toPx(), h * 0.78f)
            lineTo(libX - 9.dp.toPx(), h * 0.78f)
            close()
        }
        drawPath(bodyPath, Crimson)

        // Tablet Arm (Left)
        drawRoundRect(DarkRed, Offset(libX - 11.dp.toPx(), h * 0.53f), Size(4.dp.toPx(), 9.dp.toPx()), CornerRadius(1.dp.toPx(), 1.dp.toPx()))

        // Head (Circle)
        drawCircle(Crimson, center = Offset(libX, h * 0.44f), radius = 5.dp.toPx())

        // Crown Spikes
        val crownPath = Path().apply {
            moveTo(libX - 6.dp.toPx(), h * 0.42f)
            lineTo(libX - 4.dp.toPx(), h * 0.38f)
            lineTo(libX - 1.dp.toPx(), h * 0.41f)
            lineTo(libX, h * 0.37f)
            lineTo(libX + 1.dp.toPx(), h * 0.41f)
            lineTo(libX + 4.dp.toPx(), h * 0.38f)
            lineTo(libX + 6.dp.toPx(), h * 0.42f)
        }
        drawPath(crownPath, DarkRed, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))

        // Torch Arm (Right Upward)
        drawLine(Crimson, Offset(libX + 6.dp.toPx(), h * 0.52f), Offset(libX + 16.dp.toPx(), h * 0.30f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)

        // Glowing Flame
        drawCircle(
            color = Color(0xFFFEF3C7).copy(alpha = 0.6f),
            center = Offset(libX + 17.dp.toPx(), h * 0.27f),
            radius = 7.dp.toPx()
        )
        drawCircle(
            color = Color(0xFFF59E0B),
            center = Offset(libX + 17.dp.toPx(), h * 0.27f),
            radius = 4.dp.toPx()
        )
    }
}

/**
 * Slide 4: Dubai (Burj Khalifa + Dunes + Burj Al Arab)
 */
@Composable
private fun DubaiBurjKhalifaIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Sand Dunes in Warm Red Light Accents
        val duneA = Path().apply {
            moveTo(0f, h * 0.78f)
            quadraticTo(w * 0.30f, h * 0.66f, w * 0.60f, h * 0.76f)
            quadraticTo(w * 0.85f, h * 0.70f, w, h * 0.80f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(duneA, LightRed.copy(alpha = 0.6f))

        val duneB = Path().apply {
            moveTo(0f, h * 0.85f)
            quadraticTo(w * 0.40f, h * 0.76f, w * 0.75f, h * 0.84f)
            lineTo(w, h * 0.82f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(duneB, SoftBorderRed.copy(alpha = 0.45f))

        // Ground baseline
        drawLine(
            color = Slate200,
            start = Offset(w * 0.08f, h * 0.95f),
            end = Offset(w * 0.92f, h * 0.95f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Burj Al Arab (Left)
        val sailX = w * 0.24f
        val sailPath = Path().apply {
            moveTo(sailX, h * 0.95f)
            lineTo(sailX, h * 0.52f)
            cubicTo(sailX + 8.dp.toPx(), h * 0.58f, sailX + 16.dp.toPx(), h * 0.74f, sailX + 16.dp.toPx(), h * 0.95f)
            close()
        }
        drawPath(sailPath, Slate300)
        drawLine(Slate400, Offset(sailX, h * 0.50f), Offset(sailX, h * 0.95f), strokeWidth = 2.dp.toPx())
        drawLine(DarkRed, Offset(sailX, h * 0.65f), Offset(sailX + 12.dp.toPx(), h * 0.65f), strokeWidth = 1.2.dp.toPx())

        // Burj Khalifa (Center tall stepped tiers)
        val bkX = w * 0.58f

        // Top Spire Needle
        drawLine(DarkRed, Offset(bkX, h * 0.08f), Offset(bkX, h * 0.22f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)

        // Stepped Tiers (Top to Bottom)
        drawTier(bkX, top = h * 0.22f, bottom = h * 0.38f, halfWTop = 2.dp.toPx(), halfWBottom = 3.dp.toPx(), color = Crimson)
        drawTier(bkX, top = h * 0.38f, bottom = h * 0.54f, halfWTop = 4.dp.toPx(), halfWBottom = 6.dp.toPx(), color = BrandRed)
        drawTier(bkX, top = h * 0.54f, bottom = h * 0.72f, halfWTop = 7.dp.toPx(), halfWBottom = 11.dp.toPx(), color = Crimson)
        drawTier(bkX, top = h * 0.72f, bottom = h * 0.86f, halfWTop = 13.dp.toPx(), halfWBottom = 18.dp.toPx(), color = DarkRed)
        drawTier(bkX, top = h * 0.86f, bottom = h * 0.95f, halfWTop = 20.dp.toPx(), halfWBottom = 24.dp.toPx(), color = Crimson)

        // Center Reflective Vertical Highlight Line
        drawLine(Color.White.copy(alpha = 0.75f), Offset(bkX, h * 0.22f), Offset(bkX, h * 0.95f), strokeWidth = 1.5.dp.toPx())
        drawLine(Color.White.copy(alpha = 0.4f), Offset(bkX - 3.dp.toPx(), h * 0.54f), Offset(bkX - 3.dp.toPx(), h * 0.95f), strokeWidth = 0.8.dp.toPx())
        drawLine(Color.White.copy(alpha = 0.4f), Offset(bkX + 3.dp.toPx(), h * 0.54f), Offset(bkX + 3.dp.toPx(), h * 0.95f), strokeWidth = 0.8.dp.toPx())
    }
}

/**
 * Slide 5: Rome (The Colosseum + Mediterranean Sun)
 */
@Composable
private fun RomeColosseumIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Mediterranean Sun Accent
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFDE68A), Color(0xFFFEE2E2).copy(alpha = 0.6f), Color.Transparent),
                center = Offset(w * 0.28f, h * 0.20f),
                radius = 32f
            ),
            center = Offset(w * 0.28f, h * 0.20f),
            radius = 22f
        )

        // Ground baseline
        drawLine(
            color = Slate300,
            start = Offset(w * 0.08f, h * 0.95f),
            end = Offset(w * 0.92f, h * 0.95f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Colosseum Outer Silhouette (Sloping Ruin Shape)
        val colosseumPath = Path().apply {
            moveTo(w * 0.16f, h * 0.95f)
            lineTo(w * 0.16f, h * 0.46f)
            quadraticTo(w * 0.35f, h * 0.40f, w * 0.55f, h * 0.38f)
            quadraticTo(w * 0.75f, h * 0.42f, w * 0.78f, h * 0.56f)
            quadraticTo(w * 0.86f, h * 0.62f, w * 0.88f, h * 0.95f)
            close()
        }
        drawPath(colosseumPath, Crimson)

        // Ruined Wall Stepped Edge (Top Right)
        val ruinedEdge = Path().apply {
            moveTo(w * 0.75f, h * 0.42f)
            lineTo(w * 0.78f, h * 0.46f)
            lineTo(w * 0.78f, h * 0.52f)
            lineTo(w * 0.83f, h * 0.56f)
            lineTo(w * 0.84f, h * 0.62f)
        }
        drawPath(ruinedEdge, Color.White, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))

        // Row 1 (Top Tier Arches)
        val topY = h * 0.48f
        val topW = 4.dp.toPx()
        val topH = 7.dp.toPx()
        for (i in 0..5) {
            val ax = w * (0.22f + i * 0.085f)
            if (ax < w * 0.74f) {
                drawRoundRect(Color.White.copy(alpha = 0.85f), Offset(ax, topY), Size(topW, topH), CornerRadius(2.dp.toPx(), 2.dp.toPx()))
            }
        }

        // Row 2 (Middle Tier Arches)
        val midY = h * 0.62f
        val midW = 5.dp.toPx()
        val midH = 9.dp.toPx()
        for (i in 0..6) {
            val ax = w * (0.20f + i * 0.082f)
            if (ax < w * 0.80f) {
                drawRoundRect(Color.White.copy(alpha = 0.88f), Offset(ax, midY), Size(midW, midH), CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx()))
            }
        }

        // Row 3 (Bottom Tier Arches)
        val botY = h * 0.78f
        val botW = 6.dp.toPx()
        val botH = 12.dp.toPx()
        for (i in 0..7) {
            val ax = w * (0.19f + i * 0.080f)
            if (ax < w * 0.84f) {
                drawRoundRect(Color.White.copy(alpha = 0.92f), Offset(ax, botY), Size(botW, botH), CornerRadius(3.dp.toPx(), 3.dp.toPx()))
            }
        }
    }
}

// Drawing helper functions
private fun DrawScope.drawCloud(center: Offset, scale: Float) {
    val cloudColor = Color.White.copy(alpha = 0.75f)
    drawCircle(cloudColor, radius = 7.dp.toPx() * scale, center = center)
    drawCircle(cloudColor, radius = 10.dp.toPx() * scale, center = Offset(center.x + 6.dp.toPx() * scale, center.y - 2.dp.toPx() * scale))
    drawCircle(cloudColor, radius = 6.dp.toPx() * scale, center = Offset(center.x + 13.dp.toPx() * scale, center.y + 1.dp.toPx() * scale))
    drawRoundRect(
        cloudColor,
        topLeft = Offset(center.x - 4.dp.toPx() * scale, center.y + 2.dp.toPx() * scale),
        size = Size(20.dp.toPx() * scale, 6.dp.toPx() * scale),
        cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
    )
}

private fun DrawScope.drawCurvedRoof(cx: Float, cy: Float, widthDp: Float, color: Color) {
    val half = widthDp / 2f
    val roofPath = Path().apply {
        moveTo(cx - half, cy)
        quadraticTo(cx, cy - 5.dp.toPx(), cx + half, cy)
        lineTo(cx + half - 3.dp.toPx(), cy - 3.dp.toPx())
        quadraticTo(cx, cy - 8.dp.toPx(), cx - half + 3.dp.toPx(), cy - 3.dp.toPx())
        close()
    }
    drawPath(roofPath, color)
}

private fun DrawScope.drawTier(cx: Float, top: Float, bottom: Float, halfWTop: Float, halfWBottom: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx - halfWTop, top)
        lineTo(cx + halfWTop, top)
        lineTo(cx + halfWBottom, bottom)
        lineTo(cx - halfWBottom, bottom)
        close()
    }
    drawPath(path, color)
}
