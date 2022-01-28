package com.foodorder.fooder.restaurantorder.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TableList")
data class TableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val imagePath: String,
    val status: String,
)