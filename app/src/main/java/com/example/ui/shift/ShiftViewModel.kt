package com.example.ui.shift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.OrderWithItems
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.ShiftEntity
import com.example.data.local.entities.ShiftStatus
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ShiftSummaryData(
    val activeShift: ShiftEntity?,
    val cashSales: Long = 0L,
    val cashExpenses: Long = 0L,
    val expectedCash: Long = 0L
)

class ShiftViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    val activeShift: StateFlow<ShiftEntity?> = restaurantRepository.getActiveShift()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allShifts: StateFlow<List<ShiftEntity>> = restaurantRepository.getAllShifts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shiftSummary: StateFlow<ShiftSummaryData> = combine(
        activeShift,
        restaurantRepository.getAllOrders(),
        restaurantRepository.getAllExpenses()
    ) { currentShift, orders, expenses ->
        if (currentShift == null) {
            ShiftSummaryData(null)
        } else {
            val shiftStart = currentShift.startTime
            // Total cash sales during active shift
            val cashSales = orders
                .filter { it.order.createdAt >= shiftStart && it.order.paymentStatus == PaymentStatus.PAID && it.order.paymentMethod == PaymentMethod.CASH }
                .sumOf { it.order.totalAmount }

            // Expenses during active shift
            val cashExpenses = expenses
                .filter { it.date >= shiftStart }
                .sumOf { it.amount }

            val expected = currentShift.startingCash + cashSales - cashExpenses
            ShiftSummaryData(
                activeShift = currentShift,
                cashSales = cashSales,
                cashExpenses = cashExpenses,
                expectedCash = expected
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShiftSummaryData(null))

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun openShift(userId: Long, userName: String, startingCash: Long) {
        viewModelScope.launch {
            try {
                restaurantRepository.openShift(userId, userName, startingCash)
                _message.value = "Shift kasir berhasil dibuka dengan modal awal."
            } catch (e: Exception) {
                _message.value = "Gagal membuka shift: ${e.message}"
            }
        }
    }

    fun closeShift(shiftId: Long, actualCash: Long, note: String) {
        viewModelScope.launch {
            try {
                restaurantRepository.closeShift(shiftId, actualCash, note)
                _message.value = "Shift kasir berhasil ditutup dan direkonsiliasi."
            } catch (e: Exception) {
                _message.value = "Gagal menutup shift: ${e.message}"
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
            return ShiftViewModel(restaurantRepository) as T
        }
    }
}
