package com.example.nearbyeventradar.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.OutlineVariant
import com.example.nearbyeventradar.ui.theme.SoftBorder
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SurfaceCream
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.WarmGrayMuted
import com.example.nearbyeventradar.ui.theme.WarmGrayText
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarSonarCanvas(
    attendees: List<Attendee>,
    selectedAttendee: Attendee?,
    maxDistanceMeters: Float,
    isScanning: Boolean,
    onSelectAttendee: (Attendee) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val youLabel = strings.youMarker
    val infiniteTransition = rememberInfiniteTransition(label = "kindred_pulse_anim")

    // Gentle expanding pulse ring 1
    val pulseProgress1 by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_1"
    )

    // Gentle expanding pulse ring 2 (staggered)
    val pulseProgress2 by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, delayMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_2"
    )

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("radar_sonar_canvas")
                .pointerInput(attendees, maxDistanceMeters) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.width.coerceAtMost(size.height) / 2f * 0.88f

                        var closestAttendee: Attendee? = null
                        var minDistanceSq = Float.MAX_VALUE
                        val hitThresholdSq = (40.dp.toPx()) * (40.dp.toPx())

                        for (attendee in attendees) {
                            val rRatio = (attendee.distanceMeters / maxDistanceMeters).coerceIn(0.08f, 0.95f)
                            val r = rRatio * radius
                            val angleRad = Math.toRadians((attendee.angleDegrees - 90.0)).toFloat()
                            val blipX = center.x + r * cos(angleRad)
                            val blipY = center.y + r * sin(angleRad)

                            val dx = tapOffset.x - blipX
                            val dy = tapOffset.y - blipY
                            val distSq = dx * dx + dy * dy

                            if (distSq <= hitThresholdSq && distSq < minDistanceSq) {
                                minDistanceSq = distSq
                                closestAttendee = attendee
                            }
                        }

                        if (closestAttendee != null) {
                            onSelectAttendee(closestAttendee)
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadarRadius = size.width.coerceAtMost(size.height) / 2f * 0.88f

            // 1. Kindred Spirits Warm Cream Base with Soft Sunlight Radial Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SurfaceWhite,
                        SurfaceCream,
                        Color(0xFFF7F3EB)
                    ),
                    center = center,
                    radius = maxRadarRadius * 1.05f
                ),
                radius = maxRadarRadius,
                center = center
            )

            // Organic subtle decorative blobs
            drawCircle(
                color = SunnyYellow.copy(alpha = 0.12f),
                radius = maxRadarRadius * 0.42f,
                center = Offset(center.x - maxRadarRadius * 0.35f, center.y - maxRadarRadius * 0.35f)
            )
            drawCircle(
                color = SoftCoral.copy(alpha = 0.10f),
                radius = maxRadarRadius * 0.38f,
                center = Offset(center.x + maxRadarRadius * 0.4f, center.y + maxRadarRadius * 0.25f)
            )
            drawCircle(
                color = VibrantMint.copy(alpha = 0.12f),
                radius = maxRadarRadius * 0.32f,
                center = Offset(center.x - maxRadarRadius * 0.28f, center.y + maxRadarRadius * 0.42f)
            )

            // 2. Gentle Expanding Pulse Wave Animation (Warm growing ripple that fades as it expands)
            if (isScanning) {
                // Pulse 1
                val r1 = maxRadarRadius * pulseProgress1
                val a1 = (1f - pulseProgress1).coerceIn(0f, 1f) * 0.45f
                drawCircle(
                    color = SunnyYellow.copy(alpha = a1),
                    radius = r1,
                    center = center,
                    style = Stroke(width = 6f)
                )
                drawCircle(
                    color = SunnyYellow.copy(alpha = a1 * 0.25f),
                    radius = r1,
                    center = center
                )

                // Pulse 2
                val r2 = maxRadarRadius * pulseProgress2
                val a2 = (1f - pulseProgress2).coerceIn(0f, 1f) * 0.45f
                drawCircle(
                    color = VibrantMint.copy(alpha = a2),
                    radius = r2,
                    center = center,
                    style = Stroke(width = 5f)
                )
            }

            // 3. Concentric Range Rings with Soft Warm Gray Tones
            val ringCount = 4
            val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

            for (i in 1..ringCount) {
                val ringRadius = maxRadarRadius * (i.toFloat() / ringCount)
                val isOuter = (i == ringCount)

                // Outer border
                if (isOuter) {
                    drawCircle(
                        color = SunnyYellow.copy(alpha = 0.45f),
                        radius = ringRadius + 3f,
                        center = center,
                        style = Stroke(width = 5f)
                    )
                }

                drawCircle(
                    color = if (isOuter) OutlineVariant else SoftBorder,
                    radius = ringRadius,
                    center = center,
                    style = Stroke(
                        width = if (isOuter) 2.5f else 1.2f,
                        pathEffect = if (!isOuter && i % 2 == 1) dashedEffect else null
                    )
                )

                // Distance labels in friendly warm typography
                val ringDist = (maxDistanceMeters * (i.toFloat() / ringCount))
                val labelText = if (ringDist >= 10f) "${ringDist.toInt()}m" else String.format("%.1fm", ringDist)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(190, 126, 118, 100)
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        isFakeBoldText = true
                    }
                    drawText(
                        labelText,
                        center.x,
                        center.y - ringRadius + 28f,
                        paint
                    )
                }
            }

            // 4. Subtle Crosshair Axis Lines
            drawLine(
                color = SoftBorder.copy(alpha = 0.7f),
                start = Offset(center.x - maxRadarRadius, center.y),
                end = Offset(center.x + maxRadarRadius, center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = SoftBorder.copy(alpha = 0.7f),
                start = Offset(center.x, center.y - maxRadarRadius),
                end = Offset(center.x, center.y + maxRadarRadius),
                strokeWidth = 1f
            )

            // 5. Draw Attendee & Beacon Blips (Modern Playful Kindred Style)
            for (attendee in attendees) {
                val rRatio = (attendee.distanceMeters / maxDistanceMeters).coerceIn(0.08f, 0.95f)
                val r = rRatio * maxRadarRadius
                val angleRad = Math.toRadians((attendee.angleDegrees - 90.0)).toFloat()
                val blipX = center.x + r * cos(angleRad)
                val blipY = center.y + r * sin(angleRad)
                val blipOffset = Offset(blipX, blipY)

                val blipColor = Color(attendee.role.colorHex)
                val isSelected = selectedAttendee?.id == attendee.id

                if (attendee.isBeacon) {
                    // Venue / Station Beacon marker
                    drawCircle(
                        color = VibrantMint.copy(alpha = 0.3f * breathingScale),
                        radius = 26f * breathingScale,
                        center = blipOffset
                    )
                    // Friendly rounded diamond
                    val diamondSize = 14f
                    val diamondPath = Path().apply {
                        moveTo(blipX, blipY - diamondSize)
                        lineTo(blipX + diamondSize, blipY)
                        lineTo(blipX, blipY + diamondSize)
                        lineTo(blipX - diamondSize, blipY)
                        close()
                    }
                    drawPath(path = diamondPath, color = blipColor)
                    drawCircle(
                        color = SurfaceWhite,
                        radius = 4.5f,
                        center = blipOffset
                    )
                } else {
                    // Attendee Blip
                    if (isSelected) {
                        // Selected Sunny Halo & Soft Coral Ring
                        drawCircle(
                            color = SunnyYellow.copy(alpha = 0.45f),
                            radius = 34f * breathingScale,
                            center = blipOffset
                        )
                        drawCircle(
                            color = SoftCoral,
                            radius = 22f,
                            center = blipOffset,
                            style = Stroke(width = 3.5f)
                        )
                    }

                    // Friendly match / wave indicator
                    if (attendee.matchScore >= 90 || attendee.waveReceived) {
                        drawCircle(
                            color = SoftCoral.copy(alpha = 0.35f * breathingScale),
                            radius = 24f * breathingScale,
                            center = blipOffset
                        )
                        // Coral mini dot
                        drawCircle(
                            color = SoftCoral,
                            radius = 4.5f,
                            center = Offset(blipX + 12f, blipY - 12f)
                        )
                    }

                    // Outer white backing for contrast
                    drawCircle(
                        color = SurfaceWhite,
                        radius = 16f,
                        center = blipOffset
                    )
                    // Thick colorful role border
                    drawCircle(
                        color = blipColor,
                        radius = 14f,
                        center = blipOffset,
                        style = Stroke(width = 3.5f)
                    )
                    // Soft cream/color fill
                    drawCircle(
                        color = blipColor.copy(alpha = 0.25f),
                        radius = 11f,
                        center = blipOffset
                    )
                    // Core dot
                    drawCircle(
                        color = blipColor,
                        radius = 5.5f,
                        center = blipOffset
                    )

                    // Initials Label in high legibility Warm Gray
                    val initials = attendee.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                    drawContext.canvas.nativeCanvas.apply {
                        val textPaint = android.graphics.Paint().apply {
                            color = if (isSelected) android.graphics.Color.parseColor("#735C00") else android.graphics.Color.parseColor("#1B1C1A")
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                            isAntiAlias = true
                        }
                        drawText(initials, blipX, blipY + 34f, textPaint)
                    }
                }
            }

            // 6. Center User Marker ("YOU" - Kindred Pulse Core)
            drawCircle(
                color = SunnyYellow.copy(alpha = 0.4f * breathingScale),
                radius = 30f * breathingScale,
                center = center
            )
            drawCircle(
                color = SurfaceWhite,
                radius = 16f,
                center = center
            )
            drawCircle(
                color = OnSunnyYellow,
                radius = 16f,
                center = center,
                style = Stroke(width = 3f)
            )
            drawCircle(
                color = SunnyYellow,
                radius = 12f,
                center = center
            )
            drawCircle(
                color = VibrantMint,
                radius = 5f,
                center = center
            )

            drawContext.canvas.nativeCanvas.apply {
                val youPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#735C00")
                    textSize = 22f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                drawText(youLabel, center.x, center.y + 34f, youPaint)
            }
        }
    }
}

