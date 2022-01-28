package com.foodorder.fooder.restaurantorder.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.adapters.base.BaseAdapters
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.LayoutItemOrderBillBinding
import com.foodorder.fooder.restaurantorder.databinding.LayoutItemTableListBinding
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.models.Table
import com.foodorder.fooder.restaurantorder.utils.Utils

class OrderBillAdapter (
    context: Context,
    dataList: List<Food> = listOf(),
    private val itemOnClick: ItemOnClick<Food>? = null
    ) : BaseAdapters<Food, LayoutItemOrderBillBinding>(context, dataList) {

        private val defaultPrice = "0"

        @SuppressLint("SetTextI18n")
        override fun onBindViewHold(
            position: Int,
            dataItem: Food,
            binding: LayoutItemOrderBillBinding
        ) {
            binding.apply {
                tvName.text = dataItem.name
                tvQuantity.text = dataItem.quantity.toString()
                tvTotalPrice.text = Utils.convertLongtoAmount(dataItem.quantity
                        * Utils.convertAmountToLong(dataItem.price), dataItem.currencyCode)
            }

        }

        override fun getViewBinding(viewGroup: ViewGroup) =
            LayoutItemOrderBillBinding.inflate(LayoutInflater.from(context), viewGroup, false)
    }