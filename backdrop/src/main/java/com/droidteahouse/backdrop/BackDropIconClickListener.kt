package com.droidteahouse.backdrop

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView


/**
 * [android.view.View.OnClickListener] used to translate the sheet downward on
 * the Y-axis when the navigation icon in the toolbar is pressed.
 */
class BackDropIconClickListener @JvmOverloads constructor(
        private val context: Context, private val sheet: View, private val openIcon: Drawable? = null, private val closeIcon: Drawable? = null) : View.OnClickListener, View.OnTouchListener {

    private val animatorSet = AnimatorSet()
    private val height: Int
    private var backdropShown = false
    private val interpolator = AccelerateDecelerateInterpolator()

    init {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
    }

    override fun onClick(view: View) {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        if (view is ImageView) updateIcon(view)

        val translateY = height - context.resources.getDimensionPixelSize(R.dimen.grid_reveal_height)

        val animator = ObjectAnimator.ofFloat(sheet, "translationY", (if (backdropShown) translateY else 0).toFloat())
        animator.duration = 500
        animator.interpolator = interpolator
        animatorSet.play(animator)
        animator.start()
    }

    private fun updateIcon(view: View) {
        if (openIcon != null && closeIcon != null) {
            if (view !is ImageView) {
                throw IllegalArgumentException("updateIcon() must be called on an ImageView")
            }
            if (backdropShown) {
                view.setImageDrawable(closeIcon)
            } else {
                view.setImageDrawable(openIcon)
            }
        }
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        gestureDetector.onTouchEvent(event)
        return true
    }

    val gestureDetector: GestureDetector = GestureDetector(context as Activity, object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onClick(sheet)
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return super.onDoubleTap(e)
        }
    })
}
