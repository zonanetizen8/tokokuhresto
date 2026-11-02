package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.TableEntity
import com.example.data.local.entities.TableStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables ORDER BY tableNumber ASC")
    fun getAllTables(): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE id = :id LIMIT 1")
    fun getTableById(id: Long): Flow<TableEntity?>

    @Query("SELECT COUNT(*) FROM tables WHERE status != 'VACANT'")
    fun getOccupiedTableCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tables")
    suspend fun getTableCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: TableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<TableEntity>)

    @Update
    suspend fun updateTable(table: TableEntity)

    @Query("UPDATE tables SET status = :status, currentOrderId = :orderId WHERE id = :tableId")
    suspend fun updateTableStatus(tableId: Long, status: TableStatus, orderId: Long?)

    @Query("DELETE FROM tables WHERE id = :id")
    suspend fun deleteTable(id: Long)
}
