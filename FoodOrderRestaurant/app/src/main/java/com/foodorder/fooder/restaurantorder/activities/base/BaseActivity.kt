package com.foodorder.fooder.restaurantorder.activities.base

import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.foodorder.fooder.restaurantorder.base.BaseApplication
import com.foodorder.fooder.restaurantorder.utils.Logger
import com.foodorder.fooder.restaurantorder.utils.Utils
import com.foodorder.fooder.restaurantorder.views.ClearableEditText
import com.foodorder.fooder.restaurantorder.views.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    
    lateinit var baseApplication: BaseApplication
    private var loadingDialog: LoadingDialog? = null
    open var isLightStatusBar: Boolean? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        
        baseApplication = application as BaseApplication
    }
    
    fun showProgressDialog(cancelAble: Boolean = true, onCancel: () -> Unit = {}) {
        if(loadingDialog == null) {
            synchronized(this.javaClass) {
                loadingDialog = LoadingDialog(this)
            }
        }
        loadingDialog?.apply {
            setCancelable(cancelAble)
//            setCanceledOnTouchOutside(cancelAble)
            setOnCancelListener {
                onCancel.invoke()
            }
            show()
        }
    }
    
    fun hideProgressDialog() {
        loadingDialog?.let {
            if (it.isShowing) it.dismiss()
        }
        if(isLightStatusBar == false) {
            clearLightStatusBar()
        } else {
            setLightStatusBar()
        }
    }
    
    /**
     * set status bar to light mode => icon color is grey.
     */
    protected open fun setLightStatusBar() {
        if (isLightStatusBar == true) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // get current flag
            var flags = window.decorView.systemUiVisibility
            if (flags < View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
                // add LIGHT_STATUS_BAR to flag
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility = flags
                window.statusBarColor = Color.TRANSPARENT
            }
        }
        isLightStatusBar = true
    }
    
    /**
     * clear status bar's light mode => icon color is white.
     */
    protected open fun clearLightStatusBar() {
        if (isLightStatusBar == false) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // get current flag
            var flags = window.decorView.systemUiVisibility
            // use XOR here for remove LIGHT_STATUS_BAR from flags
            if (flags >= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
                flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility = flags
                window.statusBarColor = Color.TRANSPARENT // optional
            }
        }
        isLightStatusBar = false
    }
    
    protected open fun checkIsLightStatusBar(): Boolean {
        val flags = window.decorView.systemUiVisibility
        return flags >= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    
    // Auto hide soft keyboard when edit text lost focus
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            var view = currentFocus
            when (view) {
                is ClearableEditText -> {
                    view = view.getEditTextParentView()
                    if (view == null) return super.dispatchTouchEvent(event)
                }
                is EditText -> {
                    view = currentFocus
                }
                else -> return super.dispatchTouchEvent(event)
            }
            val outRect = Rect()
            view?.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.x.toInt(), event.y.toInt())) {
                try {
                    Utils.hideSoftKeyboard(this)
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                } catch (e: Exception) {
                    Logger.e("exception = ${e.message}")
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onPause() {
        var view = currentFocus
        when (view) {
            is ClearableEditText -> {
                view = view.getEditTextParentView()
                view?.clearFocus()
            }
            is EditText -> {
                view.clearFocus()
            }
        }
        super.onPause()
    }

}