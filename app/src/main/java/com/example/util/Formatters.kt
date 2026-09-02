package com.example.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    fun formatCop(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        format.maximumFractionDigits = 0
        return format.format(amount) + " COP"
    }

    fun getDayOfWeekName(dayIndex: Int): String {
        return when (dayIndex) {
            0 -> "Lunes"
            1 -> "Martes"
            2 -> "Miércoles"
            3 -> "Jueves"
            4 -> "Viernes"
            5 -> "Sábado"
            6 -> "Domingo"
            else -> "Día $dayIndex"
        }
    }
}
