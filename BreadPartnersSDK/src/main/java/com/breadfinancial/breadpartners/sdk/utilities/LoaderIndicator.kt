//------------------------------------------------------------------------------
//  File:          LoaderIndicator.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

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

/**
 * BallSpinFadeLoader class.
 *
 * Handles the animation or behavior for a ball spin fade loader, typically used for showing loading states.
 */
class LoaderIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ballPaints: List<Paint> = createBallPaints()
    private val ballScales = FloatArray(8) { 1f } // Scaling for each ball
    private val animators = mutableListOf<ValueAnimator>()

    private val ballRadius: Float = 30f // Default value for ball radius
    private val ballDistance: Float = 60f // Default value for ball distance
    private val duration: Long = 1000L // Default value for animation duration

    private var sdkConfiguration: PlacementsConfiguration?

    init {
        BreadPartnersSDK.getInstance().placementsConfiguration.let {
            sdkConfiguration = it
        }
        startAnimation()
    }

    private fun createBallPaints(): List<Paint> {
        val loaderColor = BreadPartnerDefaults.shared.popUpStyling.loaderColor
        return List(8) {
            Paint().apply {
                color = loaderColor
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
