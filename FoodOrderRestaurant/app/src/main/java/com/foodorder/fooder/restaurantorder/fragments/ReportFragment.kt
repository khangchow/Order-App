package com.foodorder.fooder.restaurantorder.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.databinding.FragmentReportBinding
import com.foodorder.fooder.restaurantorder.fragments.base.BaseFragment
import com.foodorder.fooder.restaurantorder.models.RevenueInfo
import com.foodorder.fooder.restaurantorder.models.convertToBarEntryList
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.utils.DateUtils
import com.foodorder.fooder.restaurantorder.viewmodels.ReportViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.util.*

@AndroidEntryPoint
class ReportFragment : BaseFragment() {
    private lateinit var viewBinding: FragmentReportBinding
    private val viewAnimDuration = 200L
    private val chartAnimDuration = 1000L
    private var year = 0
    private var month = 0
    private var day = 0
    private lateinit var timeStart: Calendar
    private lateinit var timeEnd: Calendar
    private var barEntries = listOf<BarEntry>()
    private var revenueInfo = listOf<RevenueInfo>()
    private val viewModel: ReportViewModel by viewModels()
    private var xAxisValues: MutableList<String> = mutableListOf()
    private lateinit var barDataSet: BarDataSet
    private lateinit var barData: BarData
    private var firstPicker = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentReportBinding.inflate(inflater)
        return viewBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.list.observe(viewLifecycleOwner,  {list ->
            revenueInfo = list

            xAxisValues.clear()

            xAxisValues.add("")

            revenueInfo.map {
                xAxisValues.add(it.barName)
            }

            Log.d("ReportInfo", xAxisValues.toString())

            barEntries = revenueInfo.convertToBarEntryList()

            Log.d("ReportInfo", barEntries.toString())

            setChartData()
        })

        viewBinding.apply {
            btnAction.setOnClickListener {
                timeStart = DateUtils.convertStringToTime(tvDateStart.text.toString())

                timeStart.set(Calendar.MONTH, timeStart.get(Calendar.MONTH) - 1)

                timeEnd = DateUtils.convertStringToTime(tvDateEnd.text.toString())

                timeEnd.set(Calendar.DAY_OF_MONTH, timeEnd.get(Calendar.DAY_OF_MONTH) + 1)

                timeEnd.set(Calendar.MONTH, timeEnd.get(Calendar.MONTH) - 1)

                viewModel.filterByDay(timeStart, timeEnd)
            }

            tvDate.text = DateUtils.convertDatetoString(Date())

            tvDateStart.text = DateUtils.convertDatetoString(Date())

            tvDateEnd.text = DateUtils.convertDatetoString(Date())

            val filter = resources.getStringArray(R.array.filter).toList()

            val arrayAdapter = ArrayAdapter(requireContext()
                , android.R.layout.simple_spinner_dropdown_item
                , filter)


            tvDate.setOnClickListener{
                showDatePicker(it as TextView)
            }

            tvDateStart.setOnClickListener{
                showDatePicker(it as TextView)
            }

            tvDateEnd.setOnClickListener{
                showDatePicker(it as TextView)
            }

            tvFilter.setText(filter[0])

            tvFilter.setAdapter(arrayAdapter)

            tvFilter.setOnItemClickListener { adapterView, view, i, l ->
                if (i == 0) {
                    if (viewCustom.visibility == View.VISIBLE) viewCustom.visibility = View.GONE

                    startAppearAnim(viewDate, tvDate)

                    prepareChartData(Constants.FILTER_BY_DATE)
                }else {
//                    val anim = TranslateAnimation(
//                        0f,
//                        tvDate.width.toFloat() * -1,
//                        0f,
//                        0f
//                    )
//
//                    anim.duration = viewAnimDuration
//                    anim.setAnimationListener(object: Animation.AnimationListener {
//                        override fun onAnimationRepeat(animation: Animation?) {
//                        }
//
//                        override fun onAnimationEnd(animation: Animation?) {
//                            viewDate.visibility = View.GONE
//                        }
//
//                        override fun onAnimationStart(animation: Animation?) {
//                        }
//
//                    })
//
//                    tvDate.startAnimation(anim)

                    viewDate.visibility = View.GONE
                    viewCustom.visibility = View.GONE

                    when (i) {
                        1-> {
                            prepareChartData(Constants.FILTER_BY_WEEK)
                        }

                        2-> {
                            prepareChartData(Constants.FILTER_BY_MONTH)
                        }

                        3-> {
                            startAppearAnim(viewCustom, tvDateStart, tvDateEnd)
                        }
                    }
                }
            }
        }

