package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppointmentEntity
import com.example.data.local.AvailabilityEntity
import com.example.data.local.BarberDatabase
import com.example.data.local.BarbershopEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.ServiceEntity
import com.example.data.local.UserEntity
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.PaymentMethod
import com.example.data.model.UserRole
import com.example.data.repository.BarberRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PushNotificationEvent(
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class BarberViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BarberDatabase.getDatabase(application, viewModelScope)
    private val repository = BarberRepository(database.barberDao())

    // Active Role & User Selection
    private val _currentRole = MutableStateFlow(UserRole.CLIENTE)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _selectedBarberId = MutableStateFlow("u_barber_1")
    val selectedBarberId: StateFlow<String> = _selectedBarberId.asStateFlow()

    // Notification Event Flow
    private val _notificationEvents = MutableSharedFlow<PushNotificationEvent>()
    val notificationEvents: SharedFlow<PushNotificationEvent> = _notificationEvents.asSharedFlow()

    // Data streams from repository
    val users: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val barbers: StateFlow<List<UserEntity>> = repository.getBarbers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val barbershops: StateFlow<List<BarbershopEntity>> = repository.allBarbershops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<ServiceEntity>> = repository.allServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reviews: StateFlow<List<ReviewEntity>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current logged-in user derived from role
    val currentUser: StateFlow<UserEntity?> = combine(users, _currentRole, _selectedBarberId) { userList, role, barberId ->
        when (role) {
            UserRole.CLIENTE -> userList.firstOrNull { it.rol == UserRole.CLIENTE }
            UserRole.BARBERO -> userList.firstOrNull { it.id == barberId } ?: userList.firstOrNull { it.rol == UserRole.BARBERO }
            UserRole.DUENO -> userList.firstOrNull { it.rol == UserRole.DUENO }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Filtered appointments for current role
    val roleAppointments: StateFlow<List<AppointmentEntity>> = combine(
        appointments,
        _currentRole,
        _selectedBarberId,
        currentUser
    ) { allApps, role, barberId, user ->
        when (role) {
            UserRole.CLIENTE -> allApps.filter { it.idCliente == (user?.id ?: "u_client_1") }
            UserRole.BARBERO -> allApps.filter { it.idBarbero == barberId }
            UserRole.DUENO -> allApps // Owner views all appointments of the barbershop
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Availability for active barber
    private val _barberAvailability = MutableStateFlow<List<AvailabilityEntity>>(emptyList())
    val barberAvailability: StateFlow<List<AvailabilityEntity>> = _barberAvailability.asStateFlow()

    init {
        viewModelScope.launch {
            _selectedBarberId.collect { barberId ->
                repository.getAvailability(barberId).collect { list ->
                    _barberAvailability.value = list
                }
            }
        }
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun setSelectedBarber(barberId: String) {
        _selectedBarberId.value = barberId
    }

    fun bookAppointment(
        barber: UserEntity,
        local: BarbershopEntity,
        service: ServiceEntity,
        date: String,
        time: String,
        paymentMethod: PaymentMethod,
        hasProof: Boolean
    ) {
        viewModelScope.launch {
            val client = currentUser.value ?: users.value.firstOrNull { it.rol == UserRole.CLIENTE } ?: return@launch
            val proofUrl = if (hasProof && paymentMethod == PaymentMethod.TRANSFERENCIA) {
                "https://storage.googleapis.com/barberhub-proofs/transf_${System.currentTimeMillis()}.jpg"
            } else null

            repository.createAppointment(
                client = client,
                barber = barber,
                local = local,
                service = service,
                date = date,
                time = time,
                paymentMethod = paymentMethod,
                proofUrl = proofUrl
            )

            // Trigger simulated push notification
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "💈 ¡Cita Solicitada con Éxito!",
                    message = "Tu cita para ${service.nombre} el $date a las $time ha sido enviada a ${barber.nombre}."
                )
            )
        }
    }

    fun confirmAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointment.id, AppointmentStatus.CONFIRMADA)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "✅ Cita Confirmada",
                    message = "Has confirmado la cita con ${appointment.clienteNombre} para el ${appointment.fecha} a las ${appointment.hora}."
                )
            )
        }
    }

    fun rejectAppointment(appointment: AppointmentEntity, reason: String = "Barbero no disponible en el horario") {
        viewModelScope.launch {
            repository.cancelAppointment(appointment.id, reason)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "❌ Cita Rechazada",
                    message = "La cita con ${appointment.clienteNombre} fue rechazada."
                )
            )
        }
    }

    fun completeAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointment.id, AppointmentStatus.COMPLETADA)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "🎉 Servicio Completado & Pago Verificado",
                    message = "Cita de ${appointment.servicioNombre} marcada como completada. El cliente ahora puede calificar el servicio."
                )
            )
        }
    }

    fun cancelAppointment(appointmentId: String, reason: String) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId, reason)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "⚠️ Cita Cancelada",
                    message = "La cita fue cancelada exitosamente con más de 1 hora de anticipación."
                )
            )
        }
    }

    fun submitReview(appointmentId: String, barberId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val client = currentUser.value ?: return@launch
            val result = repository.addReview(
                appointmentId = appointmentId,
                barberId = barberId,
                clientId = client.id,
                clientName = client.nombre,
                rating = rating,
                comment = comment,
                date = "2026-09-01"
            )
            if (result.isSuccess) {
                _notificationEvents.emit(
                    PushNotificationEvent(
                        title = "⭐ ¡Reseña Verificada Publicada!",
                        message = "Gracias por tu calificación de $rating estrellas."
                    )
                )
            }
        }
    }

    fun updateBarberModality(barberId: String, modality: BarberModality) {
        viewModelScope.launch {
            repository.updateBarberModality(barberId, modality)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "👔 Modalidad Actualizada",
                    message = "Modalidad cambiada a ${modality.label}."
                )
            )
        }
    }

    fun saveService(
        id: String?,
        localId: String,
        barberId: String?,
        nombre: String,
        descripcion: String,
        costoCop: Double,
        duracionMinutos: Int
    ) {
        viewModelScope.launch {
            repository.saveService(id, localId, barberId, nombre, descripcion, costoCop, duracionMinutos)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "✂️ Servicio Guardado",
                    message = "$nombre actualizado en el catálogo por $$costoCop COP."
                )
            )
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            repository.deleteService(serviceId)
        }
    }

    fun updateBarbershop(local: BarbershopEntity, nombre: String, direccion: String, ciudad: String, telefono: String, horarios: String) {
        viewModelScope.launch {
            repository.updateBarbershopDetails(local, nombre, direccion, ciudad, telefono, horarios)
            _notificationEvents.emit(
                PushNotificationEvent(
                    title = "🏢 Datos del Local Actualizados",
                    message = "La información de $nombre ha sido guardada."
                )
            )
        }
    }

    fun toggleAvailability(dayIndex: Int, isAvailable: Boolean, start: String, end: String) {
        viewModelScope.launch {
            val barberId = selectedBarberId.value
            val currentList = _barberAvailability.value.toMutableList()
            val existing = currentList.firstOrNull { it.diaSemana == dayIndex }
            if (existing != null) {
                repository.updateAvailabilityDay(
                    existing.copy(disponible = isAvailable, horaInicio = start, horaFin = end)
                )
            } else {
                repository.saveAvailabilityList(
                    listOf(
                        AvailabilityEntity(
                            idBarbero = barberId,
                            diaSemana = dayIndex,
                            horaInicio = start,
                            horaFin = end,
                            disponible = isAvailable
                        )
                    )
                )
            }
        }
    }
}
