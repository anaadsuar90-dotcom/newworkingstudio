package com.example.nearbyeventradar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.UserProfile
import com.example.nearbyeventradar.data.model.VisibilityStatus
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
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyBadgeScreen(
    profile: UserProfile,
    isBroadcasting: Boolean,
    currentLanguage: AppLanguage,
    onToggleLanguage: () -> Unit,
    onToggleBroadcasting: () -> Unit,
    onUpdateVisibility: (VisibilityStatus) -> Unit,
    onSaveProfile: (UserProfile) -> Unit,
    onShowQrPass: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember(profile) { mutableStateOf(profile.name) }
    var editTitle by remember(profile) { mutableStateOf(profile.title) }
    var editCompany by remember(profile) { mutableStateOf(profile.company) }
    var editBio by remember(profile) { mutableStateOf(profile.bio) }
    var editLookingFor by remember(profile) { mutableStateOf(profile.lookingFor) }
    var editOffering by remember(profile) { mutableStateOf(profile.offering) }

    val roleColor = Color(profile.role.colorHex)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(horizontal = 18.dp)
            .verticalScroll(rememberScrollState())
            .testTag("my_badge_screen")
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header with Language Switcher and Edit Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.badgeTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WarmGrayText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✨", fontSize = 18.sp)
                }
                Text(
                    text = strings.badgeSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmGrayTextSecondary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LanguageSwitcherChip(
                    currentLanguage = currentLanguage,
                    onToggleLanguage = onToggleLanguage
                )

                IconButton(
                    onClick = {
                        if (isEditing) {
                            onSaveProfile(
                                profile.copy(
                                    name = editName,
                                    title = editTitle,
                                    company = editCompany,
                                    bio = editBio,
                                    lookingFor = editLookingFor,
                                    offering = editOffering
                                )
                            )
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, SoftBorder, CircleShape)
                        .testTag("btn_edit_badge")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = if (isEditing) strings.saveProfile else strings.editProfile,
                        tint = if (isEditing) VibrantMintDark else WarmGrayText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Digital Badge Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CardBorderMint, RoundedCornerShape(24.dp))
                .testTag("my_badge_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = roleColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = strings.getRoleName(profile.role).uppercase(),
                            color = roleColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isBroadcasting) VibrantMint.copy(alpha = 0.25f) else WarmGrayMuted.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isBroadcasting) VibrantMintDark else WarmGrayMuted)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isBroadcasting) strings.bleActiveStatus else strings.bleStealthStatus,
                                color = if (isBroadcasting) VibrantMintDark else WarmGrayMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(strings.fullNameLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WarmGrayText,
                            unfocusedTextColor = WarmGrayText,
                            focusedBorderColor = SunnyYellowDark,
                            unfocusedBorderColor = SoftBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text(strings.titleRoleLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WarmGrayText,
                            unfocusedTextColor = WarmGrayText,
                            focusedBorderColor = SunnyYellowDark,
                            unfocusedBorderColor = SoftBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editCompany,
                        onValueChange = { editCompany = it },
                        label = { Text(strings.companyLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WarmGrayText,
                            unfocusedTextColor = WarmGrayText,
                            focusedBorderColor = SunnyYellowDark,
                            unfocusedBorderColor = SoftBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text(strings.bioLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = WarmGrayText,
                            unfocusedTextColor = WarmGrayText,
                            focusedBorderColor = SunnyYellowDark,
                            unfocusedBorderColor = SoftBorder
                        )
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val initials = profile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                        StitchBadgeAvatar(
                            initials = initials,
                            badgeColor = roleColor,
                            size = 64.dp,
                            isSpecialStitch = true
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = WarmGrayText
                            )
                            Text(
                                text = "${profile.title} • ${profile.company}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmGrayTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = profile.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmGrayText,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interests Flow
                Text(
                    text = strings.topicsSkillsHeader,
                    color = WarmGrayMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    profile.interests.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceContainerLow,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                        ) {
                            Text(
                                text = "#$tag",
                                color = WarmGrayTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // QR Pass button
                Button(
                    onClick = onShowQrPass,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_show_my_qr"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SunnyYellow,
                        contentColor = OnSunnyYellow
                    ),
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SunnyYellowDark.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "QR Pass",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.showQrPassButton, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Visibility Modes
        Text(
            text = strings.visibilityHeader,
            color = WarmGrayMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        VisibilityStatus.values().forEach { status ->
            val isSelected = profile.visibilityStatus == status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onUpdateVisibility(status) }
                    .border(
                        1.5.dp,
                        if (isSelected) SunnyYellowDark else SoftBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("vis_status_${status.name.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) SunnyYellow.copy(alpha = 0.15f) else SurfaceWhite
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.getVisibilityLabel(status),
                            color = WarmGrayText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = strings.getVisibilityDescription(status),
                            color = WarmGrayTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = SunnyYellowDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

