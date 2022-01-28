package com.foodorder.fooder.restaurantorder.views

import android.text.Editable
import android.view.View
import android.widget.EditText
import com.foodorder.fooder.restaurantorder.utils.Constants

class FormatTextWatcher(
	et: EditText?,
	maxLength: Int = Constants.NONE_LENGTH_FILTER,
	private val onTextChangedListener: OnTextChangedListener?,
) : NumberTextWatcher(et!!, maxLength) {
	var parentView: View? = null
	
	init {
		if (et is ClearableEditText) {
			parentView = et.getEditTextParentView()
		}
	}
	
	override fun afterTextChanged(s: Editable) {
		(parentView as? IEditTextView)?.removeTextChangedListener()
		super.afterTextChanged(s)
		onTextChangedListener?.afterTextChanged(parentView ?: et, et.text)
		(parentView as? IEditTextView)?.addTextChangedListener(this)
	}
}