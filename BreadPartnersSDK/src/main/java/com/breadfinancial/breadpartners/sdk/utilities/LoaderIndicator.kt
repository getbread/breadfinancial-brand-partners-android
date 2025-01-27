package com.breadfinancial.breadpartners.sdk.utilities

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import kotlin.math.cos
import kotlin.math.sin

class LoaderIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val sdkConfiguration: PlacementsConfiguration = BreadPartnersSDK.getInstance().placementsConfiguration!!
) : View(context, attrs, defStyleAttr) {

    private val ballPaints: List<Paint> = createBallPaints()
    private val ballScales = FloatArray(8) { 1f } // Scaling for each ball
    private val animators = mutableListOf<ValueAnimator>()

    private val ballRadius: Float = 30f // Default value for ball radius
    private val ballDistance: Float = 60f // Default value for ball distance
    private val duration: Long = 1000L // Default value for animation duration

    init {
        startAnimation()
    }

    private fun createBallPaints(): List<Paint> {
        return List(8) {
            Paint().apply {
                color = sdkConfiguration.popUpStyling!!.loaderColor
                isAntiAlias = true
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = ballDistance * 2

        // Draw all 8 balls
        for (i in 0 until 8) {
            val angle = Math.toRadians((45 * i).toDouble())
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            canvas.drawCircle(x, y, ballRadius * ballScales[i], ballPaints[i])
        }
    }

    private fun startAnimation() {
        for (i in 0 until 8) {
            val animator = createAnimatorForBall(i)
            animators.add(animator)
            animator.start()
        }
    }

    private fun createAnimatorForBall(i: Int): ValueAnimator {
        return ValueAnimator.ofFloat(0f, 0.3f, 1f).apply {
            duration = this@LoaderIndicator.duration
            startDelay = (i * 100L) // Stagger animations
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                ballScales[i] = animation.animatedValue as Float
                invalidate()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop animations when the view is detached
        animators.forEach { it.cancel() }
    }
}
