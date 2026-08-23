package com.example.nearbyeventradar.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nearbyeventradar.data.model.UserProfile
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CardBorderMint
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SoftCoralDark
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SunnyYellowDark
import com.example.nearbyeventradar.ui.theme.SurfaceContainerLow
import com.example.nearbyeventradar.ui.theme.SurfaceCream
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.VibrantMintDark
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import com.example.nearbyeventradar.ui.theme.WarmGrayTextSecondary

@Composable
fun DigitalBadgeQrDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val roleColor = Color(userProfile.role.colorHex)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("digital_badge_dialog")
                .border(2.dp, CardBorderMint, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "BLE",
                            tint = VibrantMintDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.qrPassHeader,
                            color = VibrantMintDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("badge_dialog_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = WarmGrayMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar and Name
                val initials = userProfile.name.split(" ")
                    .mapNotNull { it.firstOrNull()?.toString() }
                    .take(2)
                    .joinToString("")

                StitchBadgeAvatar(
                    initials = initials,
                    badgeColor = roleColor,
                    size = 68.dp,
                    isSpecialStitch = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = userProfile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = WarmGrayText
                )

                Text(
                    text = "${userProfile.title} • ${userProfile.company}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmGrayTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kindred Stylized QR Matrix Pattern
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(SurfaceCream, RoundedCornerShape(20.dp))
                        .border(1.5.dp, SoftBorder, RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        val gridSize = 15
                        val cellSize = size.width / gridSize
                        val hashSeed = (userProfile.name + userProfile.beaconId).hashCode()

                        // Draw QR finder corners
                        fun drawFinder(x: Int, y: Int) {
                            drawRect(
                                color = WarmGrayText,
                                topLeft = Offset(x * cellSize, y * cellSize),
                                size = Size(cellSize * 4, cellSize * 4)
                            )
                            drawRect(
                                color = SurfaceCream,
                                topLeft = Offset((x + 1) * cellSize, (y + 1) * cellSize),
                                size = Size(cellSize * 2, cellSize * 2)
                            )
                            drawRect(
                                color = SunnyYellowDark,
                                topLeft = Offset((x + 1.25f) * cellSize, (y + 1.25f) * cellSize),
                                size = Size(cellSize * 1.5f, cellSize * 1.5f)
                            )
                        }

                        drawFinder(0, 0)
                        drawFinder(gridSize - 4, 0)
                        drawFinder(0, gridSize - 4)

                        // Data matrix dots
                        for (r in 0 until gridSize) {
                            for (c in 0 until gridSize) {
                                val inFinder = (r < 4 && c < 4) || (r < 4 && c >= gridSize - 4) || (r >= gridSize - 4 && c < 4)
                                if (!inFinder) {
                                    val isFilled = ((r * 31 + c * 17 + hashSeed) % 7) < 4
                                    if (isFilled) {
                                        val dotColor = when {
                                            (r + c) % 5 == 0 -> SoftCoralDark
                                            (r + c) % 3 == 0 -> VibrantMintDark
                                            else -> WarmGrayText
                                        }
                                        drawRect(
                                            color = dotColor,
                                            topLeft = Offset(c * cellSize + 1.5f, r * cellSize + 1.5f),
                                            size = Size(cellSize - 3f, cellSize - 3f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Beacon Proximity ID chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = strings.qrBeaconIdPrefix, color = WarmGrayMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = userProfile.beaconId,
                            color = WarmGrayText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = strings.qrScanHint,
                    color = WarmGrayTextSecondary,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .border(1.5.dp, SunnyYellowDark.copy(alpha = 0.4f), RoundedCornerShape(23.dp))
                        .testTag("badge_dialog_done"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SunnyYellow,
                        contentColor = OnSunnyYellow
                    ),
                    shape = RoundedCornerShape(23.dp)
                ) {
                    Text(strings.doneButton, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

