package com.example.ui.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.RestaurantRepository
import com.example.data.security.SecurityHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    val employees: StateFlow<List<UserEntity>> = restaurantRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEmployee(
        fullName: String,
        username: String,
        role: UserRole,
        pin: String,
        phone: String
    ) {
        viewModelScope.launch {
            val salt = SecurityHelper.generateSalt()
            val passwordHash = SecurityHelper.hashWithSalt("resto123", salt)
            val pinHash = SecurityHelper.hashPin(pin.trim())

            restaurantRepository.insertUser(
                UserEntity(
                    username = username.trim().lowercase(),
                    fullName = fullName.trim(),
                    role = role,
                    passwordHash = passwordHash,
                    salt = salt,
                    pinHash = pinHash,
                    phone = phone.trim(),
                    isActive = true
                )
            )
        }
    }

    fun toggleActive(user: UserEntity) {
        viewModelScope.launch {
            restaurantRepository.updateUser(user.copy(isActive = !user.isActive))
        }
    }

    fun deleteEmployee(id: Long) {
        viewModelScope.launch {
            restaurantRepository.deleteUser(id)
        }
    }

    class Factory(private val restaurantRepository: RestaurantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EmployeeViewModel(restaurantRepository) as T
        }
    }
}
