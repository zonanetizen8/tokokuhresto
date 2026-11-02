package com.example.data.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {
    private val localeId = Locale("id", "ID")
    private val numberFormat = NumberFormat.getNumberInstance(localeId)

    fun formatRupiah(amount: Long): String {
        return "Rp ${numberFormat.format(amount)}"
    }

    fun formatNumber(number: Long): String {
        return numberFormat.format(number)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", localeId)
        return sdf.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", localeId)
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", localeId)
        return sdf.format(Date(timestamp))
    }
}
