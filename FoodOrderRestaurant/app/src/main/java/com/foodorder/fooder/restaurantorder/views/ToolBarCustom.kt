package com.foodorder.fooder.restaurantorder.views

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.databinding.ToolbarCustomBinding
import com.foodorder.fooder.restaurantorder.utils.Utils

class ToolBarCustom(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val viewBinding = ToolbarCustomBinding.inflate(LayoutInflater.from(context), this, true)

    private var isSearchViewShown = false

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.ToolBarCustom
        )

        viewBinding.llToolbar.visibility = VISIBLE
        viewBinding.llSearchView.visibility = GONE

        setTitleBar(attributes.getString(R.styleable.ToolBarCustom_titleBar))
        isBackButtonShown(attributes.getBoolean(R.styleable.ToolBarCustom_isBackShown, false))

        isSearchViewShown =
            attributes.getBoolean(R.styleable.ToolBarCustom_isSearchViewShown, false)
        isSearchViewShown(isSearchViewShown)

        isDoneShown(attributes.getBoolean(R.styleable.ToolBarCustom_isDoneShown, false))
        isMoreShown(attributes.getBoolean(R.styleable.ToolBarCustom_isMoreShown, false))

        viewBinding.apply {
            imgBackSearch.setOnClickListener {
                llSearchView.visibility = GONE
                llToolbar.visibility = VISIBLE
                if (isSearchViewShown) {
                    imgSearch.visibility = VISIBLE
                }
                etSearch.text?.clear()
                Utils.hideSoftKeyboard(etSearch)
            }
            imgSearch.setOnClickListener {
                Utils.hideSoftKeyboard(this@ToolBarCustom.context)
                imgSearch.visibility = GONE
                llToolbar.visibility = GONE
                llSearchView.visibility = VISIBLE
                Utils.showSoftKeyboard(context, etSearch)
            }
        }

        attributes.recycle()
    }

    fun setTitleBar(title: String?) {
        viewBinding.tvTitleBar.text = title
    }

    fun isBackButtonShown(isShown: Boolean) {
        viewBinding.imgBack.visibility = if (isShown) VISIBLE else GONE
    }

    fun isSearchViewShown(isShown: Boolean) {
        viewBinding.imgSearch.visibility = if (isShown) VISIBLE else GONE
    }

    fun isDoneShown(isShown: Boolean) {
        viewBinding.imgDone.visibility = if (isShown) VISIBLE else GONE
    }

    fun isMoreShown(isShown: Boolean) {
        viewBinding.imgMore.visibility = if (isShown) VISIBLE else GONE
    }

    fun addTextWatcher(textWatcher: TextWatcher) {
        viewBinding.etSearch.addTextChangedListener(textWatcher)
    }

    fun setBackOnClick(listener: OnClickListener) {
        viewBinding.imgBack.setOnClickListener {
            Utils.hideSoftKeyboard(this@ToolBarCustom.context)
            listener.onClick(it)
        }
    }

    fun setDoneOnClick(listener: OnClickListener) {
        viewBinding.imgDone.setOnClickListener {
            Utils.hideSoftKeyboard(this@ToolBarCustom.context)
            listener.onClick(it)
        }
    }

    fun setMoreOnClick(listener: OnClickListener) {
        viewBinding.imgMore.setOnClickListener {
            Utils.hideSoftKeyboard(this@ToolBarCustom.context)
            listener.onClick(it)
        }
    }

}