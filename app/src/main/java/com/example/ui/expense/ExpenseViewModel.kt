package com.example.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ExpenseEntity
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val expenses: StateFlow<List<ExpenseEntity>> = combine(
        restaurantRepository.getAllExpenses(),
        _selectedCategory
    ) { allExpenses, cat ->
        if (cat == null) allExpenses else allExpenses.filter { it.category == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalExpense: StateFlow<Long> = restaurantRepository.getAllExpenses().combine(_selectedCategory) { all, _ ->
        all.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun addExpense(category: String, amount: Long, note: String) {
        if (amount <= 0L) return
        viewModelScope.launch {
            try {
                restaurantRepository.addExpense(
                    ExpenseEntity(
                        category = category,
                        amount = amount,
                        note = note.trim(),
                        date = System.currentTimeMillis()
                    )
                )
                _message.value = "Pengeluaran Rp $amount berhasil dicatat."
            } catch (e: Exception) {
                _message.value = "Gagal mencatat pengeluaran: ${e.message}"
            }
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            try {
                restaurantRepository.deleteExpense(id)
                _message.value = "Pengeluaran berhasil dihapus."
            } catch (e: Exception) {
                _message.value = "Gagal menghapus pengeluaran: ${e.message}"
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
            return ExpenseViewModel(restaurantRepository) as T
        }
    }
}
