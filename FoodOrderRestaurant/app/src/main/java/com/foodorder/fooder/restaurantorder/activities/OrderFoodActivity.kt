package com.foodorder.fooder.restaurantorder.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.adapters.OrderFoodListAdapter
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.ActivityOrderFoodBinding
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.models.Order
import com.foodorder.fooder.restaurantorder.models.calculateTotalAmount
import com.foodorder.fooder.restaurantorder.models.convertFoodList
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.viewmodels.OrderFoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_custom_dialog.*
import kotlinx.android.synthetic.main.layout_item_order_bill.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class OrderFoodActivity : BaseActivity() {
	
	private lateinit var binding: ActivityOrderFoodBinding
	private val viewModel: OrderFoodViewModel by viewModels()
	private val durationTime = 500L
	
	@SuppressLint("SetTextI18n")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityOrderFoodBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		viewModel.orderDraftLive.observe(this) { list ->
			Log.d("QUANTITY", "LIVEDATA:"+list.toString())
			val sumList = list.sumOf { it.quantity }
			// animation
			when {
				sumList == 0 && binding.clBottomOrder.visibility == View.VISIBLE -> {
					val animate = TranslateAnimation(
						0f,  // fromXDelta
						0f,  // toXDelta
						0f,  // fromYDelta
						binding.viewBottomOrder.height.toFloat()
					) // toYDelta
					animate.duration = durationTime
					binding.clBottomOrder.startAnimation(animate)
					binding.clBottomOrder.visibility = View.GONE
				}
				sumList == 1 && binding.clBottomOrder.visibility == View.GONE -> {
					binding.clBottomOrder.visibility = View.VISIBLE
					val animate = TranslateAnimation(
						0f,  // fromXDelta
						0f,  // toXDelta
						binding.viewBottomOrder.height.toFloat(),  // fromYDelta
						0f
					) // toYDelta
					animate.duration = durationTime
					binding.clBottomOrder.startAnimation(animate)
				}
			}
			binding.apply {
				tvSum.text = list.calculateTotalAmount()
				tvNumberCart.text = if (sumList > Constants.MAX_NUMBER)
					Constants.MAX_VALUE_NUMBER else sumList.toString()
			}
		}
		
		val data = intent.getParcelableExtra<Order>(FOOD_FOLDER)
		if (data == null) {
			finish()
			return
		} else {
			viewModel.order = data
		}
		
		binding.apply {
			toolbarCustom.setBackOnClick {
				finish()
			}
			rvData.itemAnimator = DefaultItemAnimator()
			rvData.setHasFixedSize(true)
			rvData.layoutManager = GridLayoutManager(this@OrderFoodActivity, 1)
			val adapter = OrderFoodListAdapter(
				context = this@OrderFoodActivity,
				dataList = listOf(),
				itemOnClick = object : ItemOnClick<Food> {
					override fun onClick(view: View?, idViewClick: Int?, dataClicked: Food?) {
						when (idViewClick) {
							R.id.img_decrease, R.id.img_addition -> {
								viewModel.orderDraftLive.value =
									(rvData.adapter as OrderFoodListAdapter).dataList
							}
							R.id.tv_quantity -> {
								viewModel.orderDraftLive.value =
									(rvData.adapter as OrderFoodListAdapter).dataList
							}
						}
					}
				}
			)
			rvData.adapter = adapter
			
			btnOrder.setOnClickListener {
				showProgressDialog(cancelAble = false)
				lifecycleScope.launch(Dispatchers.IO) {
					val listFoodOrder = viewModel.getOrderFoodListLive()
					viewModel.orderDraftLive.value?.forEach { orderDraft ->
						val orderFood = listFoodOrder.find { food ->
							food.id == orderDraft.id
						}

						val foodUpdated = orderFood?.copy(
							quantity = orderFood.quantity + orderDraft.quantity
						) ?: orderDraft

						val idOrder = viewModel.insertOrReplaceOrder()

						viewModel.order = viewModel.order.copy(
							id = idOrder
						)

						Log.d("FOOD", foodUpdated.name+" "
								+orderFood?.quantity.toString() +" "+orderDraft.quantity.toString()
								+" "+foodUpdated.quantity.toString())

						viewModel.insertOrReplaceOrderFood(foodUpdated)
						
						withContext(Dispatchers.Main) {
							hideProgressDialog()
							Toast.makeText(
								this@OrderFoodActivity,
								getString(R.string.order_food_success),
								Toast.LENGTH_SHORT
							).show()
							finish()
						}
					}
				}
			}
			imgCart.setOnClickListener {
//				startActivity(
//					OrderConfirmationActivity.getIntent(
//						this@OrderFoodActivity,
//						viewModel.order.id,
//					)
//				)
			}
		}
		
		viewModel.getFoodListLive().observe(this) {
			((binding.rvData.adapter as OrderFoodListAdapter).setListObject(it.convertFoodList()))
		}
	}
	
	companion object {
		private const val FOOD_FOLDER = "OrderFoodActivity"
		
		fun getIntent(context: Context, order: Order): Intent {
			val intent = Intent(context, OrderFoodActivity::class.java)
			intent.putExtra(FOOD_FOLDER, order)
			return intent
		}
	}
}