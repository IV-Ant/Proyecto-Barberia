package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppointmentStatus
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface BarberDao {

    // --- USUARIOS ---
    @Query("SELECT * FROM usuarios")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE rol = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- LOCALES ---
    @Query("SELECT * FROM locales")
    fun getAllBarbershops(): Flow<List<BarbershopEntity>>

    @Query("SELECT * FROM locales WHERE idDueno = :ownerId LIMIT 1")
    fun getBarbershopByOwner(ownerId: String): Flow<BarbershopEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarbershop(barbershop: BarbershopEntity)

    @Update
    suspend fun updateBarbershop(barbershop: BarbershopEntity)

    // --- SERVICIOS ---
    @Query("SELECT * FROM servicios WHERE activo = 1")
    fun getAllActiveServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM servicios WHERE idLocal = :localId")
    fun getServicesByLocal(localId: String): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Query("DELETE FROM servicios WHERE id = :serviceId")
    suspend fun deleteService(serviceId: String)

    // --- DISPONIBILIDAD ---
    @Query("SELECT * FROM disponibilidad_barberos WHERE idBarbero = :barberId ORDER BY diaSemana ASC")
    fun getAvailabilityByBarber(barberId: String): Flow<List<AvailabilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailabilities(availabilities: List<AvailabilityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvailability(availability: AvailabilityEntity)

    @Update
    suspend fun updateAvailability(availability: AvailabilityEntity)

    // --- CITAS ---
    @Query("SELECT * FROM citas ORDER BY fecha DESC, hora DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM citas WHERE idCliente = :clientId ORDER BY fecha DESC, hora DESC")
    fun getAppointmentsByClient(clientId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM citas WHERE idBarbero = :barberId ORDER BY fecha DESC, hora DESC")
    fun getAppointmentsByBarber(barberId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM citas WHERE idLocal = :localId ORDER BY fecha DESC, hora DESC")
    fun getAppointmentsByLocal(localId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM citas WHERE id = :id LIMIT 1")
    suspend fun getAppointmentById(id: String): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Query("UPDATE citas SET estado = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: String, status: AppointmentStatus)

    @Query("UPDATE citas SET estado = 'CANCELADA', motivoCancelacion = :reason WHERE id = :id")
    suspend fun cancelAppointment(id: String, reason: String)

    @Query("UPDATE citas SET comprobanteUrl = :proofUrl WHERE id = :id")
    suspend fun updatePaymentProof(id: String, proofUrl: String)

    // --- RESEÑAS ---
    @Query("SELECT * FROM resenas WHERE idBarbero = :barberId ORDER BY fecha DESC")
    fun getReviewsByBarber(barberId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM resenas ORDER BY fecha DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}
