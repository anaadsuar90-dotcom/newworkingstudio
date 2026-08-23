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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.nearbyeventradar.data.model.Attendee
import com.example.nearbyeventradar.data.model.ChatMessage
import com.example.nearbyeventradar.ui.i18n.LocalAppStrings
import com.example.nearbyeventradar.ui.theme.CreamBg
import com.example.nearbyeventradar.ui.theme.OnSunnyYellow
import com.example.nearbyeventradar.ui.theme.SoftBorder
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    attendee: Attendee,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val roleColor = Color(attendee.role.colorHex)
    val isSpecialStitch = attendee.id.contains("stitch", ignoreCase = true) || attendee.id.contains("lilo", ignoreCase = true)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CreamBg,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .imePadding()
                .testTag("chat_bottom_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val initials = attendee.name.split(" ")
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .take(2)
                        .joinToString("")

                    StitchBadgeAvatar(
                        initials = initials,
                        badgeColor = roleColor,
                        size = 46.dp,
                        isSpecialStitch = isSpecialStitch
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = attendee.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WarmGrayText
                        )
                        Text(
                            text = strings.directProximityHeader(String.format("%.1fm", attendee.distanceMeters)),
                            color = VibrantMintDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("chat_close")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        tint = WarmGrayTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Icebreaker Prompt Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = strings.chatIcebreakerPrompt,
                        color = WarmGrayTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .padding(10.dp)
                            .testTag("chip_icebreaker_1")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .height(280.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.emptyChatNotice(attendee.name),
                                color = WarmGrayMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderIsMe
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            ),
                            color = if (isMe) SunnyYellow else SurfaceWhite,
                            border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, SoftBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                                Text(
                                    text = msg.text,
                                    color = if (isMe) OnSunnyYellow else WarmGrayText,
                                    fontSize = 13.sp
                                )
                                val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
                                Text(
                                    text = timeStr,
                                    color = if (isMe) OnSunnyYellow.copy(alpha = 0.7f) else WarmGrayMuted,
                                    fontSize = 9.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text Input Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(strings.chatInputPlaceholder, fontSize = 13.sp, color = WarmGrayMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedBorderColor = SunnyYellowDark,
                        unfocusedBorderColor = SoftBorder,
                        focusedTextColor = WarmGrayText,
                        unfocusedTextColor = WarmGrayText
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SunnyYellow)
                        .border(1.5.dp, SunnyYellowDark.copy(alpha = 0.5f), CircleShape)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = strings.sendButton,
                        tint = OnSunnyYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

