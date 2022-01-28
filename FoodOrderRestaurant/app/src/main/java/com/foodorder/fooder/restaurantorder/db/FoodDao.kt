package com.foodorder.fooder.restaurantorder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(model: FoodEntity?)

    @Update
    suspend fun updates(vararg models: FoodEntity)

    @Update
    suspend fun update(model: FoodEntity)

    @Delete
    suspend fun delete(model: FoodEntity?)

    @Query("DELETE FROM FoodList")
    suspend fun deleteAll()

    @Query("SELECT * FROM FoodList")
    fun getFoodList(): LiveData<List<FoodEntity>>
}