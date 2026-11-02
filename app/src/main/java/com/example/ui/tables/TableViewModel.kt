package com.example.ui.tables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.TableEntity
import com.example.data.local.entities.TableStatus
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TableViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _statusFilter = MutableStateFlow<TableStatus?>(null)
    val statusFilter: StateFlow<TableStatus?> = _statusFilter.asStateFlow()

    val tables: StateFlow<List<TableEntity>> = combine(
        restaurantRepository.getAllTables(),
        _statusFilter
    ) { allTables, filter ->
        if (filter == null) allTables else allTables.filter { it.status == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTablesRaw: StateFlow<List<TableEntity>> = restaurantRepository.getAllTables()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(status: TableStatus?) {
        _statusFilter.value = status
    }

    fun addTable(number: String, capacity: Int) {
        viewModelScope.launch {
            val formatted = if (number.startsWith("Meja", ignoreCase = true)) number else "Meja $number"
            restaurantRepository.insertTable(
                TableEntity(tableNumber = formatted, capacity = capacity, status = TableStatus.VACANT)
            )
        }
    }

    fun updateTable(table: TableEntity) {
        viewModelScope.launch {
            restaurantRepository.updateTable(table)
        }
    }

    fun deleteTable(id: Long) {
        viewModelScope.launch {
            restaurantRepository.deleteTable(id)
        }
    }

    fun updateStatus(tableId: Long, status: TableStatus) {
        viewModelScope.launch {
            restaurantRepository.updateTableStatus(tableId, status, null)
        }
    }

    fun clearTable(tableId: Long) {
        viewModelScope.launch {
            restaurantRepository.clearTable(tableId)
        }
    }

    fun moveTable(fromId: Long, toId: Long) {
        viewModelScope.launch {
            restaurantRepository.moveTable(fromId, toId)
        }
    }

    class Factory(private val restaurantRepository: RestaurantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TableViewModel(restaurantRepository) as T
        }
    }
}
