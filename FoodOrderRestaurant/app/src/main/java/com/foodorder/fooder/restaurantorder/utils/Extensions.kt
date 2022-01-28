package com.foodorder.fooder.restaurantorder.utils

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import com.foodorder.fooder.restaurantorder.databinding.BaseAlertDialogBinding
import com.foodorder.fooder.restaurantorder.databinding.BaseAlertInputDialogBinding
import com.foodorder.fooder.restaurantorder.databinding.LayoutCustomDialogBinding
import kotlinx.android.synthetic.main.layout_custom_dialog.*
import java.text.Normalizer
import java.util.regex.Pattern

@SuppressLint("UseCompatLoadingForDrawables")
fun Context.getDrawableApp(@DrawableRes resource: Int) =
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		resources.getDrawable(resource)
	} else {
		AppCompatResources.getDrawable(this, resource)
	}

fun String.deAccent(): String {
	val nfdNormalizedString: String = Normalizer.normalize(this, Normalizer.Form.NFD)
	val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
	return pattern.matcher(nfdNormalizedString).replaceAll("") ?: ""
}

fun Activity.showAlertDialog(
	alertTitle: String,
	alertMessage: String,
	positiveLabel: String?,
	negativeLabel: String? = null,
	positiveClick: () -> Unit = {},
	negativeClick: () -> Unit = {},
	cancelAble: Boolean = false,
) {
	val baseAlertDialog = BaseAlertDialogBinding.inflate(LayoutInflater.from(this))
	val alertDialog = AlertDialog.Builder(this).create()
	alertDialog.setCustomTitle(null)
	alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
	alertDialog.setView(baseAlertDialog.root)
	
	baseAlertDialog.messageContent.text = alertMessage
	baseAlertDialog.messageContent.gravity = Gravity.CENTER_HORIZONTAL
	
	if (alertTitle.isEmpty()) {
		baseAlertDialog.header.visibility = View.GONE
	} else {
		baseAlertDialog.header.text = alertTitle
		if (positiveLabel.isNullOrBlank()) {
			baseAlertDialog.positiveButton.visibility = View.GONE
		} else {
			baseAlertDialog.positiveButton.text = positiveLabel
			baseAlertDialog.positiveButton.setOnClickListener {
				alertDialog.dismiss()
				positiveClick()
			}
		}
	}
	if (negativeLabel.isNullOrBlank()) {
		baseAlertDialog.negativeButton.visibility = View.GONE
	} else {
		baseAlertDialog.negativeButton.text = negativeLabel
		baseAlertDialog.negativeButton.setOnClickListener {
			alertDialog.dismiss()
			negativeClick()
		}
	}
	alertDialog.setCancelable(cancelAble)
	alertDialog.show()
}

fun Activity.showCustomDialog(
	dialogTitle: String = "",
	dialogMessage: String = "",
	positiveLabel: String? = "",
	negativeLabel: String? = "",
	positiveClick: (data: String) -> Unit = {},
	negativeClick: () -> Unit = {},
	isNumberInputType: Boolean = false,
	showEditText: Boolean = true,
	showDropDownMenu: Boolean = false,
	dropDownList: List<String> = emptyList()
) {
	val baseCustomDialog = LayoutCustomDialogBinding.inflate(LayoutInflater.from(this))
	val customDialog = AlertDialog.Builder(this).create()
	customDialog.setCustomTitle(null)
	customDialog.setView(baseCustomDialog.root)

	if (showEditText) {
		baseCustomDialog.edittext.visibility = View.VISIBLE

		baseCustomDialog.edittext.setInputType(if (isNumberInputType) InputType.TYPE_CLASS_NUMBER
		else InputType.TYPE_CLASS_TEXT)
	}else {
		baseCustomDialog.edittext.visibility = View.GONE
	}

	if (showDropDownMenu) {
		baseCustomDialog.viewTableChooser.visibility = View.VISIBLE

		val arrayAdapter = ArrayAdapter(this
			, R.layout.simple_spinner_dropdown_item
			, dropDownList)

		baseCustomDialog.tvTableChooser.setAdapter(arrayAdapter)
	}else {
		baseCustomDialog.viewTableChooser.visibility = View.GONE
	}

	if (dialogMessage.isEmpty()) {
		baseCustomDialog.messageContent.visibility = View.GONE
	}else {
		baseCustomDialog.messageContent.visibility = View.VISIBLE
		baseCustomDialog.messageContent.text = dialogMessage
	}

	if (dialogTitle.isEmpty()) {
		baseCustomDialog.header.visibility = View.GONE
	} else {
		baseCustomDialog.header.text = dialogTitle
		if (positiveLabel.isNullOrBlank()) {
			baseCustomDialog.positiveButton.visibility = View.GONE
		} else {
			baseCustomDialog.positiveButton.text = positiveLabel
			baseCustomDialog.positiveButton.setOnClickListener {
				customDialog.dismiss()
				positiveClick(if(!showDropDownMenu) baseCustomDialog.edittext.getText()
					else baseCustomDialog.tvTableChooser.getText().toString())
			}
		}
	}
	if (negativeLabel.isNullOrBlank()) {
		baseCustomDialog.negativeButton.visibility = View.GONE
	} else {
		baseCustomDialog.negativeButton.text = negativeLabel
		baseCustomDialog.negativeButton.setOnClickListener {
			customDialog.dismiss()
			negativeClick()
		}
	}

	customDialog.show()
}

