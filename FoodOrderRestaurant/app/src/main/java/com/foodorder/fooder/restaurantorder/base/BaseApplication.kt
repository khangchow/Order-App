package com.foodorder.fooder.restaurantorder.base

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {
	
	override fun onCreate() {
		super.onCreate()
	}
}