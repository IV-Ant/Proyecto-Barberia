package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.PaymentMethod
import com.example.data.model.UserRole

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val telefono: String,
    val rol: UserRole,
    val fotoUrl: String,
    val modalidad: BarberModality? = null,
    val fechaCreacion: String
)

@Entity(
    tableName = "locales",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idDueno"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idDueno"])]
)
data class BarbershopEntity(
    @PrimaryKey val id: String,
    val idDueno: String,
    val nombre: String,
    val direccion: String,
    val ciudad: String,
    val telefono: String,
    val horarios: String,
    val logoUrl: String,
    val qrUrl: String
)

@Entity(
    tableName = "servicios",
    foreignKeys = [
        ForeignKey(
            entity = BarbershopEntity::class,
            parentColumns = ["id"],
            childColumns = ["idLocal"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idLocal"])]
)
data class ServiceEntity(
    @PrimaryKey val id: String,
    val idLocal: String,
    val idBarbero: String? = null,
    val nombre: String,
    val descripcion: String,
    val costoCop: Double,
    val duracionMinutos: Int,
    val activo: Boolean = true
)

@Entity(
    tableName = "disponibilidad_barberos",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idBarbero"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["idBarbero"])]
)
data class AvailabilityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idBarbero: String,
    val diaSemana: Int, // 0 = Lunes, 1 = Martes ... 6 = Domingo
    val horaInicio: String,
    val horaFin: String,
    val disponible: Boolean = true
)

@Entity(
    tableName = "citas",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idCliente"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idBarbero"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["idServicio"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BarbershopEntity::class,
            parentColumns = ["id"],
            childColumns = ["idLocal"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idCliente"]),
        Index(value = ["idBarbero"]),
        Index(value = ["idServicio"]),
        Index(value = ["idLocal"])
    ]
)
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val idCliente: String,
    val clienteNombre: String,
    val idBarbero: String,
    val barberoNombre: String,
    val idLocal: String,
    val localNombre: String,
    val idServicio: String,
    val servicioNombre: String,
    val fecha: String, // YYYY-MM-DD
    val hora: String,  // HH:MM
    val estado: AppointmentStatus,
    val metodoPago: PaymentMethod,
    val comprobanteUrl: String? = null,
    val valorCop: Double,
    val motivoCancelacion: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "resenas",
    foreignKeys = [
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["idCita"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idBarbero"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["idCliente"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idCita"], unique = true),
        Index(value = ["idBarbero"]),
        Index(value = ["idCliente"])
    ]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val idCita: String,
    val idBarbero: String,
    val idCliente: String,
    val clienteNombre: String,
    val valoracion: Int, // 1 to 5
    val comentario: String,
    val fecha: String
)
