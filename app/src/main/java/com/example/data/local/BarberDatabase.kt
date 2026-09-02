package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.PaymentMethod
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        BarbershopEntity::class,
        ServiceEntity::class,
        AvailabilityEntity::class,
        AppointmentEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BarberDatabase : RoomDatabase() {

    abstract fun barberDao(): BarberDao

    companion object {
        @Volatile
        private var INSTANCE: BarberDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BarberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BarberDatabase::class.java,
                    "barber_hub_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(BarberDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class BarberDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.barberDao())
                }
            }
        }

        suspend fun populateInitialData(dao: BarberDao) {
            // Seed Users
            val owner = UserEntity(
                id = "u_owner_1",
                nombre = "Don Carlos Mendoza",
                email = "carlos.dueno@barberhub.co",
                telefono = "+57 310 456 7890",
                rol = UserRole.DUENO,
                fotoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                modalidad = null,
                fechaCreacion = "2026-01-10"
            )

            val barber1 = UserEntity(
                id = "u_barber_1",
                nombre = "Mateo 'Fade' Gómez",
                email = "mateo.fade@barberhub.co",
                telefono = "+57 320 123 4567",
                rol = UserRole.BARBERO,
                fotoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                modalidad = BarberModality.INDEPENDIENTE,
                fechaCreacion = "2026-01-15"
            )

            val barber2 = UserEntity(
                id = "u_barber_2",
                nombre = "Sebastián Castro",
                email = "sebastian.c@barberhub.co",
                telefono = "+57 315 987 6543",
                rol = UserRole.BARBERO,
                fotoUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                modalidad = BarberModality.EMPLEADO,
                fechaCreacion = "2026-02-01"
            )

            val client = UserEntity(
                id = "u_client_1",
                nombre = "Andrés Felipe Ospina",
                email = "andres.ospina@gmail.com",
                telefono = "+57 300 555 1234",
                rol = UserRole.CLIENTE,
                fotoUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150",
                modalidad = null,
                fechaCreacion = "2026-02-15"
            )

            dao.insertUsers(listOf(owner, barber1, barber2, client))

            // Seed Local
            val local = BarbershopEntity(
                id = "loc_1",
                idDueno = owner.id,
                nombre = "Barbería El Barón Clásico Bogotá",
                direccion = "Cra. 15 # 85-32, Zona Rosa, Bogotá",
                ciudad = "Bogotá, D.C.",
                telefono = "+57 (601) 745-8899",
                horarios = "Lun - Sáb: 8:00 AM - 8:00 PM | Dom: 10:00 AM - 4:00 PM",
                logoUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=150",
                qrUrl = "https://barberhub.co/qr/loc_1"
            )
            dao.insertBarbershop(local)

            // Seed Services in COP
            val services = listOf(
                ServiceEntity(
                    id = "srv_1",
                    idLocal = local.id,
                    idBarbero = null,
                    nombre = "Corte Clásico & Fade",
                    descripcion = "Corte con tijera o degradado a navaja, lavado y peinado con pomada mate.",
                    costoCop = 30000.0,
                    duracionMinutos = 40
                ),
                ServiceEntity(
                    id = "srv_2",
                    idLocal = local.id,
                    idBarbero = null,
                    nombre = "Perfilado de Barba & Toalla Caliente",
                    descripcion = "Diseño de barba, vaporizador de ozono, toalla caliente y aceites hidratantes.",
                    costoCop = 20000.0,
                    duracionMinutos = 30
                ),
                ServiceEntity(
                    id = "srv_3",
                    idLocal = local.id,
                    idBarbero = null,
                    nombre = "Combo Real (Corte + Barba + Mascarilla)",
                    descripcion = "Experiencia completa: corte premium, barba completa y mascarilla de carbón activado.",
                    costoCop = 48000.0,
                    duracionMinutos = 60
                ),
                ServiceEntity(
                    id = "srv_4",
                    idLocal = local.id,
                    idBarbero = barber1.id,
                    nombre = "Freestyle Hair Tattoo & Color",
                    descripcion = "Diseño artístico personalizado a navaja y aplicación de color temporal/permanente.",
                    costoCop = 45000.0,
                    duracionMinutos = 50
                )
            )
            dao.insertServices(services)

            // Seed Availability for Mateo (Barber 1) & Sebastian (Barber 2)
            val availabilities = mutableListOf<AvailabilityEntity>()
            for (day in 0..5) { // Lun a Sab
                availabilities.add(
                    AvailabilityEntity(
                        idBarbero = barber1.id,
                        diaSemana = day,
                        horaInicio = "08:00",
                        horaFin = "18:00",
                        disponible = true
                    )
                )
                availabilities.add(
                    AvailabilityEntity(
                        idBarbero = barber2.id,
                        diaSemana = day,
                        horaInicio = "09:00",
                        horaFin = "19:00",
                        disponible = true
                    )
                )
            }
            dao.insertAvailabilities(availabilities)

            // Seed Sample Appointments
            val app1 = AppointmentEntity(
                id = "app_101",
                idCliente = client.id,
                clienteNombre = client.nombre,
                idBarbero = barber1.id,
                barberoNombre = barber1.nombre,
                idLocal = local.id,
                localNombre = local.nombre,
                idServicio = "srv_3",
                servicioNombre = "Combo Real (Corte + Barba + Mascarilla)",
                fecha = "2026-09-02",
                hora = "10:00 AM",
                estado = AppointmentStatus.CONFIRMADA,
                metodoPago = PaymentMethod.TRANSFERENCIA,
                comprobanteUrl = "https://storage.googleapis.com/barberhub-proofs/nequi_ref_48291.jpg",
                valorCop = 48000.0
            )

            val app2 = AppointmentEntity(
                id = "app_102",
                idCliente = client.id,
                clienteNombre = client.nombre,
                idBarbero = barber2.id,
                barberoNombre = barber2.nombre,
                idLocal = local.id,
                localNombre = local.nombre,
                idServicio = "srv_1",
                servicioNombre = "Corte Clásico & Fade",
                fecha = "2026-08-28",
                hora = "03:30 PM",
                estado = AppointmentStatus.COMPLETADA,
                metodoPago = PaymentMethod.EFECTIVO,
                comprobanteUrl = null,
                valorCop = 30000.0
            )

            val app3 = AppointmentEntity(
                id = "app_103",
                idCliente = client.id,
                clienteNombre = client.nombre,
                idBarbero = barber1.id,
                barberoNombre = barber1.nombre,
                idLocal = local.id,
                localNombre = local.nombre,
                idServicio = "srv_2",
                servicioNombre = "Perfilado de Barba & Toalla Caliente",
                fecha = "2026-09-03",
                hora = "04:00 PM",
                estado = AppointmentStatus.PENDIENTE,
                metodoPago = PaymentMethod.EFECTIVO,
                comprobanteUrl = null,
                valorCop = 20000.0
            )

            dao.insertAppointments(listOf(app1, app2, app3))

            // Seed Verified Review for Completed Appointment
            val review1 = ReviewEntity(
                id = "rev_1",
                idCita = app2.id,
                idBarbero = barber2.id,
                idCliente = client.id,
                clienteNombre = client.nombre,
                valoracion = 5,
                comentario = "¡Excelente servicio! Sebastián es muy detallista con el fade a navaja y el trato fue de primera. Recomendado 100%.",
                fecha = "2026-08-28"
            )
            dao.insertReview(review1)
        }
    }
}
