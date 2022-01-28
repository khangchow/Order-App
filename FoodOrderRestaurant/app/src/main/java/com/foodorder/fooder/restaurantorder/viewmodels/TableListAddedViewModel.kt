package com.foodorder.fooder.restaurantorder.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.foodorder.fooder.restaurantorder.db.TableDao
import com.foodorder.fooder.restaurantorder.models.Table
import com.foodorder.fooder.restaurantorder.models.convertTableEntity
import com.foodorder.fooder.restaurantorder.viewmodels.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TableListAddedViewModel @Inject constructor(
    private val tableDao: TableDao,
): BaseViewModel() {

    val tableItemLiveData = MutableLiveData<Table>()

    val isTableItemDataValid = Transformations.map(tableItemLiveData) {
        it.name.isNotEmpty()
    }

    suspend fun insertOrUpdateTableItem(item: Table?) =
        tableDao.insertOrReplace(item?.convertTableEntity())
}