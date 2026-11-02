package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.MenuDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.RestaurantProfileDao
import com.example.data.local.dao.ShiftExpenseDao
import com.example.data.local.dao.StockDao
import com.example.data.local.dao.TableDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.IngredientEntity
import com.example.data.local.entities.MenuEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.RecipeEntity
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.local.entities.ShiftEntity
import com.example.data.local.entities.StockMovementEntity
import com.example.data.local.entities.TableEntity
import com.example.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        MenuEntity::class,
        TableEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        IngredientEntity::class,
        RecipeEntity::class,
        StockMovementEntity::class,
        ShiftEntity::class,
        ExpenseEntity::class,
        RestaurantProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun menuDao(): MenuDao
    abstract fun tableDao(): TableDao
    abstract fun orderDao(): OrderDao
    abstract fun stockDao(): StockDao
    abstract fun shiftExpenseDao(): ShiftExpenseDao
    abstract fun restaurantProfileDao(): RestaurantProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Start with a genuinely empty local database. No demo users, products,
                // categories, tables, shifts, or sample transactions are inserted.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tokokuh_resto_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
