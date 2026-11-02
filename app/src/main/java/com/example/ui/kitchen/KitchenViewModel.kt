package com.example.ui.kitchen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.KitchenItemStatus
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderWithItems
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KitchenViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _statusFilter = MutableStateFlow<OrderStatus?>(null)
    val statusFilter: StateFlow<OrderStatus?> = _statusFilter.asStateFlow()

    val kitchenOrders: StateFlow<List<OrderWithItems>> = combine(
        restaurantRepository.getActiveKitchenOrders(),
        _statusFilter
    ) { orders, filter ->
        if (filter == null) orders else orders.filter { it.order.status == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(status: OrderStatus?) {
        _statusFilter.value = status
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            restaurantRepository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun updateItemStatus(itemId: Long, newStatus: KitchenItemStatus) {
        viewModelScope.launch {
            restaurantRepository.updateOrderItemStatus(itemId, newStatus)
        }
    }

    class Factory(private val restaurantRepository: RestaurantRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return KitchenViewModel(restaurantRepository) as T
        }
    }
}
