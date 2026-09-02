package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppointmentEntity
import com.example.data.local.AvailabilityEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.UserEntity
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.PaymentMethod
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoSectionHeader
import com.example.ui.components.BentoStatTile
import com.example.ui.components.ModalityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BarberCrimson
import com.example.ui.theme.BarberCyan
import com.example.ui.theme.BarberEmerald
import com.example.ui.theme.BarberGoldAmber
import com.example.ui.theme.BarberGoldDark
import com.example.ui.theme.BarberGoldLight
import com.example.ui.theme.BarberGoldPrimary
import com.example.ui.theme.BarberIndigo
import com.example.ui.theme.BarberPurple
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
import com.example.util.Formatters

@Composable
fun BarberScreen(
    barbers: List<UserEntity>,
    selectedBarberId: String,
    onSelectBarber: (String) -> Unit,
    appointments: List<AppointmentEntity>,
    availability: List<AvailabilityEntity>,
    reviews: List<ReviewEntity>,
    onConfirmAppointment: (AppointmentEntity) -> Unit,
    onRejectAppointment: (AppointmentEntity) -> Unit,
    onCompleteAppointment: (AppointmentEntity) -> Unit,
    onToggleModality: (barberId: String, modality: BarberModality) -> Unit,
    onToggleAvailability: (dayIndex: Int, isAvailable: Boolean, start: String, end: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Citas, 1: Disponibilidad, 2: Reseñas & Métricas
    val activeBarber = barbers.firstOrNull { it.id == selectedBarberId } ?: barbers.firstOrNull()
    var inspectPaymentAppointment by remember { mutableStateOf<AppointmentEntity?>(null) }

    val pendingCount = appointments.count { it.estado == AppointmentStatus.PENDIENTE }
    val completedCount = appointments.count { it.estado == AppointmentStatus.COMPLETADA }
    val totalEarningsCop = appointments.filter { it.estado == AppointmentStatus.COMPLETADA }.sumOf { it.valorCop }

    Column(modifier = modifier.fillMaxSize()) {
        // Bento Barber Selector & Modality Card
        BentoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            cornerRadius = 18.dp,
            containerColor = SlateCard,
            borderColor = SlateBorder
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SELECCIONAR PERFIL DE BARBERO",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(barbers) { barber ->
                        val isSelected = barber.id == selectedBarberId
                        val bgBrush = if (isSelected) {
                            Brush.linearGradient(listOf(BarberGoldPrimary, BarberGoldDark))
                        } else {
                            Brush.linearGradient(listOf(SlateSurfaceVariant, SlateSurfaceVariant))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgBrush)
                                .border(1.dp, if (isSelected) BarberGoldPrimary else SlateBorder, RoundedCornerShape(10.dp))
                                .clickable { onSelectBarber(barber.id) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("select_barber_${barber.id}")
                        ) {
                            Text(
                                text = barber.nombre,
                                color = if (isSelected) TextDark else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Active Barber Modality Bento Block
                activeBarber?.let { barber ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateSurfaceVariant)
                            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MODALIDAD ACTIVA", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                            Text(
                                text = if (barber.modalidad == BarberModality.INDEPENDIENTE) "💈 Independiente (Tarifas Propias)" else "🏢 Empleado (Tarifas de Barbería)",
                                color = if (barber.modalidad == BarberModality.INDEPENDIENTE) BarberPurple else BarberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                val next = if (barber.modalidad == BarberModality.INDEPENDIENTE) BarberModality.EMPLEADO else BarberModality.INDEPENDIENTE
                                onToggleModality(barber.id, next)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_modality_button")
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Cambiar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cambiar", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Bento Quick Stats Row (2-column Bento Grid)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BentoStatTile(
                title = "Por Confirmar",
                value = "$pendingCount",
                icon = Icons.Default.ReceiptLong,
                accentColor = BarberGoldAmber,
                subtitle = "Citas pendientes",
                modifier = Modifier.weight(1f)
            )
            BentoStatTile(
                title = "Completadas",
                value = "$completedCount",
                icon = Icons.Default.Work,
                accentColor = BarberEmerald,
                subtitle = Formatters.formatCop(totalEarningsCop),
                modifier = Modifier.weight(1f)
            )
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = SlateDarkBackground,
            contentColor = BarberGoldPrimary,
            edgePadding = 16.dp,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        "📋 Agenda (${appointments.size})",
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 0) BarberGoldAmber else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "🕒 Disponibilidad Semanal",
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 1) BarberGoldAmber else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        "⭐ Métricas & Reseñas",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 2) BarberGoldAmber else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        when (selectedTab) {
            0 -> BarberAppointmentsTab(
                appointments = appointments,
                onConfirm = onConfirmAppointment,
                onReject = onRejectAppointment,
                onInspectPayment = { inspectPaymentAppointment = it }
            )
            1 -> BarberAvailabilityTab(
                availability = availability,
                onToggle = onToggleAvailability
            )
            2 -> BarberMetricsTab(
                reviews = reviews,
                appointments = appointments
            )
        }
    }

    // Payment verification dialog modal
    inspectPaymentAppointment?.let { app ->
        PaymentVerificationModal(
            appointment = app,
            onDismiss = { inspectPaymentAppointment = null },
            onConfirmPaymentAndComplete = {
                onCompleteAppointment(app)
                inspectPaymentAppointment = null
            }
        )
    }
}

