package com.foodorder.fooder.restaurantorder.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.adapters.base.BaseAdapters
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.LayoutItemOrderListBinding
import com.foodorder.fooder.restaurantorder.models.Order
import com.foodorder.fooder.restaurantorder.utils.DateUtils

class OrderListAdapter constructor(
	context: Context,
	dataList: List<Order> = listOf(),
	private val itemOnClick: ItemOnClick<Order>? = null
) : BaseAdapters<Order, LayoutItemOrderListBinding>(context, dataList) {
	
	@SuppressLint("SetTextI18n")
	override fun onBindViewHold(
		position: Int,
		dataItem: Order,
		binding: LayoutItemOrderListBinding
	) {
		binding.apply {
			Glide.with(context).load(dataItem.imagePathTable).error(R.drawable.ic_icon)
				.into(imgTable)
			tvTitle.text = dataItem.nameTable
			if (dataItem.timeJoin != 0L) {
				tvStatus.text = String.format(
					context.getString(R.string.title_time_start),
					DateUtils.convertLongToDateTime(dataItem.timeJoin, DateUtils.TIME_DATE_FORMAT)
				)
				groupManager.visibility = View.VISIBLE
			} else {
				tvStatus.text = context.getString(R.string.free_status_table)
				groupManager.visibility = View.GONE
			}
			btnMoveTable.setOnClickListener {
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = dataItem)
			}
			btnOrder.setOnClickListener {
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = dataItem)
			}
			btnPayment.setOnClickListener {
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = dataItem)
			}
		}
	}
	
	override fun getViewBinding(viewGroup: ViewGroup) =
		LayoutItemOrderListBinding.inflate(LayoutInflater.from(context), viewGroup, false)
}