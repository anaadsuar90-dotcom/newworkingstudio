package com.example.nearbyeventradar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CardBorderMint
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.OutlineWarm
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SoftCoralContainer
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainer
import com.example.nearbyeventradar.ui.theme.SurfaceContainerHigh
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceCream
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintContainer
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AttendeeDetailSheet(
    attendee: Attendee,
    onDismiss: () -> Unit,
    onWave: (Attendee) -> Unit,
    onExchangeBadge: (Attendee) -> Unit,
    onToggleSave: (Attendee) -> Unit,
    onOpenChat: (Attendee) -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val roleColor = Color(attendee.role.colorHex)
    val isSpecialStitch = attendee.id.contains("stitch", ignoreCase = true) || attendee.id.contains("lilo", ignoreCase = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CreamBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .verticalScroll(rememberScrollState())
                .testTag("attendee_detail_sheet")
        ) {
            // Top Drag indicator / close bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = roleColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = strings.getRoleName(attendee.role).uppercase(),
                        color = WarmGrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleSave(attendee) },
                        modifier = Modifier.testTag("detail_sheet_save")
                    ) {
                        Icon(
                            imageVector = if (attendee.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = strings.saveContact,
                            tint = if (attendee.isSaved) VibrantMintDark else WarmGrayMuted
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("detail_sheet_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = WarmGrayTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Profile Header with Kindred Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val initials = attendee.name.split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")

                StitchBadgeAvatar(
                    initials = initials,
                    badgeColor = roleColor,
                    size = 72.dp,
                    isSpecialStitch = isSpecialStitch
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = attendee.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WarmGrayText
                    )
                    Text(
                        text = attendee.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = WarmGrayTextSecondary
                    )
                    Text(
                        text = attendee.company,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SunnyYellowDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Proximity & Signal Details (Clean Kindred Cards)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite, RoundedCornerShape(20.dp))
                    .border(1.5.dp, CardBorderMint, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "DISTANCE", color = WarmGrayMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = attendee.proximityText(),
                        color = SoftCoralDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .width(1.dp)
                        .background(SoftBorder)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "SIGNAL", color = WarmGrayMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${attendee.rssi} dBm",
                        color = WarmGrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .height(26.dp)
                        .width(1.dp)
                        .background(SoftBorder)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "MATCH SCORE", color = WarmGrayMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${attendee.matchScore}%",
                        color = VibrantMintDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bio
            Text(
                text = strings.sectionAbout,
                color = WarmGrayMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = attendee.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = WarmGrayText,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Interests / Tags
            Text(
                text = strings.sectionInterests,
                color = WarmGrayMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attendee.interests.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                    ) {
                        Text(
                            text = "#$tag",
                            color = WarmGrayText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Looking For / Offering Cards
            if (attendee.lookingFor.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🎯 ${strings.sectionLookingFor}",
                            color = SoftCoralDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = attendee.lookingFor,
                            color = WarmGrayText,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (attendee.offering.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 ${strings.sectionOffering}",
                            color = VibrantMintDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = attendee.offering,
                            color = WarmGrayText,
                            fontSize = 13.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Icebreaker Conversation Starters
            val icebreakers = strings.getIcebreakers(attendee.name, attendee.role)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCream),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SunnyYellow.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Icebreakers",
                            tint = SunnyYellowDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.sectionIcebreakers,
                            color = SunnyYellowDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    icebreakers.forEach { prompt ->
                        Text(
                            text = prompt,
                            color = WarmGrayText,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Primary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Exchange Contact Badge (Sunny Yellow)
                Button(
                    onClick = { onExchangeBadge(attendee) },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .border(1.5.dp, SunnyYellowDark.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .testTag("detail_sheet_exchange"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (attendee.isConnected) VibrantMint else SunnyYellow,
                        contentColor = if (attendee.isConnected) VibrantMintDark else OnSunnyYellow
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = if (attendee.isConnected) Icons.Default.CheckCircle else Icons.Default.ContactPage,
                        contentDescription = "Exchange",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (attendee.isConnected) strings.connectedStatus else strings.exchangeBadge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Send Wave (Soft Coral)
                Button(
                    onClick = { onWave(attendee) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_sheet_wave"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (attendee.waveSent) SurfaceContainer else SoftCoral,
                        contentColor = if (attendee.waveSent) WarmGrayTextSecondary else Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WavingHand,
                        contentDescription = "Wave",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (attendee.waveSent) strings.waveSent else strings.sayHi,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Chat button
                IconButton(
                    onClick = { onOpenChat(attendee) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, SoftBorder, CircleShape)
                        .testTag("detail_sheet_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = strings.chat,
                        tint = WarmGrayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
