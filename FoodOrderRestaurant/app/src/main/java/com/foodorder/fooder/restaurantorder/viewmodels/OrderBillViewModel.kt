package com.foodorder.fooder.restaurantorder.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foodorder.fooder.restaurantorder.db.OrderDao
import com.foodorder.fooder.restaurantorder.db.OrderFoodDao
import com.foodorder.fooder.restaurantorder.db.OrderFoodEntity
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderBillViewModel @Inject constructor(
    private val orderFoodDao: OrderFoodDao,
    private val orderDao: OrderDao
) : BaseViewModel() {
    private val _list = MutableLiveData<List<OrderFoodEntity>>()
    val list: LiveData<List<OrderFoodEntity>> get() = _list

    fun getOrderFoodListLive(id: Long) {
        viewModelScope.launch {
            _list.postValue(orderFoodDao.getOrderFoodList(id))
        }
    }

    fun finishOrder(id: Long) {
        viewModelScope.launch {
            orderDao.finishOrder(System.currentTimeMillis(), id)
        }
    }
}