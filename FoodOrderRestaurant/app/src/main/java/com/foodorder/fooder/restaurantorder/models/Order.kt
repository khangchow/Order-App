package com.foodorder.fooder.restaurantorder.models

import com.foodorder.fooder.restaurantorder.db.OrderEntity
import com.foodorder.fooder.restaurantorder.models.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
	val id: Long = 0,
	val timeJoin: Long = System.currentTimeMillis(),
	val timeFinish: Long = 0,
	val idTable: Int = 0,
	val nameTable: String = "",
	val imagePathTable: String = "",
	val statusTable: String = "",
	val listOrderFood: List<Food> = listOf()
) : BaseModel()

fun Order.convertToOrderEntity() = OrderEntity(
	id = id,
	timeJoin = timeJoin,
	timeFinish = timeFinish,
	idTable = idTable,
	nameTable = nameTable,
	imagePathTable = imagePathTable,
	statusTable = statusTable,
)