fun EditText.onChange(onChange: (String) -> Unit) {
	this.addTextChangedListener(object : TextWatcher {
		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		}
		
		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		}
		
		override fun afterTextChanged(s: Editable?) {
			onChange.invoke(s.toString())
		}
	})
}

fun EditText.disableEmojIcon() {
	val filterEmoj = object : InputFilter {
		override fun filter(
			source: CharSequence,
			start: Int,
			end: Int,
			dest: Spanned?,
			dstart: Int,
			dend: Int,
		): CharSequence? {
			for (i in start until end) {
				val type: Int = Character.getType(source[i])
				if (type == Character.SURROGATE.toInt()
					|| type == Character.OTHER_SYMBOL.toInt()
					|| type == Character.NON_SPACING_MARK.toInt()
					|| type == Character.ENCLOSING_MARK.toInt()) {
					return ""
				}
			}
			return null
		}
	}
	filters = filters.plus(filterEmoj)
}

fun Activity.showAlertInput(
	alertTitle: String,
	alertMessage: String,
	hintInputText: String? = "",
	isPasswordFormat: Boolean = false,
	positiveLabel: String?,
	negativeLabel: String?,
	positiveClick: (text: String) -> Unit = {},
	negativeClick: () -> Unit = {},
	cancelAble: Boolean = false,
) {
	val baseAlertDialog = BaseAlertInputDialogBinding.inflate(LayoutInflater.from(this))
	val alertDialog = AlertDialog.Builder(this).create()
	alertDialog.setCustomTitle(null)
	alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
	alertDialog.setView(baseAlertDialog.root)
	
	baseAlertDialog.etInput.setHint(hintInputText)
	
	baseAlertDialog.messageContent.text = alertMessage
	baseAlertDialog.messageContent.gravity = Gravity.CENTER_HORIZONTAL
	
	if (alertTitle.isEmpty()) {
		baseAlertDialog.header.visibility = View.GONE
	} else {
		baseAlertDialog.header.text = alertTitle
	}
	
	
	baseAlertDialog.positiveButton.isEnabled = false
	
	val inputType = if (isPasswordFormat) {
		InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
	} else {
		InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
	}
	baseAlertDialog.etInput.setInputType(inputType)
	
	if (positiveLabel.isNullOrBlank()) {
		baseAlertDialog.positiveButton.visibility = View.GONE
	} else {
		baseAlertDialog.positiveButton.text = positiveLabel
		baseAlertDialog.positiveButton.setOnClickListener {
			alertDialog.dismiss()
			positiveClick(baseAlertDialog.etInput.getText())
		}
	}
	
	if (negativeLabel.isNullOrBlank()) {
		baseAlertDialog.negativeButton.visibility = View.GONE
	} else {
		baseAlertDialog.negativeButton.text = negativeLabel
		baseAlertDialog.negativeButton.setOnClickListener {
			alertDialog.dismiss()
			negativeClick()
		}
	}
	
	val listener = object : TextWatcher {
		override fun afterTextChanged(s: Editable?) {
			baseAlertDialog.positiveButton.isEnabled = s?.isNotEmpty() == true
		}
		
		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		}
		
		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		}
	}
	
	baseAlertDialog.etInput.getEditView().addTextChangedListener(listener)
	alertDialog.setOnDismissListener {
		baseAlertDialog.etInput.getEditView().removeTextChangedListener(listener)
	}
	alertDialog.setCancelable(cancelAble)
	alertDialog.show()
}
