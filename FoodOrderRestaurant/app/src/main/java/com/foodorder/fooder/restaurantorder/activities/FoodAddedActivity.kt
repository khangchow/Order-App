package com.foodorder.fooder.restaurantorder.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.databinding.ActivityFoodAddedBinding
import com.foodorder.fooder.restaurantorder.models.Food
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.utils.FileUtils
import com.foodorder.fooder.restaurantorder.viewmodels.FoodListAddedViewModel
import com.foodorder.fooder.restaurantorder.views.FormatTextWatcher
import com.foodorder.fooder.restaurantorder.views.OnTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class FoodAddedActivity : BaseActivity() {

    private var defaultFood: Food? = null
    private lateinit var binding: ActivityFoodAddedBinding
    private val viewModel: FoodListAddedViewModel by viewModels()

    private val resultImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.getStringExtra(PictureTakeActivity.RESULT_PICTURE_IMAGE_PATH) ?: ""
                viewModel.foodItemLiveData.value = viewModel.foodItemLiveData.value?.copy(
                    imagePath = data
                )
                Glide.with(this@FoodAddedActivity)
                    .load(viewModel.foodItemLiveData.value?.imagePath)
                    .error(R.drawable.ic_icon)
                    .into(binding.imgFood)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoodAddedBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        initDefaultData()
    }

    private fun initDefaultData() {
        defaultFood = intent.getParcelableExtra(ITEM_DATA_KEY) ?: Food()
        viewModel.foodItemLiveData.value = defaultFood
        binding.apply {
            toolbarCustom.setBackOnClick {
                finish()
            }
            imgFood.setOnClickListener {
                resultImageLauncher.launch(
                    PictureTakeActivity.getIntent(
                        this@FoodAddedActivity,
                        viewModel.foodItemLiveData.value?.imagePath
                    )
                )
            }
            if (defaultFood == null || defaultFood == Food()) {
                toolbarCustom.setTitleBar(getString(R.string.title_add_food))
                btnApply.text = getString(R.string.add_new)
            } else {
                toolbarCustom.setTitleBar(getString(R.string.edit_food))
                btnApply.text = getString(R.string.update)
            }

            Glide.with(this@FoodAddedActivity)
                .load(viewModel.foodItemLiveData.value?.imagePath)
                .error(R.drawable.ic_icon)
                .into(imgFood)

            etFoodName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    viewModel.foodItemLiveData.value = viewModel.foodItemLiveData.value?.copy(
                        name = s?.toString() ?: Constants.EMPTY
                    )
                }

            })
            etStatus.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    viewModel.foodItemLiveData.value = viewModel.foodItemLiveData.value?.copy(
                        status = s?.toString() ?: Constants.EMPTY
                    )
                }

            })
            etPrice.addTextChangedListener(
                FormatTextWatcher(
                    etPrice.getEditView(),
                    Constants.MAX_LENGTH_AMOUNT,
                    object : OnTextChangedListener {
                        override fun afterTextChanged(view: View, s: Editable?) {
                            viewModel.foodItemLiveData.value =
                                viewModel.foodItemLiveData.value?.copy(
                                    price = s?.toString() ?: Constants.EMPTY
                                )
                        }
                    })
            )
            btnApply.setOnClickListener {
                showProgressDialog(cancelAble = false)
                CoroutineScope(Dispatchers.IO).launch {
                    var outputPath: String? = null
                    viewModel.foodItemLiveData.value?.imagePath?.let { currentPath ->
                        if (currentPath.isNotEmpty() && currentPath != defaultFood?.imagePath) {
                            val bitmap = FileUtils.getBitmapImageFromUri(
                                this@FoodAddedActivity,
                                Uri.parse(currentPath)
                            )
                            outputPath = FileUtils.saveToInternalStorage(
                                this@FoodAddedActivity,
                                bitmap,
                                FOOD_FOLDER
                            )
                            if (outputPath?.isNotEmpty() == true && defaultFood?.imagePath?.isNotEmpty() == true) {
                                File(defaultFood!!.imagePath).delete()
                            }
                        }
                    }

                    viewModel.insertOrUpdateFoodItem(
                        viewModel.foodItemLiveData.value?.copy(
                            imagePath = outputPath ?: defaultFood?.imagePath ?: ""
                        )
                    )
                    withContext(Dispatchers.Main) {
                        showProgressDialog(cancelAble = false)
                        Toast.makeText(
                            this@FoodAddedActivity,
                            getString(
                                if (defaultFood == null || defaultFood == Food()) R.string.create_new_success
                                else R.string.update_success
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        private const val FOOD_FOLDER = "FoodImage"
        private const val ITEM_DATA_KEY = "ITEM_DATA_KEY"

        fun getIntent(context: Context, item: Food? = null): Intent {
            val intent = Intent(context, FoodAddedActivity::class.java)
            if (item != null) intent.putExtra(ITEM_DATA_KEY, item)
            return intent
        }
    }
}