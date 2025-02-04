package com.breadfinancial.breadpartners.sdk

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceButton
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceTextView

class BreadSDKViews {

    fun applyTextStyle(textView: TextView, fontName: String, textColor: Int, textSize: Float) {
        val customFont = ResourcesCompat.getFont(
            textView.context,
            textView.context.resources.getIdentifier(fontName, "font", textView.context.packageName)
        )
        textView.apply {
            setTextColor(textColor)
            this.textSize = textSize
            typeface = customFont
        }
    }

    fun applyButtonStyle(button: Button, fontName: String, textColor: Int, textSize: Float) {
        val customFont = ResourcesCompat.getFont(
            button.context,
            button.context.resources.getIdentifier(fontName, "font", button.context.packageName)
        )

        // Apply the font and style to the button
        button.apply {
            setTextColor(textColor)
            this.textSize = textSize
            typeface = customFont

            // Create a drawable state for button's pressed state
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 25.0f
                setColor(Color.RED)
            }

            val pressedDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 25.0f
                setColor(Color.RED)
            }

            // Set the background states
            val states = StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
                addState(intArrayOf(), drawable)
            }

            setPadding(10)
            background = states
        }
    }
}