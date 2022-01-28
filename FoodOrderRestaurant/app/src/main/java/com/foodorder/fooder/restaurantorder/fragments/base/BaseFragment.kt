package com.foodorder.fooder.restaurantorder.fragments.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.foodorder.fooder.restaurantorder.activities.base.BaseActivity

abstract class BaseFragment: Fragment() {

    lateinit var baseActivity: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = requireActivity() as BaseActivity
    }

}