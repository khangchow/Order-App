package com.foodorder.fooder.restaurantorder.models

import com.foodorder.fooder.restaurantorder.models.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SampleModel(
	val data: String
): BaseModel()
