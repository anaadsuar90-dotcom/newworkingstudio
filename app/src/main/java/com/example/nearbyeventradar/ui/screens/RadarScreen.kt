package com.example.nearbyeventradar.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.RadarFilter
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.ui.components.AttendeeQuickCard
import com.example.nearbyeventradar.ui.components.LanguageSwitcherChip
import com.example.nearbyeventradar.ui.components.RadarSonarCanvas
import com.example.nearbyeventradar.ui.components.StitchBadgeAvatar
import com.example.nearbyeventradar.ui.components.StitchFaceIcon
import com.example.nearbyeventradar.ui.i18n.AppLanguage
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CardBorderMint
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.OutlineWarm
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainer
import com.example.nearbyeventradar.ui.theme.SurfaceContainerHigh
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceCream
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@Composable
fun RadarScreen(
    attendees: List<Attendee>,
    selectedAttendee: Attendee?,
    isScanning: Boolean,
    isBroadcasting: Boolean,
    radarFilter: RadarFilter,
    zoomMeters: Float,
    isSonarView: Boolean,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onToggleScanning: () -> Unit,
    onToggleBroadcasting: () -> Unit,
    onSelectAttendee: (Attendee) -> Unit,
    onDismissQuickCard: () -> Unit,
    onViewProfile: (Attendee) -> Unit,
    onWave: (Attendee) -> Unit,
    onToggleSave: (Attendee) -> Unit,
    onOpenChat: (Attendee) -> Unit,
    onSetZoom: (Float) -> Unit,
    onSetRoleFilter: (RoleCategory?) -> Unit,
    onSetSonarView: (Boolean) -> Unit,
    onShowQrPass: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBg)
            .testTag("radar_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header with Kindred Avatar, Active Status & Language Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(2.dp, SunnyYellow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        StitchFaceIcon(size = 36.dp, hasHibiscus = true)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.appTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmGrayText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftCoral.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "NEARBY BLE",
                                    color = SoftCoralDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isScanning) VibrantMint else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isScanning) strings.statusScanning else strings.statusPaused,
                                color = if (isScanning) VibrantMintDark else WarmGrayMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Switcher Chip
                    LanguageSwitcherChip(
                        currentLanguage = currentLanguage,
                        onToggleLanguage = onToggleLanguage
                    )

                    // Toggle Scanner Scan/Pause
                    IconButton(
                        onClick = onToggleScanning,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, SoftBorder, CircleShape)
                            .testTag("btn_toggle_scan")
                    ) {
                        Icon(
                            imageVector = if (isScanning) Icons.Default.Sensors else Icons.Default.SensorsOff,
                            contentDescription = "Toggle Scan",
                            tint = if (isScanning) VibrantMintDark else WarmGrayMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // QR Pass Button
                    IconButton(
                        onClick = onShowQrPass,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SunnyYellow.copy(alpha = 0.25f))
                            .border(1.5.dp, SunnyYellow, CircleShape)
                            .testTag("btn_show_qr_pass")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "QR Pass",
                            tint = SunnyYellowDark,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            // Role Filter Chips Row (Kindred Spirits rounded pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = radarFilter.selectedRole == null,
                    onClick = { onSetRoleFilter(null) },
                    label = { Text("${strings.filterAll} (${attendees.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(20.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = SoftBorder,
                        selectedBorderColor = SunnyYellowDark,
                        enabled = true,
                        selected = radarFilter.selectedRole == null
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SunnyYellow,
                        selectedLabelColor = OnSunnyYellow,
                        containerColor = SurfaceWhite,
                        labelColor = WarmGrayText
                    ),
                    modifier = Modifier.testTag("chip_role_all")
                )

                RoleCategory.values().forEach { role ->
                    val isSelected = radarFilter.selectedRole == role
                    val roleColor = Color(role.colorHex)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetRoleFilter(if (isSelected) null else role) },
                        label = { Text(strings.getRoleName(role), fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(20.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = SoftBorder,
                            selectedBorderColor = roleColor,
                            enabled = true,
                            selected = isSelected
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = roleColor.copy(alpha = 0.25f),
                            selectedLabelColor = WarmGrayText,
                            containerColor = SurfaceWhite,
                            labelColor = WarmGrayTextSecondary
                        ),
                        modifier = Modifier.testTag("chip_role_${role.name.lowercase()}")
                    )
                }
            }

            // Zoom Range & View Mode selector bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zoom Pills: 5m, 15m, 30m
                Row(
                    modifier = Modifier
                        .background(SurfaceWhite, RoundedCornerShape(16.dp))
                        .border(1.dp, SoftBorder, RoundedCornerShape(16.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf(5f to "5m", 15f to "15m", 30f to "30m").forEach { (dist, label) ->
                        val isSel = (zoomMeters == dist)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) SunnyYellow else Color.Transparent,
                            modifier = Modifier
                                .clickable { onSetZoom(dist) }
                                .testTag("zoom_btn_$label")
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) OnSunnyYellow else WarmGrayTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Radar vs List view toggle
                Row(
                    modifier = Modifier
                        .background(SurfaceWhite, RoundedCornerShape(16.dp))
                        .border(1.dp, SoftBorder, RoundedCornerShape(16.dp))
                        .padding(3.dp)
                ) {
                    IconButton(
                        onClick = { onSetSonarView(true) },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSonarView) SunnyYellow else Color.Transparent)
                            .testTag("view_sonar_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar View",
                            tint = if (isSonarView) OnSunnyYellow else WarmGrayMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { onSetSonarView(false) },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isSonarView) SunnyYellow else Color.Transparent)
                            .testTag("view_list_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewList,
                            contentDescription = "List View",
                            tint = if (!isSonarView) OnSunnyYellow else WarmGrayMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Main Display Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                if (isSonarView) {
                    RadarSonarCanvas(
                        attendees = attendees,
                        selectedAttendee = selectedAttendee,
                        maxDistanceMeters = zoomMeters,
                        isScanning = isScanning,
                        onSelectAttendee = onSelectAttendee,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Condensed proximity list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(attendees.sortedBy { it.distanceMeters }, key = { it.id }) { attendee ->
                            val roleColor = Color(attendee.role.colorHex)
                            val isSpecialStitch = attendee.id.contains("stitch", ignoreCase = true) || attendee.id.contains("lilo", ignoreCase = true)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectAttendee(attendee) }
                                    .testTag("radar_list_item_${attendee.id}")
                                    .border(1.dp, SoftBorder, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        val initials = attendee.name.split(" ")
                                            .mapNotNull { it.firstOrNull()?.toString() }
                                            .take(2)
                                            .joinToString("")

                                        StitchBadgeAvatar(
                                            initials = initials,
                                            badgeColor = roleColor,
                                            size = 46.dp,
                                            isSpecialStitch = isSpecialStitch
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = attendee.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = WarmGrayText,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${attendee.title} • ${attendee.company}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = WarmGrayTextSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = SoftCoral.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = attendee.proximityText(),
                                                color = SoftCoralDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${attendee.rssi} dBm",
                                            color = WarmGrayMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Slide-up Quick Card at bottom when an attendee is tapped
        AnimatedVisibility(
            visible = selectedAttendee != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            selectedAttendee?.let { attendee ->
                AttendeeQuickCard(
                    attendee = attendee,
                    onDismiss = onDismissQuickCard,
                    onViewProfile = onViewProfile,
                    onWave = onWave,
                    onToggleSave = onToggleSave,
                    onOpenChat = onOpenChat
                )
            }
        }
    }
}
