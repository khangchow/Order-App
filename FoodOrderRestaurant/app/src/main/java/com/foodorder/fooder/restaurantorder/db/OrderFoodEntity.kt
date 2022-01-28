package com.foodorder.fooder.restaurantorder.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderFood", primaryKeys = ["id", "idOrder"])
data class OrderFoodEntity(
    val id: Long = 0,
    val idOrder: Long,
    val nameFood: String,
    val imageFoodPath: String,
    val priceFood: String,
    val currencyCode: String,
    val status: String,
    val quantity: Int = 0,
)