package com.example.nearbyeventradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.ConnectionExchange
import com.example.nearbyeventradar.ui.components.LanguageSwitcherChip
import com.example.nearbyeventradar.ui.components.StitchBadgeAvatar
import com.example.nearbyeventradar.ui.i18n.AppLanguage
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConnectionsScreen(
    connections: List<ConnectionExchange>,
    liveAttendees: List<Attendee>,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onOpenChatWithConnection: (Attendee) -> Unit,
    onSelectAttendee: (Attendee) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(horizontal = 18.dp)
            .testTag("connections_screen")
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header with Language Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.connectionsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WarmGrayText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🤝", fontSize = 18.sp)
                }
                Text(
                    text = strings.contactsCount(connections.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmGrayTextSecondary
                )
            }

            LanguageSwitcherChip(
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (connections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(VibrantMint.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "No connections yet",
                            tint = VibrantMintDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = strings.emptyConnectionsTitle,
                        color = WarmGrayText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = strings.emptyConnectionsSubtitle,
                        color = WarmGrayTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        lineHeight = 17.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(connections, key = { it.id }) { conn ->
                    val matchedAttendee = liveAttendees.find { it.id == conn.attendeeId }
                    val roleColor = Color(conn.role.colorHex)
                    val isSpecialStitch = conn.attendeeName.contains("lilo", ignoreCase = true) || conn.attendeeName.contains("stitch", ignoreCase = true)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SoftBorder, RoundedCornerShape(20.dp))
                            .clickable {
                                if (matchedAttendee != null) {
                                    onSelectAttendee(matchedAttendee)
                                }
                            }
                            .testTag("connection_card_${conn.id}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val initials = conn.attendeeName.split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString("")

                                    StitchBadgeAvatar(
                                        initials = initials,
                                        badgeColor = roleColor,
                                        size = 48.dp,
                                        isSpecialStitch = isSpecialStitch
                                    )

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text = conn.attendeeName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = WarmGrayText
                                        )
                                        Text(
                                            text = "${conn.title} • ${conn.company}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = WarmGrayTextSecondary
                                        )
                                    }
                                }

                                if (matchedAttendee != null) {
                                    IconButton(
                                        onClick = { onOpenChatWithConnection(matchedAttendee) },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceWhite)
                                            .border(1.dp, SoftBorder, CircleShape)
                                            .testTag("conn_chat_${conn.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Chat,
                                            contentDescription = "Chat",
                                            tint = WarmGrayText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Contact info & Location note
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = WarmGrayMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = conn.contactEmail,
                                        color = SoftCoralDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(conn.exchangeTimestamp))
                                Text(
                                    text = dateStr,
                                    color = WarmGrayMuted,
                                    fontSize = 11.sp
                                )
                            }

                            if (conn.socialHandle.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🔗 ${conn.socialHandle}",
                                    color = WarmGrayTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

