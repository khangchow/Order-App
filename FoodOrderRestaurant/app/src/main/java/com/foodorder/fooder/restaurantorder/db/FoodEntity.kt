package com.foodorder.fooder.restaurantorder.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FoodList")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val imagePath: String,
    val price: String,
    val currencyCode: String,
    val status: String,
)