package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.MenuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM menus ORDER BY isFavorite DESC, name ASC")
    fun getAllMenus(): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menus WHERE isAvailable = 1 ORDER BY isFavorite DESC, name ASC")
    fun getAvailableMenus(): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menus WHERE isFavorite = 1 AND isAvailable = 1 ORDER BY name ASC")
    fun getFavoriteMenus(): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menus WHERE categoryId = :categoryId ORDER BY name ASC")
    fun getMenusByCategory(categoryId: Long): Flow<List<MenuEntity>>

    @Query("SELECT * FROM menus WHERE id = :id LIMIT 1")
    fun getMenuById(id: Long): Flow<MenuEntity?>

    @Query("SELECT * FROM menus WHERE id = :id LIMIT 1")
    suspend fun getMenuByIdSync(id: Long): MenuEntity?

    @Query("SELECT * FROM menus WHERE code = :code LIMIT 1")
    suspend fun getMenuByCode(code: String): MenuEntity?

    @Query("SELECT COUNT(*) FROM menus")
    suspend fun getMenuCount(): Int

    @Query("SELECT COUNT(*) FROM menus WHERE isAvailable = 1")
    fun getActiveMenuCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenu(menu: MenuEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenus(menus: List<MenuEntity>)

    @Update
    suspend fun updateMenu(menu: MenuEntity)

    @Query("UPDATE menus SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: Long, isAvailable: Boolean)

    @Query("UPDATE menus SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE menus SET stock = stock - :quantity WHERE id = :id")
    suspend fun reduceStock(id: Long, quantity: Double)

    @Query("DELETE FROM menus WHERE id = :id")
    suspend fun deleteMenu(id: Long)
}
