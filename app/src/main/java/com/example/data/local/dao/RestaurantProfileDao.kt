package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.RestaurantProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantProfileDao {
    @Query("SELECT * FROM restaurant_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<RestaurantProfileEntity?>

    @Query("SELECT * FROM restaurant_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): RestaurantProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: RestaurantProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: RestaurantProfileEntity)

    @Update
    suspend fun updateProfile(profile: RestaurantProfileEntity)
}
