//------------------------------------------------------------------------------
//  File:          PopupElements.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Spanned
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle

/**
 * Applies the given text style to the TextView.
 */
fun TextView.applyTextStyle(style: PopupTextStyle) {
    style.font?.let {
        this.typeface = it
    }

    style.textColor?.let { this.setTextColor(it) }

    style.textSize.let {
        if (it != null) {
            this.textSize = it
        }
    }
}

/**
 * Utility class for creating and managing popup UI elements.
 */
class PopupElements private constructor() {

    companion object {
        val shared: PopupElements by lazy { PopupElements() }
    }

    /**
     * Applies background color, border, and corner radius styling to a LinearLayout.
     */
    fun decorateLinearLayout(
        linearLayout: LinearLayout,
        borderColor: Int = Color.parseColor("#FF5733"),
        backgroundColor: Int = Color.WHITE,
        topLeftRadius: Float = 32f,
        topRightRadius: Float = 32f,
        bottomLeftRadius: Float = 32f,
        bottomRightRadius: Float = 32f
    ) {
        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.RECTANGLE
        borderDrawable.setStroke(1, borderColor)

        // Set the corner radii for each corner individually
        borderDrawable.cornerRadii = floatArrayOf(
            topLeftRadius,
            topLeftRadius,
            topRightRadius,
            topRightRadius,
            bottomLeftRadius,
            bottomLeftRadius,
            bottomRightRadius,
            bottomRightRadius
        )

        borderDrawable.setColor(backgroundColor)
        linearLayout.background = borderDrawable
    }

    /**
     * Creates a TextView label from an HTML tag and its content.
     */
    fun createLabelForTag(
        popupModel: PopUpStyling,
        tag: String,
        value: Spanned,
        context: Context,
        gravity: Int = Gravity.CENTER
    ): TextView? {
        val textView = TextView(context)
        when (tag.lowercase()) {
            "h3" -> {
                textView.text = value
                textView.applyTextStyle(popupModel.headingThreePopupTextStyle)
            }

            "p" -> {
                textView.text = value
                textView.applyTextStyle(popupModel.paragraphPopupTextStyle)
            }

            "connector" -> {
                textView.text = value
                textView.applyTextStyle(popupModel.connectorPopupTextStyle)
            }

            "footer" -> {
                textView.text = value
                textView.applyTextStyle(popupModel.paragraphPopupTextStyle)
            }

            else -> return null
        }
        textView.gravity = gravity
        textView.setPadding(0, 10, 0, 10)
        return textView
    }

}