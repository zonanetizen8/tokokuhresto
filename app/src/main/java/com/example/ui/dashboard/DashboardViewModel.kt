package com.example.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.dao.TopSellerItem
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.repository.DashboardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {

    val todayRevenue: StateFlow<Long> = dashboardRepository.getTodayRevenue()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    val todayOrderCount: StateFlow<Int> = dashboardRepository.getTodayOrderCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val weeklyRevenue: StateFlow<Long> = dashboardRepository.getWeeklyRevenue()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    val monthlyRevenue: StateFlow<Long> = dashboardRepository.getMonthlyRevenue()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

    val activeMenuCount: StateFlow<Int> = dashboardRepository.getActiveMenuCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val occupiedTableCount: StateFlow<Int> = dashboardRepository.getOccupiedTableCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val lowStockCount: StateFlow<Int> = dashboardRepository.getLowStockCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val topSellingMenus: StateFlow<List<TopSellerItem>> = dashboardRepository.getTopSellingMenus(5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentOrders: StateFlow<List<OrderEntity>> = dashboardRepository.getRecentOrders(5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val restaurantProfile: StateFlow<RestaurantProfileEntity?> = dashboardRepository.getRestaurantProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    class Factory(private val dashboardRepository: DashboardRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(dashboardRepository) as T
        }
    }
}
