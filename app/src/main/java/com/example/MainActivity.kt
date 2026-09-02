package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.IntegrationInstructions
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserRole
import com.example.ui.components.NotificationBanner
import com.example.ui.components.RoleSwitcherHeader
import com.example.ui.screens.ArchitectureDocsScreen
import com.example.ui.screens.BarberScreen
import com.example.ui.screens.ClientScreen
import com.example.ui.screens.OwnerScreen
import com.example.ui.theme.BarberGoldLight
import com.example.ui.theme.BarberGoldPrimary
import com.example.ui.theme.BarberHubTheme
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateDarkBackground
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BarberViewModel
import com.example.ui.viewmodel.PushNotificationEvent
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: BarberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarberHubTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(viewModel: BarberViewModel) {
    var mainViewMode by remember { mutableIntStateOf(0) } // 0: App Interactiva por Roles, 1: Arquitectura & SQL 3FN
    var currentNotification by remember { mutableStateOf<PushNotificationEvent?>(null) }

    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val barbers by viewModel.barbers.collectAsStateWithLifecycle()
    val barbershops by viewModel.barbershops.collectAsStateWithLifecycle()
    val services by viewModel.services.collectAsStateWithLifecycle()
    val roleAppointments by viewModel.roleAppointments.collectAsStateWithLifecycle()
    val allAppointments by viewModel.appointments.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val selectedBarberId by viewModel.selectedBarberId.collectAsStateWithLifecycle()
    val barberAvailability by viewModel.barberAvailability.collectAsStateWithLifecycle()

    // Listen for push notification events
    LaunchedEffect(Unit) {
        viewModel.notificationEvents.collect { event ->
            currentNotification = event
            delay(4500)
            if (currentNotification == event) {
                currentNotification = null
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SlateDarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BarberGoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Logo",
                                tint = TextDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BarberHub",
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Citas & Gestión Barberías (COP)",
                                color = BarberGoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Navigation Mode Bar: App Interactiva vs Arquitectura & SQL DDL
                TabRow(
                    selectedTabIndex = mainViewMode,
                    containerColor = SlateSurface,
                    contentColor = BarberGoldPrimary,
                    divider = {}
                ) {
                    Tab(
                        selected = mainViewMode == 0,
                        onClick = { mainViewMode = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("App Interactiva", fontWeight = if (mainViewMode == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                            }
                        }
                    )
                    Tab(
                        selected = mainViewMode == 1,
                        onClick = { mainViewMode = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.IntegrationInstructions, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Arquitectura & SQL 3FN", fontWeight = if (mainViewMode == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                            }
                        }
                    )
                }

                if (mainViewMode == 0) {
                    // Role Switcher
                    RoleSwitcherHeader(
                        currentRole = currentRole,
                        onRoleSelected = { viewModel.setRole(it) },
                        activeUserName = currentUser?.nombre ?: "Usuario Activo"
                    )

                    // Role Screen Container
                    when (currentRole) {
                        UserRole.CLIENTE -> ClientScreen(
                            barbershop = barbershops.firstOrNull(),
                            barbers = barbers,
                            services = services,
                            appointments = roleAppointments,
                            reviews = reviews,
                            onBookAppointment = { barber, local, srv, date, time, payMethod, hasProof ->
                                viewModel.bookAppointment(barber, local, srv, date, time, payMethod, hasProof)
                            },
                            onCancelAppointment = { id, reason ->
                                viewModel.cancelAppointment(id, reason)
                            },
                            onSubmitReview = { id, bId, rat, com ->
                                viewModel.submitReview(id, bId, rat, com)
                            }
                        )
                        UserRole.BARBERO -> BarberScreen(
                            barbers = barbers,
                            selectedBarberId = selectedBarberId,
                            onSelectBarber = { viewModel.setSelectedBarber(it) },
                            appointments = roleAppointments,
                            availability = barberAvailability,
                            reviews = reviews.filter { it.idBarbero == selectedBarberId },
                            onConfirmAppointment = { viewModel.confirmAppointment(it) },
                            onRejectAppointment = { viewModel.rejectAppointment(it) },
                            onCompleteAppointment = { viewModel.completeAppointment(it) },
                            onToggleModality = { bId, mod -> viewModel.updateBarberModality(bId, mod) },
                            onToggleAvailability = { day, isAvail, start, end -> viewModel.toggleAvailability(day, isAvail, start, end) }
                        )
                        UserRole.DUENO -> OwnerScreen(
                            barbershop = barbershops.firstOrNull(),
                            barbers = barbers,
                            services = services,
                            appointments = allAppointments,
                            onSaveService = { id, locId, bId, name, desc, price, dur ->
                                viewModel.saveService(id, locId, bId, name, desc, price, dur)
                            },
                            onDeleteService = { viewModel.deleteService(it) },
                            onUpdateBarbershop = { shop, name, addr, city, phone, hours ->
                                viewModel.updateBarbershop(shop, name, addr, city, phone, hours)
                            },
                            onToggleBarberModality = { bId, mod -> viewModel.updateBarberModality(bId, mod) }
                        )
                    }
                } else {
                    ArchitectureDocsScreen()
                }
            }

            // Notification Banner Overlay
            NotificationBanner(
                event = currentNotification,
                onDismiss = { currentNotification = null },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
