package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.IngredientEntity
import com.example.data.local.entities.RecipeEntity
import com.example.data.local.entities.StockMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {
    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE stock <= minStock ORDER BY stock ASC")
    fun getLowStockIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE id = :id LIMIT 1")
    suspend fun getIngredientById(id: Long): IngredientEntity?

    @Query("SELECT COUNT(*) FROM ingredients WHERE stock <= minStock")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun getIngredientCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Update
    suspend fun updateIngredient(ingredient: IngredientEntity)

    @Query("UPDATE ingredients SET stock = stock + :qty WHERE id = :id")
    suspend fun adjustStock(id: Long, qty: Double)

    @Query("DELETE FROM ingredients WHERE id = :id")
    suspend fun deleteIngredient(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(movement: StockMovementEntity): Long

    @Query("SELECT * FROM stock_movements WHERE ingredientId = :ingredientId ORDER BY timestamp DESC")
    fun getMovementsForIngredient(ingredientId: Long): Flow<List<StockMovementEntity>>

    @Query("SELECT * FROM recipes WHERE menuId = :menuId")
    fun getRecipesForMenu(menuId: Long): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Query("DELETE FROM recipes WHERE menuId = :menuId")
    suspend fun deleteRecipesByMenu(menuId: Long)
}
