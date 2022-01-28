package com.foodorder.fooder.restaurantorder.views

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.roundToInt

open class NumberTextWatcher(val et: EditText, val maxLength: Int = NONE_LENGTH_FILTER) : TextWatcher {
	private val dfnd: DecimalFormat
	private var hasFractionalPart: Boolean
	private var currency: Currency? = null
	
	override fun afterTextChanged(s: Editable) {
		et.removeTextChangedListener(this)
		if (s.toString().isEmpty()) {
			et.setText(s.toString())
			et.setSelection(et.text.length)
			et.addTextChangedListener(this)
			return
		}
		val textGroupingRemoved = s.toString().replace(GROUP_SEPARATOR.toString(), "")
		val listChar = textGroupingRemoved.toCharArray().toList()
		val listValidateFractional = handleValidationNumberWithFractional(listChar)
		val listValidateMaxLength = handleValidationMaxInput(listValidateFractional)
		val stringFormat = handleFormatNumeric(listValidateMaxLength)
		et.setText(stringFormat)
		et.setSelection(et.text.length)
		et.addTextChangedListener(this)
	}
	
	fun setCurrency(currency: Currency) {
		this.currency = currency
		hasFractionalPart = currency.defaultFractionDigits > DEFAULT_NONE_DIGIT
		val number = et.text.toString().replace(GROUP_SEPARATOR.toString(), "")
		if (!hasFractionalPart && number.contains(DECIMAL_SEPARATOR)) {
			et.setText(number.toFloatOrNull()?.roundToInt().toString())
		}
	}
	
	private fun handleValidationNumberWithFractional(list: List<Char>): List<Char> {
		val listChar = list.toMutableList()
		val listDot = listChar.filter { it == DECIMAL_SEPARATOR }
		// if user input more a dot, remove last dot character
		if (listDot.size > 1 || (!hasFractionalPart && listDot.isNotEmpty() && currency != null)) {
			listChar.removeLastOrNull()
			return listChar
		}
		// check length after dot character
		val indexDot = listChar.indexOf(DECIMAL_SEPARATOR)
		if (currency != null && indexDot != DEFAULT_NO_INDEX_VALUE && (listChar.size - (indexDot + 1) > currency?.defaultFractionDigits ?: DEFAULT_NONE_DIGIT || indexDot == NONE_LENGTH_FILTER)) {
			listChar.removeLastOrNull()
			return listChar
		}
		return listChar
	}
	
	private fun handleValidationMaxInput(list: List<Char>): List<Char> {
		if (maxLength <= NONE_LENGTH_FILTER) return list
		val listNonDecimalChar = list.filter { it != DECIMAL_SEPARATOR }
		return if (listNonDecimalChar.size <= maxLength) {
			list
		} else {
			val listChar = list.toMutableList()
			listChar.removeLastOrNull()
			listChar
		}
	}
	
	private fun handleFormatNumeric(list: List<Char>): String {
		if (list.isEmpty()) return ""
		val indexDot = list.indexOf(DECIMAL_SEPARATOR)
		if (indexDot == DEFAULT_NO_INDEX_VALUE) {
			return dfnd.format(dfnd.parse(String(list.toCharArray())))
		}
		val number = StringBuilder()
		for (i in 0 until indexDot) {
			number.append(list[i])
		}
		val integralPart = dfnd.format(dfnd.parse(number.toString()))
		val fullNumber = StringBuilder()
		fullNumber.append(integralPart)
		// add number after dot
		if (indexDot != DEFAULT_NO_INDEX_VALUE) {
			for (i in indexDot until list.size) {
				fullNumber.append(list[i])
			}
		}
		return fullNumber.toString()
	}
	
	override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
	override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//        hasFractionalPart =
//                s.toString().contains(df.decimalFormatSymbols.decimalSeparator.toString())
	}
	
	companion object {
		private const val TAG = "NumberTextWatcher"
		const val NONE_LENGTH_FILTER = 0
		private const val DEFAULT_NONE_DIGIT = 0
		const val GROUP_SEPARATOR = ','
		private const val DECIMAL_SEPARATOR = '.'
		private const val DEFAULT_NO_INDEX_VALUE = -1
		private const val FIRST_INDEX = 0
	}
	
	init {
		val otherSymbols = DecimalFormatSymbols(Locale.getDefault())
		otherSymbols.decimalSeparator = '.'
		otherSymbols.groupingSeparator = ','
		dfnd = DecimalFormat("#,###", otherSymbols)
		hasFractionalPart = false
	}
}