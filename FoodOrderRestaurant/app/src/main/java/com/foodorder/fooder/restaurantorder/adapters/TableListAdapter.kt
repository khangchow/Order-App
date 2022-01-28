package com.foodorder.fooder.restaurantorder.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.adapters.base.BaseAdapters
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.LayoutItemTableListBinding
import com.foodorder.fooder.restaurantorder.models.Table

class TableListAdapter(
    context: Context,
    dataList: List<Table> = listOf(),
    private val itemOnClick: ItemOnClick<Table>? = null
) : BaseAdapters<Table, LayoutItemTableListBinding>(context, dataList) {

    private val defaultPrice = "0"

    @SuppressLint("SetTextI18n")
    override fun onBindViewHold(
        position: Int,
        dataItem: Table,
        binding: LayoutItemTableListBinding
    ) {
        binding.apply {
            Glide.with(context).load(dataItem.imagePath).error(R.drawable.ic_icon).into(imgItem)
            tvName.text = dataItem.name
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
        LayoutItemTableListBinding.inflate(LayoutInflater.from(context), viewGroup, false)
}