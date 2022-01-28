package com.foodorder.fooder.restaurantorder.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import com.foodorder.fooder.restaurantorder.R

class LoadingDialog : AlertDialog {
    
    private val LOADING_DIALOG_DISMISS_DELAY_TIME = 500L
    private var showedAt = 0L
    var lifecycleTime = 0L
    val handler: Handler = Handler()
    var isCancel: Boolean = false
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val rootView = LayoutInflater.from(context).inflate(R.layout.layout_loading_dialog, null)
        setView(rootView)
//        setCancelable(false)
//        val img = rootView.findViewById<ImageView>(R.id.img_loading)
//        img.setBackgroundResource(R.drawable.anim_loading)
//        val ani = img.background as AnimationDrawable
//        ani.start()
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        super.onCreate(savedInstanceState)
    }
    
    override fun show() {
        super.show()
        showedAt = System.currentTimeMillis()
        handler.removeCallbacksAndMessages(null)
    }
    
    override fun onStart() {
        super.onStart()
//        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading_show)
//        rootView?.visibility = View.VISIBLE
//        rootView?.startAnimation(animation)
    }
    
    override fun dismiss() {
//        val animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading_dismiss)
//                .apply {
//                    setAnimationListener(object : Animation.AnimationListener {
//                        override fun onAnimationRepeat(animation: Animation?) {
//                        }
//
//                        override fun onAnimationEnd(animation: Animation?) {
//                            rootView?.visibility = View.GONE
//                        }
//
//                        override fun onAnimationStart(animation: Animation?) {
//                        }
//                    })
//                }
//        rootView?.startAnimation(animation)
        if (isCancel) {
            isCancel = false
            super.dismiss()
        }
        
        lifecycleTime = showedAt + LOADING_DIALOG_DISMISS_DELAY_TIME - System.currentTimeMillis()
        handler.removeCallbacksAndMessages(null)
        if (lifecycleTime <= 0) {
            // Tránh trướng hợp ẩn rồi hiện luôn gây nhấp nháy màn hình
            handler.postDelayed({ super.dismiss() }, 50)
//            super.dismiss()
        } else {
            handler.postDelayed({ super.dismiss() }, lifecycleTime)
        }
    }
    
    override fun cancel() {
        isCancel = true
        super.cancel()
    }
    
}
