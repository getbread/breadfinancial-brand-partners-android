package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle

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

class PopupElements private constructor() {

    companion object {
        val shared: PopupElements by lazy { PopupElements() }
    }

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

    fun createLabelForTag(
        popupModel: PopUpStyling,
        tag: String,
        value: String,
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

            else -> return null
        }
        textView.gravity = gravity
        textView.setPadding(0, 10, 0, 10)
        return textView
    }

}