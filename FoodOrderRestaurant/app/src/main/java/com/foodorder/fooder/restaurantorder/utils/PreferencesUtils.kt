package com.foodorder.fooder.restaurantorder.utils

import android.content.Context
import android.content.SharedPreferences

data class PreferencesUtils(val context: Context) {
	
	companion object {
		private const val PREF_NAME = "OrderFood"
		private const val EMPTY = ""
		private const val SAMPLE_KEY = "SAMPLE_KEY"
		private const val LATEST_VERSION_KEY = "LATEST_VERSION_KEY"
		private const val CURRENCY_CODE_KEY = "CURRENCY_CODE_KEY"
		private const val DEFAULT_VERSION_KEY = 1L
	}
	
	private var sharedPreference: SharedPreferences =
		context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
	private var editor: SharedPreferences.Editor = sharedPreference.edit()
	
	// sample
	fun setDataSample(data: String) = editor.putString(SAMPLE_KEY, data).apply()
	
	fun getDataSample(): String {
		return sharedPreference.getString(SAMPLE_KEY, EMPTY) ?: EMPTY
	}
	
	// do something
	fun setLatestVersionApp(version: Long) = editor.putLong(LATEST_VERSION_KEY, version).apply()
	
	fun getLatestVersionApp(): Long {
		return sharedPreference.getLong(LATEST_VERSION_KEY, DEFAULT_VERSION_KEY)
	}
	
	fun setCurrencyCode(version: String) = editor.putString(LATEST_VERSION_KEY, version).apply()
	
	fun getCurrencyCode(): String {
		return sharedPreference.getString(CURRENCY_CODE_KEY, Constants.CURRENCY_CODE_DEFAULT)
			?: Constants.CURRENCY_CODE_DEFAULT
	}
}