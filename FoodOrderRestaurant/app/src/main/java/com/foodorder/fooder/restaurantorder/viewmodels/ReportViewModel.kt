package com.foodorder.fooder.restaurantorder.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foodorder.fooder.restaurantorder.db.OrderDao
import com.foodorder.fooder.restaurantorder.db.OrderFoodDao
import com.foodorder.fooder.restaurantorder.models.RevenueInfo
import com.foodorder.fooder.restaurantorder.models.convertToBarEntryList
import com.foodorder.fooder.restaurantorder.utils.DateUtils
import com.foodorder.fooder.restaurantorder.utils.Utils
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val orderDao: OrderDao,
    private val orderFoodDao: OrderFoodDao,
): BaseViewModel() {
    private val _list = MutableLiveData<List<RevenueInfo>>()
    val list: LiveData<List<RevenueInfo>> get() = _list

     fun filterByOrder(timeStart: Long, timeEnd: Long) {
        viewModelScope.launch {
            val dataList: MutableList<RevenueInfo> = mutableListOf()
            var count = 0f

            orderDao.getOrderByDate(timeStart, timeEnd).map { order->
//            if (order.timeFinish != 0L) {
                var sum = 0f

                orderFoodDao.getOrderFoodList(order.id).map { food->
                    sum += (food.quantity * Utils.convertAmountToLong(food.priceFood))
                }

                dataList.add(RevenueInfo(++count, DateUtils.convertLongToTime(order.timeJoin), sum))
//            }
            }

            _list.postValue(dataList)
        }
    }

    fun filterByDay(timeStart: Calendar, timeEnd: Calendar) {
        viewModelScope.launch {
            Log.d("Date", "Time"+DateUtils.convertLongToDateTime(timeStart.timeInMillis))
            Log.d("Date", "Time"+DateUtils.convertLongToDateTime(timeEnd.timeInMillis))
            val dataList: MutableList<RevenueInfo> = mutableListOf()
            var count = 0f
            var total: Float
            var end: Calendar = DateUtils.current()
            end.set(timeStart.get(Calendar.YEAR), timeStart.get(Calendar.MONTH)
                , timeStart.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0)
            var start: Calendar = timeStart

            while (timeEnd.timeInMillis > start.timeInMillis) {
                total = 0f

                Log.d("Date", DateUtils.convertLongToDateTime(start.timeInMillis))
                Log.d("Date", DateUtils.convertLongToDateTime(end.timeInMillis))

                orderDao.getOrderByDate(start.timeInMillis, end.timeInMillis).map { order->
//            if (order.timeFinish != 0L) {

                    orderFoodDao.getOrderFoodList(order.id).map { food->
                        total += (food.quantity * Utils.convertAmountToLong(food.priceFood))
                    }

//            }
                }

                Log.d("Date", total.toString())
                dataList.add(RevenueInfo(++count, DateUtils.convertCalendartoDayMonth(start), total))

                start.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH) + 1)
                end.set(Calendar.DAY_OF_MONTH, end.get(Calendar.DAY_OF_MONTH) + 1)
            }

            _list.postValue(dataList)
        }
    }
}
