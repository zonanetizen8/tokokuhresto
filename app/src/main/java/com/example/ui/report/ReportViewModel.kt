package com.example.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderWithItems
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

enum class ReportPeriod(val label: String) {
    TODAY("Hari Ini"),
    LAST_7_DAYS("7 Hari"),
    THIS_MONTH("Bulan Ini"),
    ALL_TIME("Semua")
}

data class PaymentMethodBreakdown(
    val method: PaymentMethod,
    val count: Int,
    val totalAmount: Long
)

data class TopMenuItem(
    val menuName: String,
    val totalQty: Int,
    val totalRevenue: Long
)

data class ReportAnalyticsData(
    val period: ReportPeriod,
    val totalRevenue: Long = 0L,
    val totalTransactions: Int = 0,
    val averageTicket: Long = 0L,
    val totalItemsSold: Int = 0,
    val paymentBreakdowns: List<PaymentMethodBreakdown> = emptyList(),
    val topMenus: List<TopMenuItem> = emptyList()
)

class ReportViewModel(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.TODAY)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()

    val analytics: StateFlow<ReportAnalyticsData> = combine(
        restaurantRepository.getAllOrders(),
        _selectedPeriod
    ) { allOrders, period ->
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        val startTime = when (period) {
            ReportPeriod.TODAY -> {
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            ReportPeriod.LAST_7_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                cal.timeInMillis
            }
            ReportPeriod.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            ReportPeriod.ALL_TIME -> 0L
        }

        // Filter valid paid completed orders within period
        val filteredOrders = allOrders.filter { item ->
            val o = item.order
            o.createdAt >= startTime &&
            o.paymentStatus == PaymentStatus.PAID &&
            o.status != OrderStatus.CANCELLED
        }

        val totalRevenue = filteredOrders.sumOf { it.order.totalAmount }
        val totalTransactions = filteredOrders.size
        val avgTicket = if (totalTransactions > 0) totalRevenue / totalTransactions else 0L

        val allItems = filteredOrders.flatMap { it.items }
        val totalItemsSold = allItems.sumOf { it.quantity }

        // Payment method breakdown
        val methodGroups = filteredOrders.groupBy { it.order.paymentMethod }
        val paymentBreakdowns = PaymentMethod.values().map { method ->
            val list = methodGroups[method] ?: emptyList()
            PaymentMethodBreakdown(
                method = method,
                count = list.size,
                totalAmount = list.sumOf { it.order.totalAmount }
            )
        }

        // Top menus
        val menuGroups = allItems.groupBy { it.menuName }
        val topMenus = menuGroups.map { (name, items) ->
            TopMenuItem(
                menuName = name,
                totalQty = items.sumOf { it.quantity },
                totalRevenue = items.sumOf { it.subtotal }
            )
        }.sortedByDescending { it.totalQty }.take(5)

        ReportAnalyticsData(
            period = period,
            totalRevenue = totalRevenue,
            totalTransactions = totalTransactions,
            averageTicket = avgTicket,
            totalItemsSold = totalItemsSold,
            paymentBreakdowns = paymentBreakdowns,
            topMenus = topMenus
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportAnalyticsData(ReportPeriod.TODAY))

    fun setPeriod(period: ReportPeriod) {
        _selectedPeriod.value = period
    }

    class Factory(
        private val restaurantRepository: RestaurantRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReportViewModel(restaurantRepository) as T
        }
    }
}
