package com.foodorder.fooder.restaurantorder.views

import android.text.Editable
import android.view.View

interface OnTextChangedListener {
    fun afterTextChanged(view: View, s: Editable?)
}