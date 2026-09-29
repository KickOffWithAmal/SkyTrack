package com.skypass.skytrack.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SyncLoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A).copy(alpha = 0.4f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // To prevent clicks from passing through
            Surface(
                modifier = Modifier.scale(0.9f),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                LoaderCarousel()
            }
        }
    }
}

@Composable
private fun LoaderCarousel() {
    var currentIndex by remember { mutableStateOf(0) }
    
    val slides = listOf(
        LandmarkSlide("Paris, France", "CDG", "Eiffel Tower"),
        LandmarkSlide("Tokyo, Japan", "HND", "Mt. Fuji & Pagoda"),
        LandmarkSlide("New York, USA", "JFK", "Statue of Liberty"),
        LandmarkSlide("Dubai, UAE", "DXB", "Burj Khalifa"),
        LandmarkSlide("Rome, Italy", "FCO", "The Colosseum")
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(2800)
            currentIndex = (currentIndex + 1) % slides.size
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(240.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp) // 44 * 4dp
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            slides.forEachIndexed { index, slide ->
                val isActive = index == currentIndex
                val alpha by animateFloatAsState(
                    targetValue = if (isActive) 1f else 0f,
                    animationSpec = tween(550)
                )
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1f else 0.92f,
                    animationSpec = tween(550)
                )
                
                if (alpha > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .scale(scale)
                            .padding(14.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFFC62828), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = slide.city,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                                Text(
                                    text = slide.code,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Title
                            Text(
                                text = slide.landmark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            slides.indices.forEach { index ->
                val isActive = index == currentIndex
                val width by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 6.dp,
                    animationSpec = tween(300)
                )
                val color by animateColorAsState(
                    targetValue = if (isActive) Color(0xFFC62828) else Color(0xFFE2E8F0),
                    animationSpec = tween(300)
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(width)
                        .background(color, CircleShape)
                )
            }
        }
    }
}

private data class LandmarkSlide(
    val city: String,
    val code: String,
    val landmark: String
)
