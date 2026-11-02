package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class UserRole {
    OWNER,
    ADMIN,
    CASHIER,
    KITCHEN
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val fullName: String,
    val role: UserRole,
    val passwordHash: String,
    val salt: String,
    val pinHash: String, // 6-digit hashed PIN for fast cashier/kitchen switch
    val securityQuestion: String = "Nama restoran pertama Anda?",
    val securityAnswerHash: String = "",
    val phone: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
