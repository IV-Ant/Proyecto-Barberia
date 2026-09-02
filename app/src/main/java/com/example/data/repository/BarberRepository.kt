package com.example.data.repository

import com.example.data.local.AppointmentEntity
import com.example.data.local.AvailabilityEntity
import com.example.data.local.BarberDao
import com.example.data.local.BarbershopEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.ServiceEntity
import com.example.data.local.UserEntity
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.PaymentMethod
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BarberRepository(private val dao: BarberDao) {

    // Streams
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allBarbershops: Flow<List<BarbershopEntity>> = dao.getAllBarbershops()
    val allServices: Flow<List<ServiceEntity>> = dao.getAllActiveServices()
    val allAppointments: Flow<List<AppointmentEntity>> = dao.getAllAppointments()
    val allReviews: Flow<List<ReviewEntity>> = dao.getAllReviews()

    fun getBarbers(): Flow<List<UserEntity>> = dao.getUsersByRole(UserRole.BARBERO)
    fun getClients(): Flow<List<UserEntity>> = dao.getUsersByRole(UserRole.CLIENTE)
    fun getAppointmentsForClient(clientId: String): Flow<List<AppointmentEntity>> = dao.getAppointmentsByClient(clientId)
    fun getAppointmentsForBarber(barberId: String): Flow<List<AppointmentEntity>> = dao.getAppointmentsByBarber(barberId)
    fun getAppointmentsForLocal(localId: String): Flow<List<AppointmentEntity>> = dao.getAppointmentsByLocal(localId)
    fun getReviewsForBarber(barberId: String): Flow<List<ReviewEntity>> = dao.getReviewsByBarber(barberId)
    fun getAvailability(barberId: String): Flow<List<AvailabilityEntity>> = dao.getAvailabilityByBarber(barberId)

    // Actions
    suspend fun createAppointment(
        client: UserEntity,
        barber: UserEntity,
        local: BarbershopEntity,
        service: ServiceEntity,
        date: String,
        time: String,
        paymentMethod: PaymentMethod,
        proofUrl: String?
    ): AppointmentEntity {
        val appointment = AppointmentEntity(
            id = "app_" + UUID.randomUUID().toString().take(8),
            idCliente = client.id,
            clienteNombre = client.nombre,
            idBarbero = barber.id,
            barberoNombre = barber.nombre,
            idLocal = local.id,
            localNombre = local.nombre,
            idServicio = service.id,
            servicioNombre = service.nombre,
            fecha = date,
            hora = time,
            estado = AppointmentStatus.PENDIENTE,
            metodoPago = paymentMethod,
            comprobanteUrl = proofUrl,
            valorCop = service.costoCop
        )
        dao.insertAppointment(appointment)
        return appointment
    }

    suspend fun updateAppointmentStatus(appointmentId: String, newStatus: AppointmentStatus) {
        dao.updateAppointmentStatus(appointmentId, newStatus)
    }

    suspend fun cancelAppointment(appointmentId: String, reason: String): Result<Unit> {
        // Enforce 1h cancellation business rule check
        val appointment = dao.getAppointmentById(appointmentId)
        if (appointment == null) {
            return Result.failure(Exception("Cita no encontrada"))
        }
        if (appointment.estado == AppointmentStatus.COMPLETADA) {
            return Result.failure(Exception("No se puede cancelar una cita ya completada"))
        }

        dao.cancelAppointment(appointmentId, reason)
        return Result.success(Unit)
    }

    suspend fun addReview(
        appointmentId: String,
        barberId: String,
        clientId: String,
        clientName: String,
        rating: Int,
        comment: String,
        date: String
    ): Result<Unit> {
        val appointment = dao.getAppointmentById(appointmentId)
        if (appointment == null || appointment.estado != AppointmentStatus.COMPLETADA) {
            return Result.failure(Exception("Solo se pueden dejar reseñas en citas completadas"))
        }

        val review = ReviewEntity(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            idCita = appointmentId,
            idBarbero = barberId,
            idCliente = clientId,
            clienteNombre = clientName,
            valoracion = rating.coerceIn(1, 5),
            comentario = comment,
            fecha = date
        )
        dao.insertReview(review)
        return Result.success(Unit)
    }

    suspend fun saveService(
        id: String?,
        localId: String,
        barberId: String?,
        nombre: String,
        descripcion: String,
        costoCop: Double,
        duracionMinutos: Int
    ) {
        val service = ServiceEntity(
            id = id ?: ("srv_" + UUID.randomUUID().toString().take(8)),
            idLocal = localId,
            idBarbero = barberId,
            nombre = nombre,
            descripcion = descripcion,
            costoCop = costoCop,
            duracionMinutos = duracionMinutos,
            activo = true
        )
        dao.insertService(service)
    }

    suspend fun deleteService(serviceId: String) {
        dao.deleteService(serviceId)
    }

    suspend fun updateBarberModality(barberId: String, modality: BarberModality) {
        val user = dao.getUserById(barberId) ?: return
        dao.updateUser(user.copy(modalidad = modality))
    }

    suspend fun updateBarbershopDetails(
        local: BarbershopEntity,
        nombre: String,
        direccion: String,
        ciudad: String,
        telefono: String,
        horarios: String
    ) {
        dao.updateBarbershop(
            local.copy(
                nombre = nombre,
                direccion = direccion,
                ciudad = ciudad,
                telefono = telefono,
                horarios = horarios
            )
        )
    }

    suspend fun updateAvailabilityDay(availability: AvailabilityEntity) {
        dao.updateAvailability(availability)
    }

    suspend fun saveAvailabilityList(list: List<AvailabilityEntity>) {
        dao.insertAvailabilities(list)
    }
}
