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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.local.ServiceEntity
import com.example.data.local.UserEntity
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.ui.components.BentoCard
import com.example.ui.components.BentoSectionHeader
import com.example.ui.components.BentoStatTile
import com.example.ui.components.ModalityBadge
import com.example.ui.components.QrCodeCanvas
import com.example.ui.components.StatusBadge
import com.example.ui.theme.BarberBlue
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
fun OwnerScreen(
    barbershop: BarbershopEntity?,
    barbers: List<UserEntity>,
    services: List<ServiceEntity>,
    appointments: List<AppointmentEntity>,
    onSaveService: (id: String?, localId: String, barberId: String?, nombre: String, descripcion: String, costoCop: Double, duracionMinutos: Int) -> Unit,
    onDeleteService: (serviceId: String) -> Unit,
    onUpdateBarbershop: (local: BarbershopEntity, nombre: String, direccion: String, ciudad: String, telefono: String, horarios: String) -> Unit,
    onToggleBarberModality: (barberId: String, modality: BarberModality) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Local & QR, 1: Servicios, 2: Equipo Barberos, 3: Reportes COP
    var editingService by remember { mutableStateOf<ServiceEntity?>(null) }
    var isAddingService by remember { mutableStateOf(false) }
    var isEditingShop by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
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
                        "🏢 Local & QR",
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
                        "✂️ Servicios (${services.size})",
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
                        "👥 Barberos (${barbers.size})",
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 2) BarberGoldAmber else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = {
                    Text(
                        "📊 Reportes COP",
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == 3) BarberGoldAmber else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        when (selectedTab) {
            0 -> OwnerLocalAndQrTab(
                barbershop = barbershop,
                onEditShop = { isEditingShop = true }
            )
            1 -> OwnerServicesTab(
                services = services,
                onAddService = { isAddingService = true },
                onEditService = { editingService = it },
                onDeleteService = onDeleteService
            )
            2 -> OwnerBarbersTab(
                barbers = barbers,
                onToggleModality = onToggleBarberModality
            )
            3 -> OwnerFinancialReportTab(
                appointments = appointments,
                barbers = barbers
            )
        }
    }

    // Add or Edit Service Modal
    if (isAddingService || editingService != null) {
        val targetService = editingService
        ServiceEditorModal(
            service = targetService,
            localId = barbershop?.id ?: "loc_1",
            onDismiss = {
                isAddingService = false
                editingService = null
            },
            onSave = { id, locId, barberId, name, desc, price, duration ->
                onSaveService(id, locId, barberId, name, desc, price, duration)
                isAddingService = false
                editingService = null
            }
        )
    }

    // Edit Barbershop Modal
    if (isEditingShop && barbershop != null) {
        BarbershopEditorModal(
            shop = barbershop,
            onDismiss = { isEditingShop = false },
            onSave = { name, address, city, phone, hours ->
                onUpdateBarbershop(barbershop, name, address, city, phone, hours)
                isEditingShop = false
            }
        )
    }
}

