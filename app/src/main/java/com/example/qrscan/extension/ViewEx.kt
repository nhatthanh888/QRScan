
package com.example.qrscan.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.qrscan.R
import com.example.qrscan.util.SafeOnClickListener
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


fun View.onAvoidDoubleClick(
    throttleDelay: Long = 300L,
    onClick: (View) -> Unit,
) {
    setOnClickListener {
        onClick(this)
        isClickable = false
        postDelayed({ isClickable = true }, throttleDelay)
    }
}

fun View.setAnimation(context: Activity, anim: Int?, duration: Long, onAnimation: (View) -> Unit) {
    this.startAnimation(anim?.let { AnimationUtils.loadAnimation(context, it) })
    Timer().schedule(duration) {
        context.runOnUiThread {
            onAnimation(this@setAnimation)
        }

    }
}

fun Context.toastMsg(msg: Int) =
    Toast.makeText(this, this.getString(msg), Toast.LENGTH_SHORT).show()

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}


fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

inline fun View.snack(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    snack(resources.getString(messageRes), length, f)
}

inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    action(view.resources.getString(actionRes), color, listener)
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        if (startIndexOfLink == -1) continue
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun ImageView.ofHeight(): Int {
    return this.drawable.intrinsicHeight
}

fun ImageView.ofWidth(): Int {
    return this.drawable.intrinsicWidth
}

fun ImageView.setTint(colorRes: Int) {
    try {
        ImageViewCompat.setImageTintList(
            this,
            ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        )
    } catch (e: Exception) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(colorRes))
    }
}

fun View.setMargins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        this.requestLayout()
    }
}
    @SuppressLint("ClickableViewAccessibility")
    fun View.setOnClickAffect(onClick: View.OnClickListener) {
        this.setOnTouchListener { v, motionEvent ->
            when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> {
                    v?.alpha = 0.5f
                }

                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    v?.alpha = 1f
                }
            }
            false
        }
        this.setOnClickListener(onClick)
    }

    fun View.setOnSingleClickListener(onClick: ((View?) -> Unit)? = null) {
        val listener = SafeOnClickListener()
        listener.onSafeClick = onClick
        this.setOnClickAffect(listener)
    }