        setUpChart()

        prepareChartData(Constants.FILTER_BY_DATE)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDatePicker(tv: TextView) {
        if (firstPicker) {
            val current = DateUtils.current()

            day = current[Calendar.DAY_OF_MONTH]
            month = current[Calendar.MONTH]
            year = current[Calendar.YEAR]

            firstPicker = false
        }else {
            val date = viewBinding.tvDate.text.split("/")

            day = date[0].toInt()
            month = date[1].toInt()-1
            year = date[2].toInt()
        }

        val listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            timeStart = DateUtils.current()
            timeEnd = DateUtils.current()

            timeStart.set(i, i2, i3, 0, 0, 0)
            timeEnd.set(i, i2, i3 + 1, 0, 0, 0)

            Log.d("ReportInfo", DateUtils.convertLongToDateTime(timeStart.timeInMillis))
            Log.d("ReportInfo", DateUtils.convertLongToDateTime(timeEnd.timeInMillis))

            if (viewBinding.viewDate.visibility == View.VISIBLE)
                viewModel.filterByOrder(timeStart.timeInMillis, timeEnd.timeInMillis)

            tv.text = DateUtils.convertCalendartoString(timeStart)
        }

        val dialog = DatePickerDialog(requireContext(), listener, year, month, day)

        dialog.show()
    }

    private fun setUpChart() {
        viewBinding.apply {
            chart.setFitBars(true)

            chart.axisLeft.setDrawLabels(true)
            chart.axisRight.setDrawLabels(false)
            chart.xAxis.textSize = 15f
            chart.setExtraOffsets(0f, 0f, 0f, 10f)
            chart.description.isEnabled = false;
            chart.legend.isEnabled = false;
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        }
    }

    private fun setChartData() {
        barDataSet = BarDataSet(barEntries, "RevenueInfo")

        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()

        barDataSet.valueTextSize = 15f

        barData = BarData(barDataSet)

        viewBinding.apply {
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)

            chart.data = barData

            chart.setVisibleXRange(1f, 5f)

            chart.xAxis.granularity = 1f

            chart.notifyDataSetChanged()

            chart.invalidate()

            if (barEntries.isNotEmpty()) {
                var last: Int = barEntries.size - 1

                for (i in barEntries.size - 2 downTo 0 step 1) {
                    if (barEntries[i].y == 0f) {
                        last = i + 1

                        break
                    }
                }

                chart.moveViewToAnimated(
                    last.toFloat(),
                    barEntries[last].y,
                    YAxis.AxisDependency.LEFT,
                    chartAnimDuration
                )

                chart.animateXY(chartAnimDuration.toInt(), chartAnimDuration.toInt())
            }
        }
    }

    private fun prepareChartData(filterType: Int) {
        val timeStart = DateUtils.current()
        val timeEnd = DateUtils.current()

        timeStart.set(timeStart.get(Calendar.YEAR), timeStart.get(Calendar.MONTH), timeStart.get(Calendar.DAY_OF_MONTH) - filterType!! + 1,0, 0, 0)
        timeEnd.set(timeEnd.get(Calendar.YEAR), timeEnd.get(Calendar.MONTH), timeEnd.get(Calendar.DAY_OF_MONTH) + 1,0, 0, 0)

        Log.d("ReportInfo", DateUtils.convertLongToDateTime(timeStart.timeInMillis))
        Log.d("ReportInfo", DateUtils.convertLongToDateTime(timeEnd.timeInMillis))

        if (filterType == Constants.FILTER_BY_DATE) viewModel.filterByOrder(timeStart.timeInMillis, timeEnd.timeInMillis)
        else viewModel.filterByDay(timeStart, timeEnd)
    }

    private fun startAppearAnim(viewGroup: View, view1: View, view2: View? = null) {
        viewGroup.visibility = View.VISIBLE

        val anim = TranslateAnimation(
            viewBinding.tvFilter.width.toFloat() * -1,
            0f,
            0f,
            0f
        )

        anim.duration = viewAnimDuration

        view1.startAnimation(anim)

        view2?.startAnimation(anim)
    }
}