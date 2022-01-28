package com.foodorder.fooder.restaurantorder.models

import com.foodorder.fooder.restaurantorder.db.FoodEntity
import com.foodorder.fooder.restaurantorder.db.TableEntity
import com.foodorder.fooder.restaurantorder.models.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Table(
	val id: Int = 0,
	val name: String = "",
	val imagePath: String = "",
	val status: String = "",
) : BaseModel()

fun Table.convertTableEntity() = TableEntity(
	id = id,
	name = name,
	imagePath = imagePath,
	status = status
)

fun TableEntity.convertTable() = Table(
	id = id,
	name = name,
	imagePath = imagePath,
	status = status
)

fun List<TableEntity>.convertTableList() = map {
	it.convertTable()
}
