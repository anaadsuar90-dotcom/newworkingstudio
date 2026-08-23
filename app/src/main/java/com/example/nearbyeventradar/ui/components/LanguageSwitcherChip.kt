package com.example.nearbyeventradar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearbyeventradar.ui.i18n.AppLanguage
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@Composable
fun LanguageSwitcherChip(
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceWhite,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftBorder),
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggleLanguage() }
            .testTag("btn_language_switcher")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Spanish option
            val isEs = currentLanguage == AppLanguage.SPANISH
            val esBgColor by animateColorAsState(
                targetValue = if (isEs) SunnyYellow else Color.Transparent,
                label = "es_bg"
            )
            val esTextColor = if (isEs) OnSunnyYellow else WarmGrayMuted

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(esBgColor)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🇪🇸", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "ES",
                        fontSize = 11.sp,
                        fontWeight = if (isEs) FontWeight.Bold else FontWeight.Medium,
                        color = esTextColor
                    )
                }
            }

            Text(text = "|", color = SoftBorder, fontSize = 11.sp)

            // English option
            val isEn = currentLanguage == AppLanguage.ENGLISH
            val enBgColor by animateColorAsState(
                targetValue = if (isEn) SunnyYellow else Color.Transparent,
                label = "en_bg"
            )
            val enTextColor = if (isEn) OnSunnyYellow else WarmGrayMuted

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(enBgColor)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🇺🇸", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "EN",
                        fontSize = 11.sp,
                        fontWeight = if (isEn) FontWeight.Bold else FontWeight.Medium,
                        color = enTextColor
                    )
                }
            }
        }
    }
}
