package com.example.nearbyeventradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
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
import com.example.nearbyeventradar.data.model.EventSession
import com.example.nearbyeventradar.ui.components.LanguageSwitcherChip
import com.example.nearbyeventradar.ui.i18n.AppLanguage
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
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

@Composable
fun SessionsScreen(
    sessions: List<EventSession>,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onToggleBookmark: (EventSession) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(horizontal = 18.dp)
            .testTag("sessions_screen")
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
                        text = strings.sessionsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WarmGrayText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✨", fontSize = 18.sp)
                }
                Text(
                    text = strings.sessionsSubtitle,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions, key = { it.id }) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, SoftBorder, RoundedCornerShape(20.dp))
                        .testTag("session_card_${session.id}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SunnyYellow.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = session.category.uppercase(),
                                    color = SunnyYellowDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = { onToggleBookmark(session) },
                                modifier = Modifier.size(32.dp).testTag("session_bookmark_${session.id}")
                            ) {
                                Icon(
                                    imageVector = if (session.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (session.isBookmarked) VibrantMintDark else WarmGrayMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmGrayText,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${session.speakerName} • ${session.speakerRole}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmGrayTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = session.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmGrayTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Info pills (Time, Location, Live Crowd Density)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerLow,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Time",
                                        tint = WarmGrayTextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${session.startTime} - ${session.endTime}",
                                        color = WarmGrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceContainerLow,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Room",
                                        tint = VibrantMintDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = session.locationRoom,
                                        color = WarmGrayText,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SoftCoral.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = "Attendees nearby",
                                        tint = SoftCoralDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = strings.nearbyCount(session.nearbyAttendeesCount),
                                        color = SoftCoralDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
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

