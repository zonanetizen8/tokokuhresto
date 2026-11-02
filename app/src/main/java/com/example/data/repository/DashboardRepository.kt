package com.example.data.repository

import com.example.data.local.dao.MenuDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.RestaurantProfileDao
import com.example.data.local.dao.StockDao
import com.example.data.local.dao.TableDao
import com.example.data.local.dao.TopSellerItem
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.RestaurantProfileEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class DashboardRepository(
    private val orderDao: OrderDao,
    private val menuDao: MenuDao,
    private val tableDao: TableDao,
    private val stockDao: StockDao,
    private val profileDao: RestaurantProfileDao
) {

    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    private fun getStartOfWeek(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getStartOfMonth(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getTodayRevenue(): Flow<Long> {
        return orderDao.getRevenueBetween(getStartOfDay(), getEndOfDay())
    }

    fun getTodayOrderCount(): Flow<Int> {
        return orderDao.getTransactionCountBetween(getStartOfDay(), getEndOfDay())
    }

    fun getWeeklyRevenue(): Flow<Long> {
        return orderDao.getRevenueBetween(getStartOfWeek(), getEndOfDay())
    }

    fun getMonthlyRevenue(): Flow<Long> {
        return orderDao.getRevenueBetween(getStartOfMonth(), getEndOfDay())
    }

    fun getActiveMenuCount(): Flow<Int> = menuDao.getActiveMenuCount()

    fun getOccupiedTableCount(): Flow<Int> = tableDao.getOccupiedTableCount()

    fun getLowStockCount(): Flow<Int> = stockDao.getLowStockCount()

    fun getTopSellingMenus(limit: Int = 5): Flow<List<TopSellerItem>> =
        orderDao.getTopSellingMenus(limit)

    fun getRecentOrders(limit: Int = 5): Flow<List<OrderEntity>> =
        orderDao.getRecentOrders(limit)

    fun getRestaurantProfile(): Flow<RestaurantProfileEntity?> =
        profileDao.getProfile()
}
