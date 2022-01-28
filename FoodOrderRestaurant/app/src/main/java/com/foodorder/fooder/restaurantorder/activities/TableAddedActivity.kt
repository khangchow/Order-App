package com.foodorder.fooder.restaurantorder.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.databinding.ActivityTableAddedBinding
import com.foodorder.fooder.restaurantorder.models.Table
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.utils.FileUtils
import com.foodorder.fooder.restaurantorder.viewmodels.TableListAddedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class TableAddedActivity : BaseActivity() {

    private var defaultTable: Table? = null
    private lateinit var binding: ActivityTableAddedBinding
    private val viewModel: TableListAddedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTableAddedBinding.inflate(layoutInflater)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        initDefaultData()
    }

    private val resultImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.getStringExtra(PictureTakeActivity.RESULT_PICTURE_IMAGE_PATH) ?: ""
                viewModel.tableItemLiveData.value = viewModel.tableItemLiveData.value?.copy(
                    imagePath = data
                )
                Glide.with(this@TableAddedActivity)
                    .load(viewModel.tableItemLiveData.value?.imagePath)
                    .error(R.drawable.ic_icon)
                    .into(binding.imgTable)
            }
        }

    private fun initDefaultData() {
        defaultTable = intent.getParcelableExtra(ITEM_DATA_KEY) ?: Table()
        viewModel.tableItemLiveData.value = defaultTable
        binding.apply {
            Glide.with(this@TableAddedActivity)
                .load(viewModel.tableItemLiveData.value?.imagePath)
                .error(R.drawable.ic_icon)
                .into(binding.imgTable)
            toolbarCustom.setBackOnClick {
                finish()
            }
            imgTable.setOnClickListener {
                resultImageLauncher.launch(
                    PictureTakeActivity.getIntent(
                        this@TableAddedActivity,
                        viewModel.tableItemLiveData.value?.imagePath
                    )
                )
            }
            if (defaultTable == null || defaultTable == Table()) {
                toolbarCustom.setTitleBar(getString(R.string.add_table))
                btnApply.text = getString(R.string.add_new)
            } else {
                toolbarCustom.setTitleBar(getString(R.string.edit_table))
                btnApply.text = getString(R.string.update)
            }
            etTableName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    viewModel.tableItemLiveData.value = viewModel.tableItemLiveData.value?.copy(
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
                    viewModel.tableItemLiveData.value = viewModel.tableItemLiveData.value?.copy(
                        status = s?.toString() ?: Constants.EMPTY
                    )
                }

            })
            btnApply.setOnClickListener {
                showProgressDialog(cancelAble = false)
                CoroutineScope(Dispatchers.IO).launch {
                    var outputPath: String? = null
                    viewModel.tableItemLiveData.value?.imagePath?.let { currentPath ->
                        if (currentPath.isNotEmpty() && currentPath != defaultTable?.imagePath) {
                            val bitmap = FileUtils.getBitmapImageFromUri(
                                this@TableAddedActivity,
                                Uri.parse(currentPath)
                            )
                            outputPath = FileUtils.saveToInternalStorage(
                                this@TableAddedActivity,
                                bitmap,
                                TABLE_FOLDER
                            )
                            if (outputPath?.isNotEmpty() == true && defaultTable?.imagePath?.isNotEmpty() == true) {
                                File(defaultTable!!.imagePath).delete()
                            }
                        }
                    }

                    viewModel.insertOrUpdateTableItem(
                        viewModel.tableItemLiveData.value?.copy(
                            imagePath = outputPath ?: defaultTable?.imagePath ?: ""
                        )
                    )
                    withContext(Dispatchers.Main) {
                        showProgressDialog(cancelAble = false)
                        Toast.makeText(
                            this@TableAddedActivity,
                            getString(
                                if (defaultTable == null || defaultTable == Table()) R.string.create_new_success
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
        private const val TABLE_FOLDER = "TableImage"
        private const val ITEM_DATA_KEY = "ITEM_DATA_KEY"

        fun getIntent(context: Context, item: Table? = null): Intent {
            val intent = Intent(context, TableAddedActivity::class.java)
            if (item != null) intent.putExtra(ITEM_DATA_KEY, item)
            return intent
        }
    }
}