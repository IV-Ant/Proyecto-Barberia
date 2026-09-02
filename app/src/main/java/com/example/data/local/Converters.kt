package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AppointmentStatus
import com.example.data.model.BarberModality
import com.example.data.model.PaymentMethod
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CLIENTE)

    @TypeConverter
    fun fromBarberModality(value: BarberModality?): String? = value?.name

    @TypeConverter
    fun toBarberModality(value: String?): BarberModality? = value?.let {
        runCatching { BarberModality.valueOf(it) }.getOrNull()
    }

    @TypeConverter
    fun fromAppointmentStatus(value: AppointmentStatus): String = value.name

    @TypeConverter
    fun toAppointmentStatus(value: String): AppointmentStatus = runCatching {
        AppointmentStatus.valueOf(value)
    }.getOrDefault(AppointmentStatus.PENDIENTE)

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = runCatching {
        PaymentMethod.valueOf(value)
    }.getOrDefault(PaymentMethod.EFECTIVO)
}
