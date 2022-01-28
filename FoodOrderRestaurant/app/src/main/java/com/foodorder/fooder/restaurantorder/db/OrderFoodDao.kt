package com.foodorder.fooder.restaurantorder.db

import androidx.room.*

@Dao
interface OrderFoodDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(model: OrderFoodEntity?)
	
	@Update
	suspend fun updates(vararg models: OrderFoodEntity)
	
	@Update
	suspend fun update(model: OrderFoodEntity)
	
	@Delete
	suspend fun delete(model: OrderFoodEntity?)
	
	@Query("DELETE FROM OrderFood")
	suspend fun deleteAll()
	
	@Query("SELECT * FROM OrderFood WHERE idOrder == :idOrder")
	suspend fun getOrderFoodList(idOrder: Long): List<OrderFoodEntity>
	
	@Query("SELECT * FROM OrderFood")
	suspend fun getAllOrderFoodList(): List<OrderFoodEntity>
}