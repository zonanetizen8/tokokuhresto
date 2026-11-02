package com.example.data

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.DashboardRepository
import com.example.data.repository.RestaurantRepository

class AppContainer(context: Context) {
    val database: AppDatabase = AppDatabase.getDatabase(context)

    val authRepository: AuthRepository by lazy {
        AuthRepository(database.userDao())
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository(
            orderDao = database.orderDao(),
            menuDao = database.menuDao(),
            tableDao = database.tableDao(),
            stockDao = database.stockDao(),
            profileDao = database.restaurantProfileDao()
        )
    }

    val restaurantRepository: RestaurantRepository by lazy {
        RestaurantRepository(database)
    }
}
