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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CardBorderMint
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@Composable
fun IncomingWaveDialog(
    attendee: Attendee,
    onWaveBack: (Attendee) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val roleColor = Color(attendee.role.colorHex)
    val isSpecialStitch = attendee.id.contains("stitch", ignoreCase = true) || attendee.id.contains("lilo", ignoreCase = true)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("incoming_wave_dialog")
                .border(2.dp, CardBorderMint, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftCoral.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = strings.incomingWaveHeader,
                            color = SoftCoralDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("wave_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = WarmGrayMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.wavedAtYouMessage(attendee.name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WarmGrayText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${attendee.title} • ${attendee.company}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmGrayTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "Distance",
                            tint = SoftCoralDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.waveDistanceAndMatch(attendee.distanceMeters, attendee.matchScore),
                            color = WarmGrayText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(44.dp).testTag("wave_dialog_dismiss"),
                        shape = RoundedCornerShape(22.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                    ) {
                        Text(strings.laterButton, color = WarmGrayTextSecondary, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onWaveBack(attendee) },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(44.dp)
                            .border(1.5.dp, SunnyYellowDark.copy(alpha = 0.4f), RoundedCornerShape(22.dp))
                            .testTag("wave_dialog_wave_back"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SunnyYellow,
                            contentColor = OnSunnyYellow
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WavingHand,
                            contentDescription = "Wave Back",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.waveBackButton, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

