package com.foodorder.fooder.restaurantorder.db.root

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.foodorder.fooder.restaurantorder.db.*
import com.foodorder.fooder.restaurantorder.utils.Converters

@Database(
    entities = [
        FoodEntity::class,
        TableEntity::class,
        OrderEntity::class,
        OrderFoodEntity::class,
    ],
    version = 3,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class OrderFoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun tableDao(): TableDao
    abstract fun orderDao(): OrderDao
    abstract fun orderFoodDao(): OrderFoodDao
}