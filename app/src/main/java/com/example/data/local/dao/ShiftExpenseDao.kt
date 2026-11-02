package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.ShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftExpenseDao {
    @Query("SELECT * FROM shifts WHERE userId = :userId AND status = 'OPEN' ORDER BY startTime DESC LIMIT 1")
    fun getActiveShiftForUser(userId: Long): Flow<ShiftEntity?>

    @Query("SELECT * FROM shifts WHERE status = 'OPEN' ORDER BY startTime DESC LIMIT 1")
    fun getActiveShift(): Flow<ShiftEntity?>

    @Query("SELECT * FROM shifts ORDER BY startTime DESC")
    fun getAllShifts(): Flow<List<ShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftEntity): Long

    @Update
    suspend fun updateShift(shift: ShiftEntity)

    // Expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    fun getExpensesBetween(startTime: Long, endTime: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date >= :startTime AND date <= :endTime")
    fun getTotalExpenseBetween(startTime: Long, endTime: Long): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Long)
}
