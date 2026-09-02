package com.example.data.model

enum class UserRole(val label: String, val description: String) {
    CLIENTE("Cliente", "Agenda citas, visualiza su historial y califica servicios"),
    BARBERO("Barbero", "Gestiona agenda, confirma citas y valida pagos"),
    DUENO("Dueño / Administrador", "Administra locales, servicios, equipo y reportes")
}

enum class BarberModality(val label: String, val description: String) {
    INDEPENDIENTE("Independiente", "Define sus propios servicios y tarifas"),
    EMPLEADO("Empleado", "Servicios y precios estandarizados por el local")
}

enum class AppointmentStatus(val label: String, val colorHex: Long) {
    PENDIENTE("Pendiente", 0xFFF59E0B),       // Amber
    CONFIRMADA("Confirmada", 0xFF3B82F6),     // Blue
    CANCELADA("Cancelada", 0xFFEF4444),       // Red
    COMPLETADA("Completada", 0xFF10B981)      // Green
}

enum class PaymentMethod(val label: String, val iconName: String) {
    EFECTIVO("Efectivo en Local", "payments"),
    TRANSFERENCIA("Transferencia Bancaria", "account_balance")
}
