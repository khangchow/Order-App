package com.foodorder.fooder.restaurantorder.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.foodorder.fooder.restaurantorder.views.NumberTextWatcher
import java.text.NumberFormat
import java.util.*
import com.github.mikephil.charting.utils.FSize

import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils.getSizeOfRotatedRectangleByDegrees


object Utils {

	private val mDrawTextRectBuffer: Rect = Rect()
	private val mFontMetricsBuffer = Paint.FontMetrics()

	fun convertLongtoAmount(long: Float, currencyCode: String): String {
		val currency = Currency.getInstance(currencyCode)
		val format = NumberFormat.getCurrencyInstance()
		format.currency = currency
		format.maximumFractionDigits = currency.defaultFractionDigits

//	val amount = if (currency.defaultFractionDigits == 0)
//		totalAmount.toLong().toString() else totalAmount.toString()
//	val formatAmount = amount.
		return format.format(long)
	}
	
	fun convertAmountToLong(amount: String): Float {
		val formatAmount = amount.replace(NumberTextWatcher.GROUP_SEPARATOR.toString(), "")
		return formatAmount.toFloatOrNull() ?: 0f
	}
	
	fun openMarket(context: Context) {
		val appPackageName: String = context.packageName
		try {
			context.startActivity(
				Intent(
					"android.intent.action.VIEW",
					Uri.parse("market://details?id=$appPackageName")
				)
			)
		} catch (e: ActivityNotFoundException) {
			context.startActivity(
				Intent(
					"android.intent.action.VIEW",
					Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
				)
			)
		}
	}
	
	fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
		val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val services = activityManager.getRunningServices(Int.MAX_VALUE)
		for (runningServiceInfo in services) {
			if (runningServiceInfo.service.className == serviceClass.name) {
				return true
			}
		}
		return false
	}
	
	
	fun hideSoftKeyboard(activity: Activity) {
		val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		//Find the currently focused view, so we can grab the correct window token from it.
		var view = activity.currentFocus
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = View(activity)
		}
		imm.hideSoftInputFromWindow(view.windowToken, 0)
		view.clearFocus()
	}
	
	fun hideSoftKeyboard(context: Context) {
		getActivityFromContext(context)?.let {
			hideSoftKeyboard(it)
		}
	}
	
	fun hideSoftKeyboard(view: View) {
		val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.hideSoftInputFromWindow(view.windowToken, 0)
	}
	
	fun showSoftKeyboard(context: Context, view: View?) {
		val act = getActivityFromContext(context)
		if (act == null || act.isFinishing
			|| view == null
		) {
			return
		}
		val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
		if (imm != null) {
			view.requestFocus()
			imm.showSoftInput(view, 0)
		}
	}
	
	private fun getActivityFromContext(context: Context): Activity? {
		var contextActivity: Context? = context
		var act: Activity? = null
		if (contextActivity is Activity) {
			act = contextActivity
		} else {
			while (contextActivity is ContextWrapper) {
				if (contextActivity is Activity) {
					act = contextActivity
					break
				}
				contextActivity = contextActivity.baseContext
			}
		}
		return act
	}
}