package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_profile")
data class RestaurantProfileEntity(
    @PrimaryKey
    val id: Long = 1L,
    val name: String = "",
    val brandTagline: String = "",
    val address: String = "",
    val phone: String = "",
    val receiptHeader: String = "",
    val receiptFooter: String = "Terima kasih atas kunjungan Anda!",
    val defaultTaxPercent: Double = 0.0,
    val defaultServicePercent: Double = 0.0,
    val isAutoDeductStock: Boolean = true,
    val paperWidthMm: Int = 58, // 58mm or 80mm
    val logoPath: String? = null,
    val isDemoMode: Boolean = false,
    val autoLockMinutes: Int = 0 // 0 = off
)
