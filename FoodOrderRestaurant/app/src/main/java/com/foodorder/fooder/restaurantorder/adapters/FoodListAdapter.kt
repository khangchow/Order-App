package com.foodorder.fooder.restaurantorder.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.adapters.base.BaseAdapters
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.LayoutItemFoodListBinding
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.utils.Logger

class FoodListAdapter constructor(
	context: Context,
	dataList: List<Food> = listOf(),
	private val itemOnClick: ItemOnClick<Food>? = null
) : BaseAdapters<Food, LayoutItemFoodListBinding>(context, dataList) {
	
	@SuppressLint("SetTextI18n")
	override fun onBindViewHold(
		position: Int,
		dataItem: Food,
		binding: LayoutItemFoodListBinding
	) {
		binding.apply {
			Logger.e("item path food = ${dataItem.imagePath}")
			Glide.with(context).load(dataItem.imagePath).error(R.drawable.ic_icon).into(imgItem)
			tvName.text = dataItem.name
			tvPrice.text = "${dataItem.price} ${dataItem.currencyCode}"
			tvStatus.text = dataItem.status
			btnEdit.setOnClickListener {
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = dataItem)
			}
			btnDelete.setOnClickListener {
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = dataItem)
			}
		}
	}
	
	override fun getViewBinding(viewGroup: ViewGroup) =
		LayoutItemFoodListBinding.inflate(LayoutInflater.from(context), viewGroup, false)
}