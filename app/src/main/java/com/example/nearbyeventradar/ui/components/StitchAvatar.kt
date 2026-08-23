package com.example.nearbyeventradar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftCoral
import com.example.nearbyeventradar.ui.theme.SunnyYellow
import com.example.nearbyeventradar.ui.theme.SurfaceCream
import com.example.nearbyeventradar.ui.theme.SurfaceWhite
import com.example.nearbyeventradar.ui.theme.VibrantMint
import com.example.nearbyeventradar.ui.theme.WarmGrayText

@Composable
fun StitchFaceIcon(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    hasHibiscus: Boolean = true
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height

            // Warm joyful avatar background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SunnyYellow, Color(0xFFFFC83B)),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.5f
                ),
                radius = w * 0.44f,
                center = Offset(w * 0.5f, h * 0.5f)
            )

            // Outer friendly border
            drawCircle(
                color = OnSunnyYellow,
                radius = w * 0.44f,
                center = Offset(w * 0.5f, h * 0.5f),
                style = Stroke(width = w * 0.05f)
            )

            // Cheerful face eyes
            drawOval(
                color = WarmGrayText,
                topLeft = Offset(w * 0.32f, h * 0.38f),
                size = Size(w * 0.12f, h * 0.18f)
            )
            drawOval(
                color = WarmGrayText,
                topLeft = Offset(w * 0.56f, h * 0.38f),
                size = Size(w * 0.12f, h * 0.18f)
            )

            // Eye sparkle
            drawCircle(
                color = Color.White,
                radius = w * 0.035f,
                center = Offset(w * 0.35f, h * 0.42f)
            )
            drawCircle(
                color = Color.White,
                radius = w * 0.035f,
                center = Offset(w * 0.59f, h * 0.42f)
            )

            // Rosy cheeks (Soft Coral)
            drawCircle(
                color = SoftCoral.copy(alpha = 0.6f),
                radius = w * 0.09f,
                center = Offset(w * 0.25f, h * 0.56f)
            )
            drawCircle(
                color = SoftCoral.copy(alpha = 0.6f),
                radius = w * 0.09f,
                center = Offset(w * 0.75f, h * 0.56f)
            )

            // Cheerful curved smile
            val smilePath = Path().apply {
                moveTo(w * 0.36f, h * 0.58f)
                quadraticTo(w * 0.50f, h * 0.72f, w * 0.64f, h * 0.58f)
            }
            drawPath(
                path = smilePath,
                color = WarmGrayText,
                style = Stroke(width = w * 0.055f)
            )

            // Little celebratory flower or badge in top corner
            if (hasHibiscus) {
                val hx = w * 0.76f
                val hy = h * 0.24f
                val petalR = w * 0.07f

                for (i in 0 until 5) {
                    val angle = (i * 72f) * (Math.PI / 180f)
                    val px = hx + (petalR * Math.cos(angle)).toFloat()
                    val py = hy + (petalR * Math.sin(angle)).toFloat()
                    drawCircle(
                        color = SoftCoral,
                        radius = w * 0.05f,
                        center = Offset(px, py)
                    )
                }
                drawCircle(
                    color = SunnyYellow,
                    radius = w * 0.035f,
                    center = Offset(hx, hy)
                )
            }
        }
    }
}

@Composable
fun StitchBadgeAvatar(
    initials: String,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    isSpecialStitch: Boolean = false
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (isSpecialStitch) {
            StitchFaceIcon(size = size, hasHibiscus = true)
        } else {
            // Kindred Spirits: Circular avatar with thick 2.5dp colorful status border
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(SurfaceWhite)
                    .border(2.5.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(size - 6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(badgeColor.copy(alpha = 0.22f), SurfaceCream)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = WarmGrayText,
                        fontSize = (size.value * 0.38f).sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }

            // Cheerful miniature status dot / floral accent
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(VibrantMint)
                    .border(1.5.dp, SurfaceWhite, CircleShape)
            )
        }
    }
}

