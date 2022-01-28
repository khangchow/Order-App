package com.foodorder.fooder.restaurantorder.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.OrderConfirmationActivity
import com.foodorder.fooder.restaurantorder.activities.OrderFoodActivity
import com.foodorder.fooder.restaurantorder.adapters.OrderListAdapter
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.FragmentHomeBinding
import com.foodorder.fooder.restaurantorder.fragments.base.BaseFragment
import com.foodorder.fooder.restaurantorder.models.Order
import com.foodorder.fooder.restaurantorder.utils.showCustomDialog
import com.foodorder.fooder.restaurantorder.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
	private lateinit var viewBinding: FragmentHomeBinding
	private val viewModel: HomeViewModel by viewModels()
	private var selectedId: Long = -1L

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		viewBinding = FragmentHomeBinding.inflate(inflater)
		viewBinding.lifecycleOwner = this
		return viewBinding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewBinding.apply {
			viewModel.table.observe(viewLifecycleOwner, {table ->
				Log.d("TABLE", table.toString())
				Log.d("TABLE", selectedId.toString())
				viewModel.changeTable(table.name, table.id, selectedId)
			})

			fabAction.setOnClickListener {

			}
			
			rvData.itemAnimator = DefaultItemAnimator()
			rvData.setHasFixedSize(true)
			rvData.layoutManager = GridLayoutManager(requireContext(), 1)
			val adapter = OrderListAdapter(
				context = requireContext(),
				dataList = listOf(),
				itemOnClick = object : ItemOnClick<Order> {
					override fun onClick(view: View?, idViewClick: Int?, dataClicked: Order?) {
						when (idViewClick) {
							R.id.btn_move_table -> {
								Log.d("ORDERS", viewModel.getEmptyTables().toString())
								requireActivity().showCustomDialog(
									dialogTitle = getString(R.string.change_table),
									positiveLabel = getString(R.string.accept),
									negativeLabel= getString(R.string.cancel_button),
									positiveClick = {
										selectedId = dataClicked!!.id

										viewModel.getTable(it)
									},
									showEditText = false,
									showDropDownMenu = true,
									dropDownList = viewModel.getEmptyTables()
								)
							}
							R.id.btn_order -> {
								if (dataClicked != null) {
									Log.d("ORDER", dataClicked.id.toString())
									startActivity(
										OrderFoodActivity.getIntent(requireContext(), dataClicked)
									)
								}
							}
							R.id.btn_payment -> {
								startActivity(dataClicked?.let {
									OrderConfirmationActivity.getIntent(
										requireContext(),
										it.id
									)
								})
							}
						}
					}
				}
			)
			rvData.adapter = adapter
		}
		
		viewModel.listOrderLive.observe(viewLifecycleOwner) {
			lifecycleScope.launch(Dispatchers.IO) {
				val listOrder = mutableListOf<Order>()

				it?.forEach { order ->
					if (order.id == 0L) {
						listOrder.add(order)
					} else {
						val listFood = viewModel.getOrderFoodListLive(order.id)
						listOrder.add(
							order.copy(listOrderFood = listFood)
						)
					}
				}

				viewModel.ordersList = listOrder



				withContext(Dispatchers.Main) {
					(viewBinding.rvData.adapter as OrderListAdapter).setListObject(listOrder)
				}
			}
		}
	}

	override fun onPause() {
		super.onPause()

		Log.d("LFC", "onPause: ")
	}

	override fun onResume() {
		super.onResume()

		Log.d("LFC", "onResume: ")
	}
}