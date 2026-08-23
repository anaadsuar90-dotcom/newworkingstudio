package com.example.nearbyeventradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.nearbyeventradar.ui.components.LanguageSwitcherChip
import com.example.nearbyeventradar.ui.components.StitchBadgeAvatar
import com.example.nearbyeventradar.ui.i18n.AppLanguage
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CardBorderMint
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainer
import androidx.compose.material3.OutlinedButton
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceCream
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendeesListScreen(
    attendees: List<Attendee>,
    filter: RadarFilter,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onTagSelect: (String) -> Unit,
    onSelectAttendee: (Attendee) -> Unit,
    onWave: (Attendee) -> Unit,
    onToggleSave: (Attendee) -> Unit,
    onOpenChat: (Attendee) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val commonTags = listOf("Kotlin", "On-Device AI", "Compose", "Kindred UX", "BLE Mesh", "Design Tokens", "Space Tech", "P2P Mesh")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(horizontal = 18.dp)
            .testTag("attendees_list_screen")
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Title Header with Language Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.attendeesTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WarmGrayText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✨", fontSize = 18.sp)
                }
                Text(
                    text = strings.showingAttendeesCount(attendees.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmGrayTextSecondary
                )
            }

            LanguageSwitcherChip(
                currentLanguage = currentLanguage,
                onToggleLanguage = onToggleLanguage
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar (Warm Kindred styling)
        OutlinedTextField(
            value = filter.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(strings.searchPlaceholder, fontSize = 13.sp, color = WarmGrayMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = SunnyYellowDark,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (filter.searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = WarmGrayMuted
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("attendee_search_input"),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedBorderColor = SunnyYellowDark,
                unfocusedBorderColor = SoftBorder,
                focusedTextColor = WarmGrayText,
                unfocusedTextColor = WarmGrayText
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Tag Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            commonTags.forEach { tag ->
                val isSelected = filter.selectedTag.equals(tag, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagSelect(tag) },
                    label = { Text("#$tag", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    shape = RoundedCornerShape(16.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = SoftBorder,
                        selectedBorderColor = SunnyYellowDark,
                        enabled = true,
                        selected = isSelected
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SunnyYellow,
                        selectedLabelColor = OnSunnyYellow,
                        containerColor = SurfaceWhite,
                        labelColor = WarmGrayTextSecondary
                    ),
                    modifier = Modifier.testTag("chip_tag_$tag")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Attendees List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (attendees.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = strings.emptyAttendeesTitle,
                                color = WarmGrayTextSecondary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = strings.emptyAttendeesSubtitle,
                                color = WarmGrayMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            items(attendees, key = { it.id }) { attendee ->
                val roleColor = Color(attendee.role.colorHex)
                val isSpecialStitch = attendee.id.contains("stitch", ignoreCase = true) || attendee.id.contains("lilo", ignoreCase = true)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelectAttendee(attendee) }
                        .border(1.dp, SoftBorder, RoundedCornerShape(20.dp))
                        .testTag("attendee_card_${attendee.id}"),
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
                                val initials = attendee.name.split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString("")

                                StitchBadgeAvatar(
                                    initials = initials,
                                    badgeColor = roleColor,
                                    size = 50.dp,
                                    isSpecialStitch = isSpecialStitch
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = attendee.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = WarmGrayText,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (attendee.matchScore >= 90) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = VibrantMint.copy(alpha = 0.25f)
                                            ) {
                                                Text(
                                                    text = "${attendee.matchScore}% ✨",
                                                    color = VibrantMintDark,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

                            // Distance Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SoftCoral.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = "Distance",
                                        tint = SoftCoralDark,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = attendee.proximityText(),
                                        color = SoftCoralDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bio excerpt
                        Text(
                            text = attendee.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmGrayText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tags
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            attendee.interests.take(4).forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SurfaceContainerLow,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        color = WarmGrayTextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onWave(attendee) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("list_wave_${attendee.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (attendee.waveSent) SurfaceContainer else SoftCoral,
                                    contentColor = if (attendee.waveSent) WarmGrayTextSecondary else Color.White
                                ),
                                shape = RoundedCornerShape(21.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WavingHand,
                                    contentDescription = "Wave",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (attendee.waveSent) strings.waveSent else strings.sayHi,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { onOpenChat(attendee) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite)
                                    .border(1.dp, SoftBorder, CircleShape)
                                    .testTag("list_chat_${attendee.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Chat",
                                    tint = WarmGrayText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { onToggleSave(attendee) },
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite)
                                    .border(1.dp, SoftBorder, CircleShape)
                                    .testTag("list_save_${attendee.id}")
                            ) {
                                Icon(
                                    imageVector = if (attendee.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save Contact",
                                    tint = if (attendee.isSaved) VibrantMintDark else WarmGrayMuted,
                                    modifier = Modifier.size(18.dp)
                                )
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
