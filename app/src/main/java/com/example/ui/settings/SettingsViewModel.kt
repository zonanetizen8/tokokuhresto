package com.example.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    val profile: StateFlow<RestaurantProfileEntity?> = restaurantRepository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun updateProfile(
        name: String,
        slogan: String,
        address: String,
        phone: String,
        receiptHeader: String,
        receiptFooter: String,
        taxPercent: Double,
        servicePercent: Double,
        printerWidthMm: Int
    ) {
        val current = profile.value ?: RestaurantProfileEntity(
            name = name,
            brandTagline = slogan,
            address = address,
            phone = phone
        )

        viewModelScope.launch {
            try {
                val updated = current.copy(
                    name = name.trim(),
                    brandTagline = slogan.trim(),
                    address = address.trim(),
                    phone = phone.trim(),
                    receiptHeader = receiptHeader.trim(),
                    receiptFooter = receiptFooter.trim(),
                    defaultTaxPercent = taxPercent,
                    defaultServicePercent = servicePercent,
                    paperWidthMm = printerWidthMm,
                    logoPath = current.logoPath
                )
                restaurantRepository.updateProfile(updated)
                _message.value = "Pengaturan restoran & struk berhasil disimpan."
            } catch (e: Exception) {
                _message.value = "Gagal menyimpan pengaturan: ${e.message}"
            }
        }
    }

    fun updateLogo(logoPath: String?) {
        val current = profile.value ?: RestaurantProfileEntity()
        viewModelScope.launch {
            try {
                val updated = current.copy(logoPath = logoPath)
                restaurantRepository.updateProfile(updated)
                _message.value = if (logoPath != null) "Logo restoran berhasil diperbarui & disimpan." else "Logo restoran berhasil dihapus."
            } catch (e: Exception) {
                _message.value = "Gagal memperbarui logo: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    class Factory(
        private val restaurantRepository: RestaurantRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(restaurantRepository) as T
        }
    }
}
