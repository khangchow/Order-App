package com.foodorder.fooder.restaurantorder.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import com.foodorder.fooder.restaurantorder.R
import com.foodorder.fooder.restaurantorder.adapters.TextWatcherAdapter

open class ClearableEditText : AppCompatEditText, OnTouchListener, OnFocusChangeListener,
	TextWatcherAdapter.TextWatcherListener {
	enum class Location(val idx: Int) {
		LEFT(0), RIGHT(2);
	}
	
	interface Listener {
		fun didClearText()
	}
	
	constructor(context: Context) : super(context) {
		init()
	}
	
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		init()
	}
	
	constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
		init()
	}
	
	fun setListener(listener: Listener?) {
		this.listener = listener
	}
	
	/**
	 * null disables the icon
	 */
	fun setIconLocation(loc: Location?) {
		this.loc = loc
		initIcon()
	}
	
	fun setClearableShown(clearableVisibleType: ClearableVisibleType?) {
		this.clearableVisibleType = clearableVisibleType ?: ClearableVisibleType.ONLY_FOCUS
		setPadding(paddingLeft,paddingTop,resources.getDimensionPixelSize(R.dimen.dp_16),paddingBottom)
		setClearIconVisible()
		
	}
	
	override fun setOnTouchListener(l: OnTouchListener) {
		this.l = l
	}
	
	override fun setOnFocusChangeListener(f: OnFocusChangeListener) {
		this.f = f
	}
	
	private var clearableVisibleType: ClearableVisibleType = ClearableVisibleType.ONLY_FOCUS
	private var loc: Location? = Location.RIGHT
	private var xD: Drawable? = null
	private var listener: Listener? = null
	private var l: OnTouchListener? = null
	private var f: OnFocusChangeListener? = null
	override fun onTouch(v: View?, event: MotionEvent): Boolean {
		if (displayedDrawable != null) {
			val x = event.x.toInt()
			val y = event.y.toInt()
			val left = if (loc == Location.LEFT) 0 else width - paddingRight - xD!!.intrinsicWidth
			val right = if (loc == Location.LEFT) paddingLeft + xD!!.intrinsicWidth else width
			val tappedX = x in left..right && y >= 0 && y <= bottom - top
			if (tappedX) {
				if (event.action == MotionEvent.ACTION_UP) {
					setText("")
					if (listener != null) {
						listener!!.didClearText()
					}
				}
				return true
			}
		}
		return if (l != null) {
			l!!.onTouch(v, event)
		} else false
	}
	
	override fun onFocusChange(v: View?, hasFocus: Boolean) {
		setClearIconVisible()
		if (f != null) {
			f!!.onFocusChange(v, hasFocus)
		}
	}
	
	override fun onTextChanged(view: EditText?, text: String?) {
		setClearIconVisible()
	}
	
	override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
		super.setCompoundDrawables(left, top, right, bottom)
		initIcon()
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private fun init() {
		super.setOnTouchListener(this)
		super.setOnFocusChangeListener(this)
		addTextChangedListener(TextWatcherAdapter(this, this))
		initIcon()
		setClearIconVisible()
	}
	
	@SuppressLint("UseCompatLoadingForDrawables")
	private fun initIcon() {
		xD = null
		if (loc != null) {
			xD = compoundDrawables[loc!!.idx]
		}
		if (xD == null) {
			xD = AppCompatResources.getDrawable(context, R.drawable.ic_clear_edittext)
		}
		xD!!.setBounds(0, 0, xD!!.intrinsicWidth, xD!!.intrinsicHeight)
		val min = paddingTop + xD!!.intrinsicHeight + paddingBottom
		if (suggestedMinimumHeight < min) {
			minimumHeight = min
		}
		this.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.dp_08)
	}
	
	private val displayedDrawable: Drawable?
		get() = if (loc != null) compoundDrawables[loc!!.idx] else null
	
	private fun setClearIconVisible() {
		val visible = isClearableVisiable() && text?.isNotEmpty() == true
		val cd = compoundDrawables
		val displayed = displayedDrawable
		val wasVisible = displayed != null
		if (visible != wasVisible) {
			val x = if (visible) xD else null
			super.setCompoundDrawables(if (loc == Location.LEFT) x else cd[0], cd[1], if (loc == Location.RIGHT) x else cd[2], cd[3])
		}
	}
	
	override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
		if (KeyEvent.KEYCODE_BACK == event?.keyCode) {
			clearFocus()
		}
		return false
	}
	
	private fun isClearableVisiable(): Boolean {
		return if (this.isFocused) {
			clearableVisibleType in listOf(
				ClearableVisibleType.ALWAYS,
				ClearableVisibleType.ONLY_FOCUS
			)
		} else {
			clearableVisibleType == ClearableVisibleType.ALWAYS
		}
	}
	
	fun getEditTextParentView(): View? {
		var parentView: View? = this
		do {
			parentView = parentView?.parent as? View
		} while (parentView != null && parentView !is IEditTextView)
		return if (parentView is IEditTextView) parentView else this
	}
}

enum class ClearableVisibleType(val value: Int) {
	ALWAYS(0),
	ONLY_FOCUS(1),
	NEVER(2);
	
	companion object {
		private val map = values().associateBy(ClearableVisibleType::value)
		fun fromValue(value: Int) = map[value]
	}
}