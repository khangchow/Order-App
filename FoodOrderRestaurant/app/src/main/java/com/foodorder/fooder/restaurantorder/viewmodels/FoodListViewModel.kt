package com.foodorder.fooder.restaurantorder.viewmodels

import com.foodorder.fooder.restaurantorder.db.FoodDao
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.models.convertFoodEntity
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(private val foodDao: FoodDao) : BaseViewModel() {
	
	fun getFoodListLive() = foodDao.getFoodList()
	
	suspend fun deleteFoodItem(food: Food?) = foodDao.delete(food?.convertFoodEntity())
	
}