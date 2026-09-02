package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.UserRole
import com.example.ui.theme.BarberCrimson
import com.example.ui.theme.BarberEmerald
import com.example.ui.theme.BarberGoldAmber
import com.example.ui.theme.BarberGoldDark
import com.example.ui.theme.BarberGoldLight
import com.example.ui.theme.BarberGoldPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.SlateDarkBackground
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PushNotificationEvent

// ==========================================
// Bento Grid Building Block Components
// ==========================================

@Composable
fun BentoCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    containerColor: Color = SlateCard,
    borderColor: Color = SlateBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .then(clickableModifier),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        content()
    }
}

@Composable
fun BentoStatTile(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    BentoCard(
        modifier = modifier,
        containerColor = SlateCard,
        borderColor = accentColor.copy(alpha = 0.35f),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun BentoSectionHeader(
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = BarberGoldPrimary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.15f))
                    .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ==========================================
// Role Switcher in Bento Layout
// ==========================================

@Composable
fun RoleSwitcherHeader(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    activeUserName: String,
    modifier: Modifier = Modifier
) {
    BentoCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("role_switcher_card"),
        cornerRadius = 18.dp,
        containerColor = SlateCard,
        borderColor = SlateBorder
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(BarberGoldPrimary, BarberGoldDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                UserRole.CLIENTE -> Icons.Default.AccountCircle
                                UserRole.BARBERO -> Icons.Default.ContentCut
                                UserRole.DUENO -> Icons.Default.Storefront
                            },
                            contentDescription = "Rol actual",
                            tint = TextDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ROL ACTIVO: ",
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentRole.label.uppercase(),
                                color = BarberGoldAmber,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = activeUserName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Bento Active Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "En línea",
                        color = BarberEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bento 3-column switcher grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserRole.values().forEach { role ->
                    val isSelected = currentRole == role
                    val bgBrush = if (isSelected) {
                        Brush.linearGradient(listOf(BarberGoldPrimary, BarberGoldDark))
                    } else {
                        Brush.linearGradient(listOf(SlateSurfaceVariant, SlateSurfaceVariant))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgBrush)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BarberGoldPrimary else SlateBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onRoleSelected(role) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("role_button_${role.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (role) {
                                UserRole.CLIENTE -> "💈 Cliente"
                                UserRole.BARBERO -> "✂️ Barbero"
                                UserRole.DUENO -> "👑 Dueño"
                            },
                            color = if (isSelected) TextDark else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// Bento Badges & Status Pills
// ==========================================

@Composable
fun StatusBadge(status: AppointmentStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, borderCol) = when (status) {
        AppointmentStatus.PENDIENTE -> Triple(Color(0x22F59E0B), Color(0xFFFBBF24), Color(0x66F59E0B))
        AppointmentStatus.CONFIRMADA -> Triple(Color(0x223B82F6), Color(0xFF60A5FA), Color(0x663B82F6))
        AppointmentStatus.CANCELADA -> Triple(Color(0x22EF4444), Color(0xFFF87171), Color(0x66EF4444))
        AppointmentStatus.COMPLETADA -> Triple(Color(0x2210B981), Color(0xFF34D399), Color(0x6610B981))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderCol, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ModalityBadge(modality: BarberModality?, modifier: Modifier = Modifier) {
    if (modality == null) return
    val isIndependent = modality == BarberModality.INDEPENDIENTE
    val bgColor = if (isIndependent) Color(0x228B5CF6) else Color(0x2206B6D4)
    val textColor = if (isIndependent) Color(0xFFA78BFA) else Color(0xFF22D3EE)
    val borderCol = if (isIndependent) Color(0x558B5CF6) else Color(0x5506B6D4)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "Modo: ${modality.label}",
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==========================================
// Push Notification Banner
// ==========================================

@Composable
fun NotificationBanner(
    event: PushNotificationEvent?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = event != null,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut(),
        modifier = modifier
    ) {
        if (event != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onDismiss() }
                    .testTag("push_notification_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BarberGoldPrimary))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x33F59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Push Notification",
                            tint = BarberGoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = event.message,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// Procedural QR Code Canvas
// ==========================================

@Composable
fun QrCodeCanvas(
    content: String,
    modifier: Modifier = Modifier,
    qrColor: Color = TextDark,
    backgroundColor: Color = Color.White
) {
    // Procedural QR matrix visualizer for Bento QR Tile
    Canvas(
        modifier = modifier
            .size(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        val sizePx = size.width
        val moduleCount = 21
        val cellSize = sizePx / moduleCount

        val hash = content.hashCode()

        fun drawFinderPattern(startX: Int, startY: Int) {
            // Outer square (7x7)
            drawRect(
                color = qrColor,
                topLeft = Offset(startX * cellSize, startY * cellSize),
                size = Size(7 * cellSize, 7 * cellSize)
            )
            // Inner white square (5x5)
            drawRect(
                color = backgroundColor,
                topLeft = Offset((startX + 1) * cellSize, (startY + 1) * cellSize),
                size = Size(5 * cellSize, 5 * cellSize)
            )
            // Inner solid center (3x3)
            drawRect(
                color = qrColor,
                topLeft = Offset((startX + 2) * cellSize, (startY + 2) * cellSize),
                size = Size(3 * cellSize, 3 * cellSize)
            )
        }

        drawFinderPattern(0, 0)
        drawFinderPattern(moduleCount - 7, 0)
        drawFinderPattern(0, moduleCount - 7)

        for (r in 0 until moduleCount) {
            for (c in 0 until moduleCount) {
                val isFinder1 = r < 8 && c < 8
                val isFinder2 = r < 8 && c >= moduleCount - 8
                val isFinder3 = r >= moduleCount - 8 && c < 8

                if (!isFinder1 && !isFinder2 && !isFinder3) {
                    val bit = ((hash xor (r * 31 + c * 17)) and (1 shl ((r + c) % 16))) != 0
                    if (bit) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(c * cellSize, r * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    }
}
