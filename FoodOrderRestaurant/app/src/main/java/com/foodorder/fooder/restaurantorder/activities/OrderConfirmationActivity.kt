package com.foodorder.fooder.restaurantorder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.adapters.OrderFoodListAdapter
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.ActivityOrderConfirmationBinding
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.utils.Logger
import com.foodorder.fooder.restaurantorder.viewmodels.OrderConfirmationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class OrderConfirmationActivity : BaseActivity() {
	
	private lateinit var binding: ActivityOrderConfirmationBinding
	private val viewModel: OrderConfirmationViewModel by viewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		initAddedData()
		
	}
	
	private fun initAddedData() {
		val idOrder = intent.getLongExtra(ORDER_ID_ADDED, 0)
		binding.apply {
			toolbarCustom.setBackOnClick{
				finish()
			}
			rvDataHistory.itemAnimator = DefaultItemAnimator()
			rvDataHistory.setHasFixedSize(true)
			rvDataHistory.layoutManager =
				GridLayoutManager(this@OrderConfirmationActivity, 1)
			val adapter = OrderFoodListAdapter(
				context = this@OrderConfirmationActivity,
				dataList = listOf(),
				itemOnClick = object : ItemOnClick<Food> {
					override fun onClick(view: View?, idViewClick: Int?, dataClicked: Food?) {
						when (idViewClick) {
							R.id.img_decrease, R.id.img_addition -> {
								dataClicked?.let {
									lifecycleScope.launch {
										viewModel.insertOrReplaceOrderFood(idOrder, it)
									}
								}
							}
						}
					}
				}
			)
			rvDataHistory.adapter = adapter
			
			btnPaymentNow.setOnClickListener {
				startActivity(
					OrderBillActivity.getIntent(
						this@OrderConfirmationActivity,
						idOrder
					)
				)
				finish()
			}
		}
		
		lifecycleScope.launch {
			Logger.e("listdata -----------")
			val listData = viewModel.getOrderFoodListLive(idOrder).filter {
				it.quantity > 0
			}
			Logger.e("listdata - ${listData.size}")
			withContext(Dispatchers.Main) {
				(binding.rvDataHistory.adapter as OrderFoodListAdapter).setListObject(listData)
			}
		}
	}
	
	companion object {
		private const val ORDER_ID_ADDED = "ORDER_ID_ADDED"
		
		fun getIntent(context: Context, idOrder: Long): Intent {
			val intent = Intent(context, OrderConfirmationActivity::class.java)
			intent.putExtra(ORDER_ID_ADDED, idOrder)
			return intent
		}
	}
	
}