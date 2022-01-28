package com.foodorder.fooder.restaurantorder.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderList")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timeJoin: Long = 0,
    val timeFinish: Long = 0,
    val idTable: Int,
    val nameTable: String,
    val imagePathTable: String,
    val statusTable: String,
)