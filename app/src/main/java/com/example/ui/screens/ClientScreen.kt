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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ScrollableTabRow
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
import com.example.data.local.BarbershopEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.ServiceEntity
import com.example.data.local.UserEntity
import com.example.data.model.AppointmentStatus
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
fun ClientScreen(
    barbershop: BarbershopEntity?,
    barbers: List<UserEntity>,
    services: List<ServiceEntity>,
    appointments: List<AppointmentEntity>,
    reviews: List<ReviewEntity>,
    onBookAppointment: (barber: UserEntity, local: BarbershopEntity, service: ServiceEntity, date: String, time: String, paymentMethod: PaymentMethod, hasProof: Boolean) -> Unit,
    onCancelAppointment: (appointmentId: String, reason: String) -> Unit,
    onSubmitReview: (appointmentId: String, barberId: String, rating: Int, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Servicios & Agendar, 1: Mis Citas, 2: Reseñas
    var bookingService by remember { mutableStateOf<ServiceEntity?>(null) }
    var reviewAppointment by remember { mutableStateOf<AppointmentEntity?>(null) }
    var cancelAppointmentDialog by remember { mutableStateOf<AppointmentEntity?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Bento Barbershop Header Tile
        barbershop?.let { shop ->
            BentoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = shop.nombre,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x3310B981))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ABIERTO", color = BarberEmerald, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Dirección",
                                    tint = BarberGoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${shop.direccion} • ${shop.ciudad}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x22F59E0B))
                                .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("COP ($)", color = BarberGoldAmber, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bento micro chips row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🕒 ${shop.horarios}",
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⭐ 4.9 (120+ reseñas)",
                                color = BarberGoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Bento Navigation Tabs
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
                        "✂️ Servicios & Reserva",
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
                        "📅 Mis Citas (${appointments.size})",
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
                        "⭐ Reseñas (${reviews.size})",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 2) BarberGoldAmber else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        when (selectedTab) {
            0 -> ServicesAndBookingTab(
                services = services,
                barbers = barbers,
                onSelectService = { bookingService = it }
            )
            1 -> MyAppointmentsTab(
                appointments = appointments,
                onCancel = { cancelAppointmentDialog = it },
                onReview = { reviewAppointment = it }
            )
            2 -> ReviewsTab(reviews = reviews)
        }
    }

    // Booking Dialog Modal
    bookingService?.let { service ->
        BookingModal(
            service = service,
            barbershop = barbershop,
            barbers = barbers,
            onDismiss = { bookingService = null },
            onConfirm = { barber, local, srv, date, time, payMethod, hasProof ->
                onBookAppointment(barber, local, srv, date, time, payMethod, hasProof)
                bookingService = null
                selectedTab = 1
            }
        )
    }

    // Review Dialog Modal
    reviewAppointment?.let { app ->
        ReviewModal(
            appointment = app,
            onDismiss = { reviewAppointment = null },
            onSubmit = { rating, comment ->
                onSubmitReview(app.id, app.idBarbero, rating, comment)
                reviewAppointment = null
            }
        )
    }

    // Cancellation Dialog Modal
    cancelAppointmentDialog?.let { app ->
        CancelModal(
            appointment = app,
            onDismiss = { cancelAppointmentDialog = null },
            onConfirm = { reason ->
                onCancelAppointment(app.id, reason)
                cancelAppointmentDialog = null
            }
        )
    }
}

