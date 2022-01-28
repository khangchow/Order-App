package com.foodorder.fooder.restaurantorder.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.databinding.IEditTextViewBinding
import com.foodorder.fooder.restaurantorder.utils.Logger
import com.foodorder.fooder.restaurantorder.utils.disableEmojIcon
import com.foodorder.fooder.restaurantorder.utils.onChange

class IEditTextView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs),
	TextWatcher {
	
	private val viewBinding =
		IEditTextViewBinding.inflate(LayoutInflater.from(context), this, true)
	private var isPasswordVisible = false
	private var onEndDrawableClickListener: OnClickListener? = null
	private var onStartDrawableClickListener: OnClickListener? = null
	private var onMoreDrawableClickListener: OnClickListener? = null
	private var onEditorActionListener: OnImeActionListener? = null
	private var onTextWatcher: TextWatcher? = null
	private var onTextChangedListener: OnTextChangedListener? = null
	private var inputType: Int = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
	private var maxLength = 0
	
	@StyleRes
	private var styleContent = R.style.AppTheme_TextView_TextField1
	
	init {
		val attributes = context.obtainStyledAttributes(
			attrs, R.styleable.IEditTextView
		)
		setHint(attributes.getString(R.styleable.IEditTextView_android_hint))
		setText(attributes.getString(R.styleable.IEditTextView_android_text))
		setHintWarning(attributes.getString(R.styleable.IEditTextView_hintWarning))
		setMaxLines(attributes.getInt(R.styleable.IEditTextView_android_maxLines, DEFAULT_VALUE))
		setInputType(
			attributes.getInt(
				R.styleable.IEditTextView_android_inputType,
				InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
			)
		)
		setDrawableStart(attributes.getDrawable(R.styleable.IEditTextView_android_drawableStart))
		setDrawableEnd(attributes.getDrawable(R.styleable.IEditTextView_android_drawableEnd))
		setImeOptions(
			attributes.getInt(
				R.styleable.IEditTextView_android_imeOptions,
				DEFAULT_VALUE
			)
		)
		setEditable(attributes.getBoolean(R.styleable.IEditTextView_editable, true))
		setEnable(attributes.getBoolean(R.styleable.IEditTextView_android_enabled, true))
		
		setEndTextView(attributes.getString(R.styleable.IEditTextView_endTextView))
		setWarningText(attributes.getString(R.styleable.IEditTextView_warningText))
		setNoticeText(attributes.getString(R.styleable.IEditTextView_noticeText))
		setDrawableMore(attributes.getDrawable(R.styleable.IEditTextView_drawableMore))
		setClearableShown(
			attributes.getInt(
				R.styleable.IEditTextView_clearableShown,
				ClearableVisibleType.ONLY_FOCUS.value
			)
		)
		setShowTextCountDown(
			attributes.getBoolean(
				R.styleable.IEditTextView_showCountDown,
				false
			)
		)
		setupHintView()
		attributes.recycle()
		
		preventEnterEmoj()
		
		viewBinding.apply {
			imgEndImage.setOnClickListener {
				if (isPasswordEditView()) {
					isPasswordVisible = !isPasswordVisible
					setPasswordToggleImage()
				} else {
					onEndDrawableClickListener?.onClick(this@IEditTextView)
				}
			}
			
			imgMoreImage.setOnClickListener {
				onMoreDrawableClickListener?.onClick(this@IEditTextView)
			}
			
			imgStartImage.setOnClickListener {
				onStartDrawableClickListener?.onClick(this@IEditTextView)
			}
			
			etContent.setOnFocusChangeListener { v, hasFocus ->
				if (hasFocus) {
					viewBinding.clMain.setBackgroundResource(R.drawable.bg_ibank_editext_v3_focus)
					tvHint.setTextColor(tvHint.context.getColor(R.color.primary4))
				} else {
					viewBinding.clMain.setBackgroundResource(R.drawable.i_editext_not_focus)
					tvHint.setTextColor(tvHint.context.getColor(R.color.neutral_yellow_3))
					onEditorActionListener?.imeAction(EditorInfo.IME_ACTION_DONE)
				}
			}
			
			etContent.setOnEditorActionListener { _, actionId, _ ->
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
					onEditorActionListener?.imeAction(actionId)
					true
				}
				// Return true if you have consumed the action, else false.
				false
			}
			
			viewBinding.etContent.addTextChangedListener(this@IEditTextView)
		}
	}
	
	fun setOnIBankEditTextClickListener(listener: OnClickListener) {
		viewBinding.root.setOnClickListener { listener.onClick(this@IEditTextView) }
		viewBinding.etContent.setOnClickListener { listener.onClick(this@IEditTextView) }
	}
	
	fun setHintWarning(hint: String?) {
		viewBinding.etContent.hint = hint
	}
	
	fun setEditable(editable: Boolean) {
		if (editable) {
			if (viewBinding.etContent.inputType == InputType.TYPE_NULL) {
				viewBinding.etContent.inputType = inputType
				viewBinding.etContent.setTextIsSelectable(true)
				viewBinding.etContent.isEnabled = true
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					viewBinding.etContent.focusable = View.FOCUSABLE
				} else {
					viewBinding.etContent.isFocusable = true
				}
			}
		} else {
			if (viewBinding.etContent.inputType != InputType.TYPE_NULL) {
				viewBinding.etContent.inputType = InputType.TYPE_NULL
				viewBinding.etContent.setTextIsSelectable(false)
				viewBinding.etContent.isEnabled = false
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					viewBinding.etContent.focusable = View.NOT_FOCUSABLE
				} else {
					viewBinding.etContent.isFocusable = false
				}
			}
		}
	}
	
	fun setOnEditorActionListener(listener: OnImeActionListener) {
		onEditorActionListener = listener
	}
	
	fun setOnTextChangedListener(listener: OnTextChangedListener) {
		onTextChangedListener = listener
	}
	
	fun addTextChangedListener(textWatcher: TextWatcher) {
		onTextWatcher = textWatcher
	}
	
	fun getTextWatcherListener() = onTextWatcher
	
	fun removeTextChangedListener() {
		onTextWatcher = null
	}
	
	override fun onViewRemoved(view: View?) {
		viewBinding.etContent.removeTextChangedListener(this)
		super.onViewRemoved(view)
	}
	
	fun setClearableShown(type: Int) {
		(getEditView() as? ClearableEditText)?.setClearableShown(ClearableVisibleType.fromValue(type))
	}
	
	fun setEndTextView(text: String?) {
		if (text.isNullOrEmpty()) {
			viewBinding.tvEndText.visibility = View.GONE
		} else {
			viewBinding.tvEndText.visibility = View.VISIBLE
		}
		viewBinding.tvEndText.text = text
		setVisibilityEndLayoutView()
	}
	
	fun setDrawableEnd(resource: Int) {
		if (resource != DEFAULT_VALUE) {
			viewBinding.imgEndImage.visibility = View.VISIBLE
			if (!isPasswordEditView()) {
				viewBinding.imgEndImage.setImageResource(resource)
			}
		} else {
			viewBinding.imgEndImage.visibility = View.GONE
		}
		setVisibilityEndLayoutView()
	}
	
	fun setImeOptions(imeOptions: Int) {
		if (imeOptions != DEFAULT_VALUE) {
			viewBinding.etContent.imeOptions = imeOptions
		}
	}
	
	fun setDrawableEnd(drawable: Drawable?) {
		if (drawable != null && !isPasswordEditView()) {
			viewBinding.imgEndImage.setImageDrawable(drawable)
		}
		viewBinding.imgEndImage.visibility =
			if (drawable == null && !isPasswordEditView()) View.GONE else View.VISIBLE
		setVisibilityEndLayoutView()
	}
	
	fun setDrawableStart(drawable: Drawable?) {
		if (drawable != null) {
			viewBinding.imgStartImage.setImageDrawable(drawable)
			viewBinding.imgStartImage.visibility = View.VISIBLE
		} else {
//            viewBinding.etContent.setPadding(
//                resources.getDimensionPixelSize(R.dimen.dp_08),
//                0,
//                0,
//                0
//            )
			viewBinding.imgStartImage.visibility = View.GONE
		}
	}
	
	fun setOnEndDrawableClickListener(listener: OnClickListener) {
		onEndDrawableClickListener = listener
	}
	
	fun setOnStartDrawableClickListener(listener: OnClickListener) {
		onStartDrawableClickListener = listener
	}
	
	fun setOnMoreDrawableClickListener(listener: OnClickListener) {
		onMoreDrawableClickListener = listener
	}
	
	fun setHint(hint: String?) {
		viewBinding.tvHint.text = hint
		viewBinding.tvHint.visibility = if (hint.isNullOrEmpty()) View.GONE else View.VISIBLE
	}
	
	fun setWarningText(text: String?) {
		viewBinding.tvWarningText.text = text
		viewBinding.tvWarningText.visibility =
			if (text?.isNotEmpty() == true) View.VISIBLE else View.GONE
	}
	
	fun setNoticeText(text: String?) {
		viewBinding.tvNoticeText.text = text
		viewBinding.tvNoticeText.visibility =
			if (text?.isNotEmpty() == true) View.VISIBLE else View.GONE
	}
	
	fun setDrawableMore(drawable: Drawable?) {
		if (drawable != null) {
			viewBinding.imgMoreImage.setImageDrawable(drawable)
		}
		viewBinding.imgMoreImage.visibility = if (drawable == null) View.GONE else View.VISIBLE
		setVisibilityEndLayoutView()
	}
	
	fun setInputType(inputType: Int) {
		this.inputType = inputType
		getEditView().inputType = inputType
		if (isPasswordEditView()) {
			setPasswordToggleImage()
		}
	}
	
	private fun setPasswordToggleImage() {
		if (!isPasswordVisible) {
			viewBinding.etContent.transformationMethod = PasswordTransformationMethod.getInstance()
			viewBinding.imgEndImage.setImageResource(R.drawable.ic_eye_off)
			viewBinding.imgEndImage.visibility = View.VISIBLE
		} else {
			viewBinding.etContent.transformationMethod = null
			viewBinding.imgEndImage.setImageResource(R.drawable.ic_eye_on)
			viewBinding.imgEndImage.visibility = View.VISIBLE
		}
		viewBinding.etContent.setSelection(viewBinding.etContent.text?.length ?: 0)
	}
	
	private fun setMaxLines(lines: Int) {
		if (lines != DEFAULT_VALUE) {
			getEditView().maxLines = lines
		}
	}
	
	fun preventEnterEmoj() {
		getEditView().disableEmojIcon()
	}
	
	private fun setShowTextCountDown(isShow: Boolean) {
		viewBinding.tvCountDown.apply {
			if (isShow && maxLength > 0) {
				visibility = View.VISIBLE
				text = String.format(
					context.getString(R.string.format_remark_remain),
					maxLength.toString()
				)
				getEditView().onChange {
					text = String.format(
						context.getString(R.string.format_remark_remain),
						(maxLength - it.length).toString()
					)
				}
			} else {
				visibility = View.GONE
			}
		}
	}
	
	fun getText() = getEditView().text.toString()
	
	fun setText(value: String?) {
		if (!getText().equals(value, true)) {
			getEditView().apply {
				setText(value)
				if (!TextUtils.isEmpty(value)) {
					try {
						setSelection(value!!.length)
					} catch (e: Exception) {
						Logger.e("error set text = ${e.message}")
					}
				}
			}
		}
	}
	
	fun setSelection(selection: Int) {
		viewBinding.etContent.setSelection(selection)
	}
	
	fun getEditView(): EditText = viewBinding.etContent
