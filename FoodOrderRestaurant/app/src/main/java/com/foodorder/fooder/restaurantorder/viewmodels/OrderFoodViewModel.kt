package com.foodorder.fooder.restaurantorder.viewmodels

import androidx.lifecycle.MutableLiveData
import com.foodorder.fooder.restaurantorder.db.FoodDao
import com.foodorder.fooder.restaurantorder.db.OrderDao
import com.foodorder.fooder.restaurantorder.db.OrderFoodDao
import com.foodorder.fooder.restaurantorder.models.*
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderFoodViewModel @Inject constructor(
	private val foodDao: FoodDao,
	private val orderDao: OrderDao,
	private val orderFoodDao: OrderFoodDao,
) : BaseViewModel() {
	
	lateinit var order: Order
	
	val orderDraftLive = MutableLiveData<List<Food>>()
	
	fun getFoodListLive() = foodDao.getFoodList()
	
	suspend fun insertOrReplaceOrder() = orderDao.insertOrReplace(order.copy(
		timeJoin = if (order.timeJoin == 0L) System.currentTimeMillis() else order.timeJoin
	).convertToOrderEntity())
	
	suspend fun getOrderFoodListLive() =
		orderFoodDao.getOrderFoodList(order.id).convertOrderFoodList()
	
	suspend fun insertOrReplaceOrderFood(food: Food) {
		orderFoodDao.insertOrReplace(food.convertOrderFoodEntity(order.id))
	}
}