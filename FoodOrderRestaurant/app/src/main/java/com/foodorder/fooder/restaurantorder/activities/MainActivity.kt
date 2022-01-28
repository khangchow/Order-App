package com.foodorder.fooder.restaurantorder.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.foodorder.fooder.restaurantorder.BuildConfig
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity
import com.foodorder.fooder.restaurantorder.databinding.ActivityMainBinding
import com.foodorder.fooder.restaurantorder.utils.Constants
import com.foodorder.fooder.restaurantorder.utils.PreferencesUtils
import com.foodorder.fooder.restaurantorder.utils.showAlertDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
	
	private lateinit var binding: ActivityMainBinding
	private lateinit var navController: NavController
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		navController = Navigation.findNavController(this, R.id.nav_host_fragment)
		NavigationUI.setupWithNavController(binding.navigationView, navController)
		binding.layoutToolbar.customToolbarBtn.visibility = View.VISIBLE
		binding.layoutToolbar.customToolbarBtn.setOnClickListener {
			binding.drawerLayout.openDrawer(GravityCompat.START)
		}
		binding.layoutToolbar.toolbar.title = getString(R.string.menu_main)
		binding.navigationView.setNavigationItemSelectedListener(this)
		checkAdRemote()
	}
	
	private fun checkAdRemote() {
		val remoteConfig = Firebase.remoteConfig
		val configSettings = remoteConfigSettings {
			minimumFetchIntervalInSeconds = 0
		}
		remoteConfig.setConfigSettingsAsync(configSettings)
		remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
			CoroutineScope(Dispatchers.IO).launch {
				val latestVersion = if (task.isSuccessful) {
					remoteConfig.getLong("version_app")
				} else {
					PreferencesUtils(this@MainActivity).getLatestVersionApp()
				}
				PreferencesUtils(this@MainActivity).setLatestVersionApp(latestVersion)
				delay(2000)
				if (latestVersion > BuildConfig.VERSION_CODE) {
					withContext(Dispatchers.Main) {
						showAlertDialog(
							alertTitle = getString(R.string.title_upgrade_app),
							alertMessage = getString(R.string.message_upgrade_app),
							positiveLabel = getString(R.string.button_upgrade),
							positiveClick = {
								val intent = Intent(Intent.ACTION_DIAL)
								intent.data = Uri.parse("tel:${Constants.PHONE_CONTACT}")
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
								startActivity(intent)
								finish()
							}
						)
					}
				}
			}
		}
	}
	
	companion object {
		fun getIntent(context: Context) = Intent(context, MainActivity::class.java)
	}
	
	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		binding.drawerLayout.closeDrawer(GravityCompat.START)
		binding.layoutToolbar.toolbar.title = item.title
		when (item.itemId) {
			R.id.menu_main -> {
				navController.navigate(R.id.fragment_main)
			}
			R.id.menu_food_list -> {
				navController.navigate(R.id.fragment_food_list)
			}
			R.id.menu_table_list -> {
				navController.navigate(R.id.fragment_table_list)
			}
			R.id.menu_history -> {
				navController.navigate(R.id.fragment_history)
			}
			R.id.menu_report -> {
				navController.navigate(R.id.fragment_report)
			}
			R.id.menu_payment -> {
				navController.navigate(R.id.fragment_report)
			}
			R.id.menu_printer -> {
				navController.navigate(R.id.fragment_printer)
			}
			R.id.menu_info -> {
				navController.navigate(R.id.fragment_info)
			}
			else -> {
				navController.navigate(R.id.fragment_main)
			}
		}
		return true
	}
}