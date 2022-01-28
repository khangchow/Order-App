package com.foodorder.fooder.restaurantorder.viewmodels

import com.foodorder.fooder.restaurantorder.db.FoodDao
import com.foodorder.fooder.restaurantorder.db.OrderDao
import com.foodorder.fooder.restaurantorder.db.OrderFoodDao
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.models.convertOrderFoodEntity
import com.foodorder.fooder.restaurantorder.models.convertOrderFoodList
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderConfirmationViewModel @Inject constructor(
	private val foodDao: FoodDao,
	private val orderDao: OrderDao,
	private val orderFoodDao: OrderFoodDao,
) : BaseViewModel() {
	
	suspend fun getOrderFoodListLive(orderId: Long) =
		orderFoodDao.getOrderFoodList(orderId).convertOrderFoodList()
	
	suspend fun insertOrReplaceOrderFood(orderId: Long, food: Food) {
		orderFoodDao.insertOrReplace(food.convertOrderFoodEntity(orderId))
	}
}