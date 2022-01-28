package com.foodorder.fooder.restaurantorder.activities

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		this.window.setFlags(
			WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
			WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
		)
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.decorView.windowInsetsController?.hide(
				android.view.WindowInsets.Type.navigationBars()
			)
		}
		
		window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
		val attrib = window.attributes
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			attrib.layoutInDisplayCutoutMode =
				WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
		}
		
		super.onCreate(savedInstanceState)
		setContentView(R.layout.splash_screen_activity)
		CoroutineScope(Dispatchers.IO).launch {
			delay(2000)
			startActivity(MainActivity.getIntent(this@SplashScreenActivity))
			finish()
		}
	}
	
}