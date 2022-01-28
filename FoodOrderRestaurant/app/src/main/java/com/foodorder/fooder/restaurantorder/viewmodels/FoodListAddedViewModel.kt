package com.foodorder.fooder.restaurantorder.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.foodorder.fooder.restaurantorder.db.FoodDao
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.models.convertFoodEntity
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodListAddedViewModel @Inject constructor(
    private val foodDao: FoodDao,
) : BaseViewModel() {
    
    val foodItemLiveData = MutableLiveData<Food>()
    
    val isFoodItemDataValid = Transformations.map(foodItemLiveData) {
        it.name.isNotEmpty() && it.price.isNotEmpty()
    }
    
    fun insertOrUpdateFoodItem(item: Food?) =
        foodDao.insertOrReplace(item?.convertFoodEntity())
    
}