@Composable
fun ServicesAndBookingTab(
    services: List<ServiceEntity>,
    barbers: List<UserEntity>,
    onSelectService: (ServiceEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Bento Barbers carousel section
        item {
            BentoSectionHeader(
                title = "Equipo de Barberos",
                subtitle = "Selecciona un profesional calificado",
                badgeText = "${barbers.size} activos",
                badgeColor = BarberCyan,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp)
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(barbers) { barber ->
                    BentoCard(
                        modifier = Modifier.width(185.dp),
                        cornerRadius = 14.dp,
                        containerColor = SlateCard,
                        borderColor = SlateBorder
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(BarberGoldPrimary, BarberGoldDark)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = barber.nombre.take(1),
                                        color = TextDark,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = barber.nombre,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                    ModalityBadge(modality = barber.modalidad)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            BentoSectionHeader(
                title = "Catálogo de Servicios & Tarifas",
                subtitle = "Precios oficiales en Pesos Colombianos (COP)",
                badgeText = "${services.size} disponibles",
                badgeColor = BarberGoldPrimary,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp)
            )
        }

        items(services) { service ->
            BentoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("service_item_${service.id}"),
                cornerRadius = 16.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BarberGoldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = service.nombre,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = service.descripcion,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SlateSurfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = "Duración",
                                            tint = BarberGoldLight,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${service.duracionMinutos} min",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                text = Formatters.formatCop(service.costoCop),
                                color = BarberGoldAmber,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onSelectService(service) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary),
                                modifier = Modifier.testTag("book_button_${service.id}")
                            ) {
                                Text("Agendar", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

@Composable
fun MyAppointmentsTab(
    appointments: List<AppointmentEntity>,
    onCancel: (AppointmentEntity) -> Unit,
    onReview: (AppointmentEntity) -> Unit
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
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = "Sin citas",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aún no tienes citas agendadas",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Selecciona un servicio en la pestaña anterior para programar tu corte.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                        .testTag("appointment_card_${app.id}"),
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
                                text = app.servicioNombre,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            StatusBadge(status = app.estado)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "✂️ Barbero: ${app.barberoNombre}",
                            color = BarberGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "📅 ${app.fecha} a las ${app.hora}",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "💳 Pago: ${app.metodoPago.label} (${Formatters.formatCop(app.valorCop)})",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        if (app.comprobanteUrl != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📎 Comprobante de transferencia adjunto",
                                color = BarberEmerald,
                                fontSize = 11.sp
                            )
                        }

                        if (app.motivoCancelacion != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Motivo: ${app.motivoCancelacion}",
                                color = BarberCrimson,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (app.estado == AppointmentStatus.PENDIENTE || app.estado == AppointmentStatus.CONFIRMADA) {
                                OutlinedButton(
                                    onClick = { onCancel(app) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BarberCrimson),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("cancel_btn_${app.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Cancelar", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cancelar (>1h)", fontSize = 12.sp)
                                }
                            }

                            if (app.estado == AppointmentStatus.COMPLETADA) {
                                Button(
                                    onClick = { onReview(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("review_btn_${app.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Calificar", tint = TextDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Calificar Servicio", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
fun ReviewsTab(reviews: List<ReviewEntity>) {
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
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = "Verificado", tint = BarberEmerald, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Todas las reseñas provienen de citas 100% verificadas y completadas en el sistema.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        items(reviews) { rev ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
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
                            text = rev.clienteNombre,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row {
                            repeat(5) { starIndex ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (starIndex < rev.valoracion) BarberGoldPrimary else SlateBorder,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${rev.comentario}\"",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Fecha: ${rev.fecha} • Cita Verificada",
                        color = TextMuted,
                        fontSize = 11.sp
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
fun BookingModal(
    service: ServiceEntity,
    barbershop: BarbershopEntity?,
    barbers: List<UserEntity>,
    onDismiss: () -> Unit,
    onConfirm: (barber: UserEntity, local: BarbershopEntity, service: ServiceEntity, date: String, time: String, paymentMethod: PaymentMethod, hasProof: Boolean) -> Unit
) {
    var selectedBarber by remember { mutableStateOf(barbers.firstOrNull()) }
    var selectedDate by remember { mutableStateOf("2026-09-03") }
    var selectedTime by remember { mutableStateOf("11:00 AM") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.EFECTIVO) }
    var hasUploadedProof by remember { mutableStateOf(false) }

    val dates = listOf("2026-09-02", "2026-09-03", "2026-09-04", "2026-09-05")
    val timeSlots = listOf("09:00 AM", "10:00 AM", "11:00 AM", "02:30 PM", "04:00 PM", "05:30 PM")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Agendar Cita",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Servicio: ${service.nombre}",
                        color = BarberGoldAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Valor: ${Formatters.formatCop(service.costoCop)} (${service.duracionMinutos} min)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                item {
                    Text("Selecciona tu Barbero:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        barbers.forEach { barber ->
                            val isSelected = selectedBarber?.id == barber.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0x33F59E0B) else SlateSurfaceVariant)
                                    .border(1.dp, if (isSelected) BarberGoldPrimary else SlateBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedBarber = barber }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedBarber = barber },
                                    colors = RadioButtonDefaults.colors(selectedColor = BarberGoldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(barber.nombre, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    ModalityBadge(modality = barber.modalidad)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Fecha:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(dates) { d ->
                            val isSelected = selectedDate == d
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BarberGoldPrimary else SlateSurfaceVariant)
                                    .border(1.dp, if (isSelected) BarberGoldDark else SlateBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedDate = d }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(d, color = if (isSelected) TextDark else TextSecondary, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                item {
                    Text("Hora:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(timeSlots) { t ->
                            val isSelected = selectedTime == t
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BarberGoldPrimary else SlateSurfaceVariant)
                                    .border(1.dp, if (isSelected) BarberGoldDark else SlateBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedTime = t }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(t, color = if (isSelected) TextDark else TextSecondary, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                item {
                    Text("Método de Pago:", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentMethod.values().forEach { method ->
                            val isSelected = selectedPaymentMethod == method
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0x33F59E0B) else SlateSurfaceVariant)
                                    .border(1.dp, if (isSelected) BarberGoldPrimary else SlateBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedPaymentMethod = method }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = if (method == PaymentMethod.EFECTIVO) Icons.Default.Payments else Icons.Default.AccountBalance,
                                        contentDescription = method.label,
                                        tint = if (isSelected) BarberGoldPrimary else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (method == PaymentMethod.EFECTIVO) "Efectivo" else "Transferencia",
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (selectedPaymentMethod == PaymentMethod.TRANSFERENCIA) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SlateBorder))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Nequi / Daviplata: 310 456 7890", color = BarberGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Bancolombia Ahorros: 245-89102-11", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (hasUploadedProof) Color(0x3310B981) else Color(0x33F59E0B))
                                        .border(1.dp, if (hasUploadedProof) BarberEmerald else BarberGoldPrimary, RoundedCornerShape(8.dp))
                                        .clickable { hasUploadedProof = !hasUploadedProof }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (hasUploadedProof) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (hasUploadedProof) BarberEmerald else BarberGoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (hasUploadedProof) "Comprobante Adjuntado (nequi_3910.jpg)" else "Adjuntar Comprobante de Pago",
                                        color = if (hasUploadedProof) BarberEmerald else BarberGoldPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val barber = selectedBarber ?: barbers.first()
                    val local = barbershop ?: return@Button
                    onConfirm(barber, local, service, selectedDate, selectedTime, selectedPaymentMethod, hasUploadedProof)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary),
                modifier = Modifier.testTag("confirm_booking_button")
            ) {
                Text("Confirmar y Agendar", color = TextDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = SlateCardElevated,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun ReviewModal(
    appointment: AppointmentEntity,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Calificar Servicio Verificado", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Servicio: ${appointment.servicioNombre} con ${appointment.barberoNombre}",
                    color = BarberGoldLight,
                    fontSize = 13.sp
                )
                Text(
                    text = "Selecciona tu valoración:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star estrellas",
                                tint = if (star <= rating) BarberGoldPrimary else SlateBorder,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Escribe tu experiencia (opcional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarberGoldPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalComment = if (comment.isBlank()) "¡Excelente atención y resultado impecable!" else comment
                    onSubmit(rating, finalComment)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary)
            ) {
                Text("Publicar Reseña", color = TextDark, fontWeight = FontWeight.Bold)
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

@Composable
fun CancelModal(
    appointment: AppointmentEntity,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("Imprevisto personal") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cancelar Cita", color = BarberCrimson, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "¿Deseas cancelar tu cita para ${appointment.servicioNombre} programada para el ${appointment.fecha} a las ${appointment.hora}?",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x22EF4444)),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BarberCrimson.copy(alpha = 0.5f)))
                ) {
                    Text(
                        text = "⏱️ Política de la Barbería: La cancelación se realiza con más de 1 hora de anticipación sin penalidad.",
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Motivo de cancelación") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarberCrimson,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarberCrimson)
            ) {
                Text("Confirmar Cancelación", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Volver", color = TextSecondary)
            }
        },
        containerColor = SlateCardElevated,
        shape = RoundedCornerShape(20.dp)
    )
}
