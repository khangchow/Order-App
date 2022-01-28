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
import com.foodorder.fooder.restaurantorder.activities.TableAddedActivity
import com.foodorder.fooder.restaurantorder.adapters.TableListAdapter
import com.foodorder.fooder.restaurantorder.adapters.base.ItemOnClick
import com.foodorder.fooder.restaurantorder.databinding.FragmentTableListBinding
import com.foodorder.fooder.restaurantorder.fragments.base.BaseFragment
import com.foodorder.fooder.restaurantorder.models.Table
import com.foodorder.fooder.restaurantorder.models.convertTableList
import com.foodorder.fooder.restaurantorder.utils.showAlertDialog
import com.foodorder.fooder.restaurantorder.viewmodels.TableListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class TableListFragment : BaseFragment() {

    private lateinit var viewBinding: FragmentTableListBinding
    private val viewModel: TableListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTableListBinding.inflate(inflater)
        viewBinding.lifecycleOwner = this
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            fabAction.setOnClickListener {
                startActivity(TableAddedActivity.getIntent(requireContext()))
            }

            rvData.itemAnimator = DefaultItemAnimator()
            rvData.setHasFixedSize(true)
            rvData.layoutManager = GridLayoutManager(requireContext(), 2)
            val adapter = TableListAdapter(
                context = requireContext(),
                dataList = listOf(),
                itemOnClick = object : ItemOnClick<Table> {
                    override fun onClick(view: View?, idViewClick: Int?, dataClicked: Table?) {
                        when (idViewClick) {
                            R.id.btn_delete -> {
                                baseActivity.showAlertDialog(
                                    alertTitle = getString(R.string.delete_title_table),
                                    alertMessage = getString(R.string.delete_message_table),
                                    positiveLabel = getString(R.string.yes_button),
                                    positiveClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            viewModel.deleteTable(dataClicked)

                                            dataClicked?.imagePath?.let { path ->
                                                val imageFile = File(path)
                                                if (imageFile.exists()) {
                                                    imageFile.delete()
                                                }
                                            }

                                            withContext(Dispatchers.Main) {
                                                val adapter = rvData.adapter as TableListAdapter
                                                val newList =
                                                    adapter.dataList.toMutableList().apply {
                                                        remove(dataClicked)
                                                    }
                                                adapter.setListObject(newList)
                                                Toast.makeText(
                                                    requireContext(),
                                                    getString(R.string.delete_table_success),
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
                                    TableAddedActivity.getIntent(
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

        viewModel.getTableListLive().observe(viewLifecycleOwner) {
            (viewBinding.rvData.adapter as TableListAdapter).setListObject(it.convertTableList())
        }
    }
}