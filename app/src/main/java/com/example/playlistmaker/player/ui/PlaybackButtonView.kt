package com.example.playlistmaker.player.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val SIZE_DP = 96
    }

    private var isPlaying = false

    private var playDrawable = AppCompatResources.getDrawable(context, android.R.color.transparent)
    private var pauseDrawable = AppCompatResources.getDrawable(context, android.R.color.transparent)

    private val dstRectF = RectF()
    private val dstRect = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var onToggleListener: ((Boolean) -> Unit)? = null

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView)
            try {
                val playRes = ta.getResourceId(R.styleable.PlaybackButtonView_srcPlay, 0)
                val pauseRes = ta.getResourceId(R.styleable.PlaybackButtonView_srcPause, 0)
                if (playRes != 0) setPlayDrawable(playRes)
                if (pauseRes != 0) setPauseDrawable(pauseRes)
            } finally {
                ta.recycle()
            }
        }
        isClickable = true
        isFocusable = true
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying != playing) {
            isPlaying = playing
            invalidate()
        }
    }

    fun toggle() = setPlaying(!isPlaying)

    fun setOnToggleListener(listener: ((Boolean) -> Unit)?) {
        onToggleListener = listener
    }

    fun setPlayDrawable(@DrawableRes resId: Int) {
        playDrawable = AppCompatResources.getDrawable(context, resId)
        requestLayout(); invalidate()
    }

    fun setPauseDrawable(@DrawableRes resId: Int) {
        pauseDrawable = AppCompatResources.getDrawable(context, resId)
        requestLayout(); invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desired = (SIZE_DP * resources.displayMetrics.density).toInt()
        val w = resolveSize(desired, widthMeasureSpec)
        val h = resolveSize(desired, heightMeasureSpec)
        val side = minOf(w, h)
        setMeasuredDimension(side, side)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        dstRectF.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (w - paddingRight).toFloat(),
            (h - paddingBottom).toFloat()
        )
        dstRect.set(
            dstRectF.left.toInt(),
            dstRectF.top.toInt(),
            dstRectF.right.toInt(),
            dstRectF.bottom.toInt()
        )
        playDrawable?.bounds = dstRect
        pauseDrawable?.bounds = dstRect
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val currentDrawable = if (isPlaying) pauseDrawable else playDrawable
        currentDrawable?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> true
            MotionEvent.ACTION_UP -> {
                toggle()
                onToggleListener?.invoke(isPlaying)
                performClick()
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}