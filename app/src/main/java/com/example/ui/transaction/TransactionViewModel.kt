package com.example.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderWithItems
import com.example.data.local.entities.PaymentStatus
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TransactionFilter {
    ALL,
    PAID,
    CANCELLED
}

class TransactionViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(TransactionFilter.ALL)
    val filter: StateFlow<TransactionFilter> = _filter.asStateFlow()

    val transactions: StateFlow<List<OrderWithItems>> = combine(
        restaurantRepository.getAllOrders(),
        _searchQuery,
        _filter
    ) { orders, query, filterStatus ->
        orders.filter { item ->
            val matchFilter = when (filterStatus) {
                TransactionFilter.ALL -> true
                TransactionFilter.PAID -> item.order.status == OrderStatus.COMPLETED && item.order.paymentStatus == PaymentStatus.PAID
                TransactionFilter.CANCELLED -> item.order.status == OrderStatus.CANCELLED
            }
            val matchQuery = query.isBlank() ||
                item.order.orderNumber.contains(query, ignoreCase = true) ||
                item.order.customerName.contains(query, ignoreCase = true) ||
                (item.order.cashierName?.contains(query, ignoreCase = true) ?: false)

            matchFilter && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedOrder = MutableStateFlow<OrderWithItems?>(null)
    val selectedOrder: StateFlow<OrderWithItems?> = _selectedOrder.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: TransactionFilter) {
        _filter.value = filter
    }

    fun selectOrder(order: OrderWithItems?) {
        _selectedOrder.value = order
    }

    fun cancelOrder(orderId: Long, reason: String) {
        viewModelScope.launch {
            try {
                restaurantRepository.cancelOrder(orderId, reason)
                _feedbackMessage.value = "Transaksi #$orderId berhasil dibatalkan."
                _selectedOrder.value = null
            } catch (e: Exception) {
                _feedbackMessage.value = "Gagal membatalkan transaksi: ${e.message}"
            }
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    class Factory(
        private val restaurantRepository: RestaurantRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TransactionViewModel(restaurantRepository) as T
        }
    }
}
