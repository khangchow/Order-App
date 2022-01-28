package com.foodorder.fooder.restaurantorder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.FoodAddedActivity
import com.foodorder.fooder.restaurantorder.adapters.FoodListAdapter
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.FragmentFoodListBinding
import com.foodorder.fooder.restaurantorder.fragments.base.BaseFragment
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.models.convertFoodList
import com.foodorder.fooder.restaurantorder.utils.showAlertDialog
import com.foodorder.fooder.restaurantorder.viewmodels.FoodListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class FoodListFragment : BaseFragment() {

    private lateinit var viewBinding: FragmentFoodListBinding
    private val viewModel: FoodListViewModel by viewModels()

    override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
        viewBinding = FragmentFoodListBinding.inflate(inflater)
        viewBinding.lifecycleOwner = this
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            fabAction.setOnClickListener {
                startActivity(FoodAddedActivity.getIntent(requireContext()))
            }

            rvData.itemAnimator = DefaultItemAnimator()
            rvData.setHasFixedSize(true)
            rvData.layoutManager = GridLayoutManager(requireContext(), 2)
            val adapter = FoodListAdapter(
	            context = requireContext(),
	            dataList = listOf(),
	            itemOnClick = object : ItemOnClick<Food> {
		            override fun onClick(view: View?, idViewClick: Int?, dataClicked: Food?) {
			            when (idViewClick) {
				            R.id.btn_delete -> {
					            baseActivity.showAlertDialog(
						            alertTitle = getString(R.string.delete_title_food),
						            alertMessage = getString(R.string.delete_message_food),
						            positiveLabel = getString(R.string.yes_button),
						            positiveClick = {
							            // todo check nếu như có bàn chưa thanh toán mà có món ăn đó thì không cho xóa. chỉ cho xóa khi không có bàn nào chưa thanh toán dùng món ăn đó
							            CoroutineScope(Dispatchers.IO).launch {
								            // delete image of food
								            dataClicked?.imagePath?.let { path ->
									            val imageFile = File(path)
									            if (imageFile.exists()) {
										            imageFile.delete()
									            }
								            }
								            viewModel.deleteFoodItem(dataClicked)
								            withContext(Dispatchers.Main) {
									            val adapter = rvData.adapter as FoodListAdapter
									            val newList =
										            adapter.dataList.toMutableList().apply {
											            remove(dataClicked)
										            }
									            adapter.setListObject(newList)
									            Toast.makeText(
										            requireContext(),
										            getString(R.string.delete_food_success),
										            Toast.LENGTH_SHORT
									            ).show()
								            }
							            }
						            },
						            negativeLabel = getString(R.string.cancel_button)
					            )
				            }
				            R.id.btn_edit -> {
					            startActivity(
						            FoodAddedActivity.getIntent(
							            context = requireContext(),
							            item = dataClicked
						            )
					            )
				            }
			            }
		            }
		
	            }
            )
            rvData.adapter = adapter
        }

        viewModel.getFoodListLive().observe(viewLifecycleOwner) {
	        (viewBinding.rvData.adapter as FoodListAdapter).setListObject(it.convertFoodList())
        }
    }
}