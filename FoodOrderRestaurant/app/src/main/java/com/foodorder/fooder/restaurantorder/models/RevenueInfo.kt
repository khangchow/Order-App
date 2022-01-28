package com.foodorder.fooder.restaurantorder.models

import com.foodorder.fooder.restaurantorder.db.FoodEntity
import com.foodorder.fooder.restaurantorder.db.OrderFoodEntity
import com.github.mikephil.charting.data.BarEntry

data class RevenueInfo(
    val id: Float,
    val barName: String,
    val revenue: Float
)

fun RevenueInfo.convertToBarEntry() = BarEntry(
    id,
    revenue
)

fun List<RevenueInfo>.convertToBarEntryList() = map {
    it.convertToBarEntry()
}