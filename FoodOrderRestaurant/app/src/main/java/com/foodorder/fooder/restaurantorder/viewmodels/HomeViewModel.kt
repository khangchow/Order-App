package com.foodorder.fooder.restaurantorder.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.foodorder.fooder.restaurantorder.db.*
import com.foodorder.fooder.restaurantorder.models.*
import com.foodorder.fooder.restaurantorder.utils.CombinedLiveData
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val orderDao: OrderDao,
	private val orderFoodDao: OrderFoodDao,
	private val tableDao: TableDao,
) : BaseViewModel() {
	private val _table = MutableLiveData<Table>()
	val table: LiveData<Table> get() = _table

	lateinit var ordersList: List<Order>
	
	fun getTableListLive() = tableDao.getTableList()
	
	fun getProcessingOrderListLive() = orderDao.getProcessingOrder()
	
	suspend fun getOrderFoodListLive(idOrder: Long) =
		orderFoodDao.getOrderFoodList(idOrder).convertOrderFoodList()
	
	val listOrderLive = CombinedLiveData(
		getTableListLive(),
		getProcessingOrderListLive()
	) { dataList: List<Any?> ->
		val listTableEntity = dataList.getOrNull(0) as? List<TableEntity>
		val listOrderEntity = dataList.getOrNull(1) as? List<OrderEntity>
		val list = listTableEntity?.map { tableEntity ->
			val orderEntityMap = listOrderEntity?.find { tableEntity.id == it.idTable }
			Order(
				id = orderEntityMap?.id ?: 0,
				timeJoin = orderEntityMap?.timeJoin ?: 0,
				timeFinish = orderEntityMap?.timeFinish ?: 0,
				idTable = orderEntityMap?.idTable ?: tableEntity.id,
				nameTable = orderEntityMap?.nameTable ?: tableEntity.name,
				imagePathTable = orderEntityMap?.imagePathTable ?: tableEntity.imagePath,
				statusTable = orderEntityMap?.statusTable ?: tableEntity.status,
			)
		}
		list
	}

	fun getEmptyTables(): List<String> {
		val tableNameList = mutableListOf<String>()

		ordersList.toMutableList().filterIndexed { index, order ->
			order.id == 0L
		}.map {
			tableNameList.add(it.nameTable)
		}

		return tableNameList
	}

	fun changeTable(nameTable: String, idTable: Int, id: Long) {
		viewModelScope.launch {
			orderDao.changeTable(nameTable, idTable, id)
		}
	}

	fun getTable(nameTable: String) {
		viewModelScope.launch {
			_table.postValue(tableDao.getTable(nameTable).convertTable())
		}
	}
}