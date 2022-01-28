package com.foodorder.fooder.restaurantorder.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(model: TableEntity?)

    @Update
    suspend fun updates(vararg models: TableEntity)

    @Update
    suspend fun update(model: TableEntity)

    @Delete
    suspend fun delete(model: TableEntity?)

    @Query("DELETE FROM TableList")
    suspend fun deleteAll()

    @Query("SELECT * FROM TableList")
    fun getTableList(): LiveData<List<TableEntity>>

    @Query("SELECT * FROM TableList WHERE name = :name")
    suspend fun getTable(name: String): TableEntity
}