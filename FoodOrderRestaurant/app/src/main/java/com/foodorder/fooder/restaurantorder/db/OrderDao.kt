package com.foodorder.fooder.restaurantorder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(model: OrderEntity?): Long

    @Update
    suspend fun updates(vararg models: OrderEntity)

    @Update
    suspend fun update(model: OrderEntity)

    @Delete
    suspend fun delete(model: OrderEntity?)

    @Query("DELETE FROM OrderList")
    suspend fun deleteAll()

    @Query("SELECT * FROM OrderList WHERE timeFinish == 0")
    fun getProcessingOrder(): LiveData<List<OrderEntity>>

    @Query("SELECT * FROM OrderList WHERE timeJoin >= :timeStart and timeJoin <= :timeEnd")
    suspend fun getOrderByDate(timeStart: Long, timeEnd: Long): List<OrderEntity>

    @Query("UPDATE OrderList SET timeFinish=:timeFinish WHERE id = :id")
    suspend fun finishOrder(timeFinish: Long, id: Long)

    @Query("UPDATE OrderList SET nameTable=:nameTable, idTable = :idTable WHERE id = :id")
    suspend fun changeTable(nameTable: String, idTable: Int, id: Long)
}