@Composable
fun BarberAppointmentsTab(
    appointments: List<AppointmentEntity>,
    onConfirm: (AppointmentEntity) -> Unit,
    onReject: (AppointmentEntity) -> Unit,
    onInspectPayment: (AppointmentEntity) -> Unit
) {
    if (appointments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No hay citas registradas para este barbero.", color = TextSecondary, fontSize = 14.sp)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(appointments) { app ->
                BentoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("barber_app_card_${app.id}"),
                    cornerRadius = 16.dp,
                    containerColor = SlateCard,
                    borderColor = SlateBorder
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = app.clienteNombre,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            StatusBadge(status = app.estado)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "✂️ ${app.servicioNombre} • ${Formatters.formatCop(app.valorCop)}",
                            color = BarberGoldAmber,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "📅 ${app.fecha} • 🕒 ${app.hora}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "💳 Pago: ${app.metodoPago.label}",
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions according to status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (app.estado == AppointmentStatus.PENDIENTE) {
                                OutlinedButton(
                                    onClick = { onReject(app) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BarberCrimson),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.padding(end = 8.dp).testTag("reject_btn_${app.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Rechazar", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rechazar", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { onConfirm(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("confirm_btn_${app.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Confirmar", tint = TextDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Confirmar Cita", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            if (app.estado == AppointmentStatus.CONFIRMADA) {
                                Button(
                                    onClick = { onInspectPayment(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BarberEmerald),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("verify_payment_btn_${app.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Verificar", tint = TextDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Verificar Pago & Completar", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            if (app.estado == AppointmentStatus.COMPLETADA) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Completada", tint = BarberEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Servicio Completado & Pagado", color = BarberEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BarberAvailabilityTab(
    availability: List<AvailabilityEntity>,
    onToggle: (dayIndex: Int, isAvailable: Boolean, start: String, end: String) -> Unit
) {
    val dayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = BarberGoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Activa los días en los que atiendes citas y define tu franja de servicio en la barbería.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        items((0..6).toList()) { dayIndex ->
            val existing = availability.firstOrNull { it.diaSemana == dayIndex }
            val isAvail = existing?.disponible ?: true
            val startHour = existing?.horaInicio ?: "08:00"
            val endHour = existing?.horaFin ?: "18:00"

            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = if (isAvail) SlateCard else SlateSurfaceVariant,
                borderColor = if (isAvail) BarberGoldPrimary.copy(alpha = 0.3f) else SlateBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dayNames[dayIndex],
                            color = if (isAvail) TextPrimary else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isAvail) "Horario: $startHour - $endHour" else "Cerrado / Día libre",
                            color = if (isAvail) BarberGoldLight else TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isAvail,
                        onCheckedChange = { checked ->
                            onToggle(dayIndex, checked, startHour, endHour)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BarberGoldPrimary,
                            checkedTrackColor = BarberGoldDark
                        ),
                        modifier = Modifier.testTag("switch_day_$dayIndex")
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BarberMetricsTab(
    reviews: List<ReviewEntity>,
    appointments: List<AppointmentEntity>
) {
    val completedCount = appointments.count { it.estado == AppointmentStatus.COMPLETADA }
    val totalRevenueCop = appointments
        .filter { it.estado == AppointmentStatus.COMPLETADA }
        .sumOf { it.valorCop }

    val averageRating = if (reviews.isNotEmpty()) reviews.map { it.valoracion }.average() else 5.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoStatTile(
                    title = "Calificación",
                    value = String.format("%.1f ★", averageRating),
                    icon = Icons.Default.Star,
                    accentColor = BarberGoldAmber,
                    subtitle = "${reviews.size} reseñas verificadas",
                    modifier = Modifier.weight(1f)
                )

                BentoStatTile(
                    title = "Ingresos COP",
                    value = Formatters.formatCop(totalRevenueCop),
                    icon = Icons.Default.Payments,
                    accentColor = BarberEmerald,
                    subtitle = "$completedCount citas completadas",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            BentoSectionHeader(
                title = "Reseñas Verificadas de Clientes",
                subtitle = "Opiniones de clientes que completaron su cita",
                badgeText = "${reviews.size} total",
                badgeColor = BarberGoldPrimary,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp)
            )
        }

        items(reviews) { rev ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(rev.clienteNombre, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row {
                            repeat(rev.valoracion) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = BarberGoldPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("\"${rev.comentario}\"", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📅 ${rev.fecha} • Cita Verificada", color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaymentVerificationModal(
    appointment: AppointmentEntity,
    onDismiss: () -> Unit,
    onConfirmPaymentAndComplete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Verificación de Pago", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Cliente: ${appointment.clienteNombre}",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Servicio: ${appointment.servicioNombre}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Monto a cobrar: ${Formatters.formatCop(appointment.valorCop)}",
                    color = BarberGoldAmber,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

                BentoCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 12.dp,
                    containerColor = SlateSurfaceVariant,
                    borderColor = SlateBorder
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (appointment.metodoPago == PaymentMethod.EFECTIVO) Icons.Default.Payments else Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = BarberGoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Método: ${appointment.metodoPago.label}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (appointment.metodoPago == PaymentMethod.TRANSFERENCIA) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Comprobante: ${appointment.comprobanteUrl ?: "Comprobante digital validado en Firebase Storage"}", color = BarberEmerald, fontSize = 11.sp)
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Recibe ${Formatters.formatCop(appointment.valorCop)} en efectivo en el local.", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmPaymentAndComplete,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarberEmerald),
                modifier = Modifier.testTag("confirm_payment_complete_btn")
            ) {
                Text("Confirmar Pago & Marcar Completada", color = TextDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cerrar", color = TextSecondary)
            }
        },
        containerColor = SlateCardElevated,
        shape = RoundedCornerShape(20.dp)
    )
}
