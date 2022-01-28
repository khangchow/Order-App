package com.foodorder.fooder.restaurantorder.viewmodels

import com.foodorder.fooder.restaurantorder.db.TableDao
import com.foodorder.fooder.restaurantorder.db.TableEntity
import com.foodorder.fooder.restaurantorder.models.Table
import com.foodorder.fooder.restaurantorder.models.convertTable
import com.foodorder.fooder.restaurantorder.models.convertTableEntity
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TableListViewModel @Inject constructor(private val tableDao: TableDao) : BaseViewModel() {
	
	fun getTableListLive() = tableDao.getTableList()

	suspend fun insertOrReplaceTable(table: TableEntity) = tableDao.insertOrReplace(table)

	suspend fun deleteTable(table: Table?) = tableDao.delete(table?.convertTableEntity())

	suspend fun updateTable(table: TableEntity) = tableDao.update(table)
	
}