package com.example.nearbyeventradar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CardBorderMint
import com.example.nearbyeventradar.ui.theme.OnSoftCoral
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.OutlineWarm
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainer
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@Composable
fun AttendeeQuickCard(
    attendee: Attendee,
    onDismiss: () -> Unit,
    onViewProfile: (Attendee) -> Unit,
    onWave: (Attendee) -> Unit,
    onToggleSave: (Attendee) -> Unit,
    onOpenChat: (Attendee) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val roleColor = Color(attendee.role.colorHex)
    val isStitchOrLilo = attendee.id.contains("stitch", ignoreCase = true) || attendee.id.contains("lilo", ignoreCase = true)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("attendee_quick_card_${attendee.id}")
            .border(2.dp, CardBorderMint, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        size = 52.dp,
                        isSpecialStitch = isStitchOrLilo
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = attendee.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = WarmGrayText,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (attendee.matchScore >= 85) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SoftCoral.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${attendee.matchScore}% Match",
                                        color = SoftCoralDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "${attendee.title} • ${attendee.company}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmGrayTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("quick_card_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = WarmGrayMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Proximity & Signal indicators (Kindred Spirits friendly soft pills)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SoftCoral.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftCoral.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "Distance",
                            tint = SoftCoralDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = attendee.proximityText(),
                            color = SoftCoralDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = roleColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = strings.getRoleName(attendee.role),
                        color = WarmGrayText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLow
                ) {
                    Text(
                        text = "${attendee.rssi} dBm",
                        color = WarmGrayMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons row (Pill shaped, Sunny Yellow Primary & Soft Coral Secondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary action: Say Hi / Wave (Soft Coral)
                Button(
                    onClick = { onWave(attendee) },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(44.dp)
                        .testTag("btn_wave_${attendee.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (attendee.waveSent) SurfaceContainer else SoftCoral,
                        contentColor = if (attendee.waveSent) WarmGrayTextSecondary else Color.White
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WavingHand,
                        contentDescription = "Say Hi",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (attendee.waveSent) strings.waveSent else strings.sayHi,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Primary action: View Profile (Sunny Yellow with dark border)
                Button(
                    onClick = { onViewProfile(attendee) },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(44.dp)
                        .border(1.5.dp, SunnyYellowDark.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                        .testTag("btn_view_profile_${attendee.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SunnyYellow,
                        contentColor = OnSunnyYellow
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text(
                        text = strings.viewProfile,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Chat icon
                IconButton(
                    onClick = { onOpenChat(attendee) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, SoftBorder, CircleShape)
                        .testTag("btn_quick_chat_${attendee.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat",
                        tint = WarmGrayText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Bookmark icon
                IconButton(
                    onClick = { onToggleSave(attendee) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLow)
                        .border(1.dp, SoftBorder, CircleShape)
                        .testTag("btn_quick_save_${attendee.id}")
                ) {
                    Icon(
                        imageVector = if (attendee.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Contact",
                        tint = if (attendee.isSaved) VibrantMintDark else WarmGrayMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
