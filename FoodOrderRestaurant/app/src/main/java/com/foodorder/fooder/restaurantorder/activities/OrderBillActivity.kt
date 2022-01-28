package com.foodorder.fooder.restaurantorder.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.adapters.OrderBillAdapter
import com.foodorder.fooder.restaurantorder.databinding.ActivityOrderBillBinding
import com.foodorder.fooder.restaurantorder.models.calculateTotalAmount
import com.foodorder.fooder.restaurantorder.models.convertOrderFoodList
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.utils.Utils
import com.foodorder.fooder.restaurantorder.viewmodels.OrderBillViewModel
import com.foodorder.fooder.restaurantorder.views.FormatTextWatcher
import com.foodorder.fooder.restaurantorder.views.OnTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.i_edit_text_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class OrderBillActivity : BaseActivity() {

    private lateinit var binding: ActivityOrderBillBinding
	private val viewModel: OrderBillViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getLongExtra(ORDER_ID, -1L)

        if (id == -1L) {
            finish()
            return
        }

        binding.apply {
            viewModel.list.observe(this@OrderBillActivity, { list ->
                val foods = list.convertOrderFoodList()
                Log.d("FOODS", foods.toString())

                (rvData.adapter as OrderBillAdapter).setListObject(foods)

                totalbill.text = foods.calculateTotalAmount()
            })

            viewModel.getOrderFoodListLive(id)

            toolbarCustom.setBackOnClick {
                finish()
            }

            btnApply.setOnClickListener{
                viewModel.finishOrder(id)

                finish()
            }

            receive.setEndTextView(Constants.CURRENCY_CODE_DEFAULT)

            rvData.itemAnimator = DefaultItemAnimator()
            rvData.setHasFixedSize(true)
            rvData.layoutManager = LinearLayoutManager(this@OrderBillActivity)
            val adapter = OrderBillAdapter(
                context = this@OrderBillActivity,
                dataList = listOf(),
            )
            rvData.adapter = adapter

            receive.addTextChangedListener(FormatTextWatcher(
                receive.getEditView(),
                Constants.MAX_LENGTH_AMOUNT,
                object : OnTextChangedListener {
                    override fun afterTextChanged(view: View, s: Editable?) {
                        if (s.toString() != "") {
                            change.text =  Utils.convertLongtoAmount(
                                Utils.convertAmountToLong(s.toString())
                                        - Utils.convertAmountToLong(totalbill.text.toString()
                                    .substring(1)
                                ), Constants.CURRENCY_CODE_DEFAULT
                            )
    
                            btnApply.isEnabled =
                                change.text.toString().substring(0, 1) != "-"
                        } else {
                            change.text = ""
                        }
                    }
                })
            )
        }
    }

    companion object {
        private const val ORDER_ID = "OrderId"

        fun getIntent(context: Context, id: Long): Intent {
            val intent = Intent(context, OrderBillActivity::class.java)
            intent.putExtra(ORDER_ID, id)
            return intent
        }
    }
}