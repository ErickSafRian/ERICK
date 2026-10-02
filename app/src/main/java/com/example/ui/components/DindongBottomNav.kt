package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DindongColorScheme

enum class DindongTab(val title: String, val icon: String, val tag: String) {
    ARCADE("Mesin", "🎰", "tab_arcade"),
    WALLET("Top Up", "💳", "tab_wallet"),
    MISSIONS("Misi", "🏆", "tab_missions"),
    LOUNGE("Lounge", "✨", "tab_lounge")
}

@Composable
fun DindongBottomNav(
    selectedTab: DindongTab,
    themeColors: DindongColorScheme,
    onTabSelected: (DindongTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = themeColors.hudBackground,
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            themeColors.surfaceVariant,
                            themeColors.primary.copy(alpha = 0.5f),
                            themeColors.surfaceVariant
                        )
                    ),
                    shape = androidx.compose.ui.graphics.RectangleShape
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DindongTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val activeBgColor by animateColorAsState(
                        targetValue = if (isSelected) themeColors.primary.copy(alpha = 0.2f) else Color.Transparent,
                        animationSpec = tween(200),
                        label = "tabBg"
                    )

                    Column(
                        modifier = Modifier
                            .testTag(tab.tag)
                            .clip(RoundedCornerShape(14.dp))
                            .background(activeBgColor)
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = tab.icon,
                            fontSize = if (isSelected) 18.sp else 16.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) themeColors.primary else themeColors.textMuted
                        )
                    }
                }
            }
        }
    }
}
