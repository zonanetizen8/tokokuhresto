package com.example.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.IngredientEntity
import com.example.data.local.entities.MovementType
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _onlyLowStock = MutableStateFlow(false)
    val onlyLowStock: StateFlow<Boolean> = _onlyLowStock.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val ingredients: StateFlow<List<IngredientEntity>> = combine(
        restaurantRepository.getAllIngredients(),
        _onlyLowStock,
        _searchQuery
    ) { allIng, lowOnly, query ->
        allIng.filter { ing ->
            val matchLow = !lowOnly || ing.isLowStock
            val matchQuery = query.isBlank() || ing.name.contains(query, ignoreCase = true) || ing.code.contains(query, ignoreCase = true)
            matchLow && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockCount: StateFlow<Int> = restaurantRepository.getAllIngredients().combine(_onlyLowStock) { all, _ ->
        all.count { it.isLowStock }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSearch(q: String) { _searchQuery.value = q }
    fun setLowOnly(v: Boolean) { _onlyLowStock.value = v }

    fun addIngredient(name: String, code: String, stock: Double, unit: String, buyPrice: Long, minStock: Double) {
        viewModelScope.launch {
            val autoCode = if (code.isBlank()) "BHN-${System.currentTimeMillis() % 1000}" else code
            restaurantRepository.insertIngredient(
                IngredientEntity(
                    code = autoCode,
                    name = name.trim(),
                    stock = stock,
                    unit = unit.ifBlank { "Kg" },
                    buyPrice = buyPrice,
                    minStock = minStock
                )
            )
        }
    }

    fun adjustStock(id: Long, qty: Double, type: MovementType, note: String) {
        viewModelScope.launch {
            restaurantRepository.adjustStock(id, qty, type, note, "Kasir")
        }
    }

    fun deleteIngredient(id: Long) {
        viewModelScope.launch {
            restaurantRepository.deleteIngredient(id)
        }
    }

    class Factory(private val restaurantRepository: RestaurantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return InventoryViewModel(restaurantRepository) as T
        }
    }
}
