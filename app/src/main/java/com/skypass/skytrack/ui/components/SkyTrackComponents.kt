package com.skypass.skytrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.skypass.skytrack.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skypass.skytrack.ui.theme.SkyRed
import com.skypass.skytrack.ui.theme.SkyRedPale
import com.skypass.skytrack.ui.theme.SkyTextSecondary

enum class MainTab { HOME, LEAVE, STATISTICS }

@Composable
fun SkyTrackLogo(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 46.dp
) {
    Image(
        painter = painterResource(R.drawable.skytrack_logo),
        contentDescription = "SkyTrack logo",
        modifier = modifier.size(size)
    )
}

@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    onProfile: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkyTrackLogo(size = 38.dp)
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "SKYTRACK",
                style = MaterialTheme.typography.labelMedium,
                color = SkyTextSecondary
            )
            Text(
                title,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Icon(
            Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = SkyTextSecondary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.size(14.dp))
        Surface(
            onClick = { onProfile?.invoke() },
            enabled = onProfile != null,
            shape = CircleShape,
            color = SkyRed
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier
                    .padding(9.dp)
                    .size(22.dp)
            )
        }
    }
}

@Composable
fun BottomBar(
    selected: MainTab,
    onSelected: (MainTab) -> Unit
) {
    val items = listOf(
        Triple(MainTab.HOME, Icons.Default.Home, "Home"),
        Triple(MainTab.LEAVE, Icons.Default.CalendarMonth, "Leave"),
        Triple(MainTab.STATISTICS, Icons.Default.BarChart, "Statistics")
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (tab, icon, label) ->
                val active = tab == selected
                val scale by animateFloatAsState(
                    targetValue = if (active) 1.14f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "tabScale"
                )
                val iconColor by animateColorAsState(
                    if (active) SkyRed else SkyTextSecondary,
                    label = "tabColor"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale)
                        .clickable { onSelected(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (active) 44.dp else 36.dp)
                            .background(
                                if (active) SkyRedPale else Color.Transparent,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = label,
                            tint = iconColor,
                            modifier = Modifier.size(if (active) 26.dp else 23.dp)
                        )
                    }
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        color = iconColor
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun WhiteCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) { content() }
}