//		viewBinding.root.findViewWithTag(context.getString(R.string.edit_registration))
	
	// refer: https://developer.android.com/reference/android/widget/TextView.html#attr_android:inputType
	private fun isPasswordEditView(): Boolean {
		val variation =
			viewBinding.etContent.inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
		return (variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
			|| (variation == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD)
			|| (variation == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
	}
	
	private fun setVisibilityEndLayoutView() {
		viewBinding.clEndView.visibility = if (
			viewBinding.imgEndImage.visibility != View.VISIBLE
			&& viewBinding.tvEndText.visibility != View.VISIBLE
			&& viewBinding.imgMoreImage.visibility != View.VISIBLE
		) View.GONE else View.VISIBLE
	}
	
	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		getEditView().isEnabled = enabled
	}
	
	private fun removeUnderLine(text: Editable?) {
		text?.let {
			it.getSpans(0, it.length, UnderlineSpan::class.java).forEach { span ->
				it.removeSpan(span)
			}
		}
	}
	
	override fun afterTextChanged(s: Editable?) {
		onTextWatcher?.afterTextChanged(s)
		onTextChangedListener?.afterTextChanged(this, s)
		removeUnderLine(s)
	}
	
	override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		onTextWatcher?.beforeTextChanged(s, start, count, after)
	}
	
	override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		onTextWatcher?.onTextChanged(s, start, before, count)
		setupHintView()
	}
	
	private fun setupHintView() {
		if (viewBinding.etContent.hint?.isNotEmpty() == true && viewBinding.etContent.text.isNullOrEmpty()) {
			styleContent = R.style.AppTheme_TextView_Body2
			viewBinding.etContent.setTextAppearance(R.style.AppTheme_TextView_Body2)
		} else {
			viewBinding.etContent.setTextAppearance(R.style.AppTheme_TextView_TextField1)
			styleContent = R.style.AppTheme_TextView_TextField1
			viewBinding.etContent.setTextColor(
				ContextCompat.getColor(
					this.context,
					R.color.primary4
				)
			)
		}
	}
	
	fun setEnable(enable: Boolean) {
		super.setEnabled(enable)
		viewBinding.etContent.isEnabled = enable
		viewBinding.etContent.setTextColor(
			ContextCompat.getColor(
				context,
				R.color.neutral_yellow_3
			)
		)
	}
	
	companion object {
		private const val DEFAULT_VALUE = -1
		private const val MAX_LENGTH_VALUE = 999
	}
	
}