@Composable
fun OwnerLocalAndQrTab(
    barbershop: BarbershopEntity?,
    onEditShop: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        barbershop?.let { shop ->
            item {
                BentoCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 18.dp,
                    containerColor = SlateCard,
                    borderColor = SlateBorder
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PERFIL DE LA BARBERÍA",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = shop.nombre,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                            IconButton(
                                onClick = onEditShop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SlateSurfaceVariant)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = BarberGoldPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("📍 ${shop.direccion}, ${shop.ciudad}", color = TextSecondary, fontSize = 13.sp)
                        Text("📞 ${shop.telefono}", color = TextSecondary, fontSize = 13.sp)
                        Text("🕒 ${shop.horarios}", color = BarberGoldLight, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            item {
                BentoCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 18.dp,
                    containerColor = SlateCard,
                    borderColor = SlateBorder
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = BarberGoldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Código QR de Mostrador",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "Colócalo en recepción para que los clientes escaneen y agenden directamente con tu equipo.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        QrCodeCanvas(content = "https://barberhub.co/barberia/${shop.id}")
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "https://barberhub.co/barberia/${shop.id}",
                                color = BarberGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
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
fun OwnerServicesTab(
    services: List<ServiceEntity>,
    onAddService: () -> Unit,
    onEditService: (ServiceEntity) -> Unit,
    onDeleteService: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catálogo Oficial de Precios (COP)",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Button(
                    onClick = onAddService,
                    colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_service_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Añadir Servicio", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        items(services) { service ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(service.nombre, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(service.descripcion, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
                        Text(
                            text = "${Formatters.formatCop(service.costoCop)} • ${service.duracionMinutos} min",
                            color = BarberGoldAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row {
                        IconButton(onClick = { onEditService(service) }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = BarberGoldLight)
                        }
                        IconButton(onClick = { onDeleteService(service.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = BarberCrimson)
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
fun OwnerBarbersTab(
    barbers: List<UserEntity>,
    onToggleModality: (barberId: String, modality: BarberModality) -> Unit
) {
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
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("GESTIÓN DE EQUIPO Y MODALIDADES", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Independiente: Gestiona sus propios servicios y tarifas.\n• Empleado: Atiende según catálogo y precios oficiales del local.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(barbers) { barber ->
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(barber.nombre, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("📞 ${barber.telefono}", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        ModalityBadge(modality = barber.modalidad)
                    }

                    OutlinedButton(
                        onClick = {
                            val newMod = if (barber.modalidad == BarberModality.INDEPENDIENTE) BarberModality.EMPLEADO else BarberModality.INDEPENDIENTE
                            onToggleModality(barber.id, newMod)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (barber.modalidad == BarberModality.INDEPENDIENTE) "Hacer Empleado" else "Hacer Independiente",
                            fontSize = 11.sp
                        )
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
fun OwnerFinancialReportTab(
    appointments: List<AppointmentEntity>,
    barbers: List<UserEntity>
) {
    val completed = appointments.filter { it.estado == AppointmentStatus.COMPLETADA }
    val totalRevenueCop = completed.sumOf { it.valorCop }
    val pendingCount = appointments.count { it.estado == AppointmentStatus.PENDIENTE }
    val confirmedCount = appointments.count { it.estado == AppointmentStatus.CONFIRMADA }
    val cancelledCount = appointments.count { it.estado == AppointmentStatus.CANCELADA }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 18.dp,
                containerColor = SlateCard,
                borderColor = BarberGoldPrimary.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("INGRESOS TOTALES (COP)", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatCop(totalRevenueCop),
                        color = BarberGoldAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                    Text("${completed.size} citas completadas y cobradas en el sistema", color = BarberEmerald, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        // Bento 3-column status breakdown
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BentoCard(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 12.dp,
                    containerColor = SlateCard,
                    borderColor = Color(0x66F59E0B)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pendientes", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("$pendingCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                BentoCard(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 12.dp,
                    containerColor = SlateCard,
                    borderColor = Color(0x663B82F6)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Confirmadas", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("$confirmedCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                BentoCard(
                    modifier = Modifier.weight(1f),
                    cornerRadius = 12.dp,
                    containerColor = SlateCard,
                    borderColor = Color(0x66EF4444)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Canceladas", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("$cancelledCount", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            BentoSectionHeader(
                title = "Productividad por Barbero",
                subtitle = "Desglose financiero individual",
                badgeText = "${barbers.size} barberos",
                badgeColor = BarberCyan,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 4.dp)
            )
        }

        items(barbers) { barber ->
            val barberCompleted = completed.filter { it.idBarbero == barber.id }
            val barberTotalCop = barberCompleted.sumOf { it.valorCop }

            BentoCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 14.dp,
                containerColor = SlateCard,
                borderColor = SlateBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(barber.nombre, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        ModalityBadge(modality = barber.modalidad)
                        Text(
                            text = "${barberCompleted.size} servicios completados",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = Formatters.formatCop(barberTotalCop),
                        color = BarberGoldAmber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
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
fun ServiceEditorModal(
    service: ServiceEntity?,
    localId: String,
    onDismiss: () -> Unit,
    onSave: (id: String?, localId: String, barberId: String?, nombre: String, descripcion: String, costoCop: Double, duracionMinutos: Int) -> Unit
) {
    var name by remember { mutableStateOf(service?.nombre ?: "") }
    var description by remember { mutableStateOf(service?.descripcion ?: "") }
    var priceText by remember { mutableStateOf(service?.costoCop?.toInt()?.toString() ?: "35000") }
    var durationText by remember { mutableStateOf(service?.duracionMinutos?.toString() ?: "45") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (service == null) "Nuevo Servicio" else "Editar Servicio", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Servicio") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarberGoldPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarberGoldPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Precio en Pesos COP") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarberGoldPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duración (minutos)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BarberGoldPrimary,
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
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 30000.0
                    val duration = durationText.toIntOrNull() ?: 30
                    onSave(service?.id, localId, service?.idBarbero, name, description, price, duration)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary)
            ) {
                Text("Guardar", color = TextDark, fontWeight = FontWeight.Bold)
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
fun BarbershopEditorModal(
    shop: BarbershopEntity,
    onDismiss: () -> Unit,
    onSave: (nombre: String, direccion: String, ciudad: String, telefono: String, horarios: String) -> Unit
) {
    var name by remember { mutableStateOf(shop.nombre) }
    var address by remember { mutableStateOf(shop.direccion) }
    var city by remember { mutableStateOf(shop.ciudad) }
    var phone by remember { mutableStateOf(shop.telefono) }
    var hours by remember { mutableStateOf(shop.horarios) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Editar Datos del Local", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Barbería") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BarberGoldPrimary, unfocusedBorderColor = SlateBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BarberGoldPrimary, unfocusedBorderColor = SlateBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BarberGoldPrimary, unfocusedBorderColor = SlateBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono de Contacto") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BarberGoldPrimary, unfocusedBorderColor = SlateBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Horarios de Atención") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BarberGoldPrimary, unfocusedBorderColor = SlateBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, address, city, phone, hours) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BarberGoldPrimary)
            ) {
                Text("Guardar Cambios", color = TextDark, fontWeight = FontWeight.Bold)
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
