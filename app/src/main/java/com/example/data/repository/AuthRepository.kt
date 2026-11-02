package com.example.data.repository

import com.example.data.local.dao.UserDao
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.security.SecurityHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(private val userDao: UserDao) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun isOwnerRegistered(): Boolean {
        return userDao.getOwner() != null
    }

    suspend fun loginWithPassword(username: String, password: String): Result<UserEntity> {
        val user = userDao.getUserByUsername(username.trim())
            ?: return Result.failure(Exception("Username tidak ditemukan."))

        if (!user.isActive) {
            return Result.failure(Exception("Akun ini telah dinonaktifkan."))
        }

        val isValid = SecurityHelper.verifyHash(password, user.salt, user.passwordHash)
        return if (isValid) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Password yang Anda masukkan salah."))
        }
    }

    suspend fun loginWithPin(pin: String): Result<UserEntity> {
        val pinHash = SecurityHelper.hashPin(pin.trim())
        val user = userDao.getUserByPin(pinHash)
            ?: return Result.failure(Exception("PIN tidak cocok dengan akun aktif."))

        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun registerOwner(
        username: String,
        fullName: String,
        password: String,
        pin: String,
        securityQuestion: String,
        securityAnswer: String,
        phone: String
    ): Result<UserEntity> {
        val trimmedUsername = username.trim().lowercase()
        val existing = userDao.getUserByUsername(trimmedUsername)
        if (existing != null) {
            return Result.failure(Exception("Username '$trimmedUsername' sudah terpakai."))
        }

        val salt = SecurityHelper.generateSalt()
        val passwordHash = SecurityHelper.hashWithSalt(password, salt)
        val pinHash = SecurityHelper.hashPin(pin.trim())
        val answerHash = SecurityHelper.hashWithSalt(securityAnswer.trim().lowercase(), salt)

        val ownerUser = UserEntity(
            username = trimmedUsername,
            fullName = fullName.trim(),
            role = UserRole.OWNER,
            passwordHash = passwordHash,
            salt = salt,
            pinHash = pinHash,
            securityQuestion = securityQuestion,
            securityAnswerHash = answerHash,
            phone = phone.trim(),
            isActive = true
        )

        val id = userDao.insertUser(ownerUser)
        val saved = ownerUser.copy(id = id)
        _currentUser.value = saved
        return Result.success(saved)
    }

    suspend fun resetPinWithSecurityAnswer(
        username: String,
        securityAnswer: String,
        newPin: String
    ): Result<Boolean> {
        val user = userDao.getUserByUsername(username.trim())
            ?: return Result.failure(Exception("Pengguna tidak ditemukan."))

        val isAnswerCorrect = SecurityHelper.verifyHash(
            securityAnswer.trim().lowercase(),
            user.salt,
            user.securityAnswerHash
        )

        if (!isAnswerCorrect) {
            return Result.failure(Exception("Jawaban pemulihan keamanan salah."))
        }

        val newPinHash = SecurityHelper.hashPin(newPin.trim())
        userDao.updateUser(user.copy(pinHash = newPinHash))
        return Result.success(true)
    }

    suspend fun changePin(userId: Long, oldPin: String, newPin: String): Result<Boolean> {
        val current = _currentUser.value
        if (current == null || current.id != userId) {
            return Result.failure(Exception("Sesi tidak sah."))
        }
        val oldPinHash = SecurityHelper.hashPin(oldPin.trim())
        if (current.pinHash != oldPinHash) {
            return Result.failure(Exception("PIN lama tidak cocok."))
        }
        val newPinHash = SecurityHelper.hashPin(newPin.trim())
        val updated = current.copy(pinHash = newPinHash)
        userDao.updateUser(updated)
        _currentUser.value = updated
        return Result.success(true)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
    }
}
