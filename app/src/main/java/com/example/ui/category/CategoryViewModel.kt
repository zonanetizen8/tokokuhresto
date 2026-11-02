package com.example.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.CategoryEntity
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = restaurantRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun addCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            restaurantRepository.insertCategory(
                CategoryEntity(name = name.trim())
            )
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            restaurantRepository.updateCategory(category)
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            val result = restaurantRepository.deleteCategory(id)
            result.onFailure {
                _errorMessage.value = it.message ?: "Gagal menghapus kategori"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    class Factory(private val restaurantRepository: RestaurantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryViewModel(restaurantRepository) as T
        }
    }
}
