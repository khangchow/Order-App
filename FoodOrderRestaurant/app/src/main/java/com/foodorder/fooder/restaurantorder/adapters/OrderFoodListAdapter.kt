package com.foodorder.fooder.restaurantorder.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.OrderFoodActivity
import com.foodorder.fooder.restaurantorder.adapters.base.BaseAdapters
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.LayoutItemOrderFoodBinding
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.utils.Logger
import com.foodorder.fooder.restaurantorder.utils.showCustomDialog

class OrderFoodListAdapter constructor(
	context: Context,
	dataList: List<Food> = listOf(),
	private val itemOnClick: ItemOnClick<Food>? = null
) : BaseAdapters<Food, LayoutItemOrderFoodBinding>(context, dataList) {
	
	private val durationTimeAnimation = 200L
	
	@SuppressLint("SetTextI18n")
	override fun onBindViewHold(
		position: Int,
		dataItem: Food,
		binding: LayoutItemOrderFoodBinding
	) {
		binding.apply {
			Logger.e("item path food = ${dataItem.imagePath}")
			Glide.with(context).load(dataItem.imagePath).error(R.drawable.ic_icon).into(imgFood)
			tvTitle.text = dataItem.name
			tvPrice.text = "${dataItem.price} ${dataItem.currencyCode}"
			tvStatus.text = dataItem.status
			tvQuantity.text = dataItem.quantity.toString()
			if (dataItem.quantity >= 1) {
				viewDecrease.visibility = View.VISIBLE
			} else {
				viewDecrease.visibility = View.INVISIBLE
			}

			tvQuantity.setOnClickListener {view ->
				val data = getItem(position)

				(context as OrderFoodActivity).showCustomDialog(
					dialogTitle = context.getString(R.string.quantity),
					positiveLabel = context.getString(R.string.accept),
					positiveClick = {
						tvQuantity.text = it

						updateDataList(
							position = position,
							item = data.copy(
								quantity = Integer.valueOf(it)
							)
						)

						itemOnClick?.onClick(view = view, idViewClick = view.id, dataClicked = dataItem)
					},
					negativeLabel = context.getString(R.string.cancel_button),
					isNumberInputType = true
				)
			}
			
			imgDecrease.setOnClickListener {
				val data = getItem(position)
				if (data.quantity >= 1) {
					if (data.quantity == 1) {
						val animate = TranslateAnimation(
							imgDecrease.x,  // fromXDelta
							imgAddition.x,  // toXDelta
							0f,  // fromYDelta
							0f
						) // toYDelta
						
						animate.duration = durationTimeAnimation
						animate.fillAfter = true
						imgDecrease.startAnimation(animate)
						tvQuantity.startAnimation(animate)
						viewDecrease.visibility = View.INVISIBLE
					}
					tvQuantity.text = (data.quantity - 1).toString()
					updateDataList(
						position = position,
						item = data.copy(
							quantity = data.quantity - 1
						)
					)
				}
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = getItem(position))
			}
			imgAddition.setOnClickListener {
				val data = getItem(position)
				if (data.quantity == 0) {
					viewDecrease.visibility = View.VISIBLE
					Logger.e("imgAddition.x = ${imgAddition.x}")
					Logger.e("imgDecrease.x = ${imgDecrease.x}")
					val animate = TranslateAnimation(
						imgAddition.x,  // fromXDelta
						imgDecrease.x,  // toXDelta
						0f,  // fromYDelta
						0f
					) // toYDelta
					
					animate.duration = durationTimeAnimation
//					animate.fillAfter = true
					imgDecrease.startAnimation(animate)
					tvQuantity.startAnimation(animate)
				}
				tvQuantity.text = (data.quantity + 1).toString()
				updateDataList(
					position = position,
					item = data.copy(
						quantity = data.quantity + 1
					)
				)
				itemOnClick?.onClick(view = it, idViewClick = it.id, dataClicked = getItem(position))
			}
		}
	}
	
	override fun getViewBinding(viewGroup: ViewGroup) =
		LayoutItemOrderFoodBinding.inflate(LayoutInflater.from(context), viewGroup, false)
}