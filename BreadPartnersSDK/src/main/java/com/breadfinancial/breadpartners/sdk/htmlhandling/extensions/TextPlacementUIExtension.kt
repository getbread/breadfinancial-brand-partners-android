package com.breadfinancial.breadpartners.sdk.htmlhandling.extensions

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentRenderer
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.InteractiveText
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementActionType
import com.breadfinancial.breadpartners.sdk.utilities.Constants

fun HTMLContentRenderer.renderTextAndButton() {
    val plainTextView = createPlainTextView()
    val actionButton = createActionButton()

    callback(BreadPartnerEvent.RenderSeparateTextAndButton(plainTextView, actionButton))
}

fun HTMLContentRenderer.renderSingleTextView() {
    val interactiveText = InteractiveText(thisContext!!)
    interactiveText.configure(textPlacementModel!!, textPlacementStyling!!) {
        handleLinkInteraction()
    }
    callback(BreadPartnerEvent.RenderTextViewWithLink(appCompatTextView = interactiveText))
}

fun HTMLContentRenderer.createPlainTextView(): TextView {
    val textView = TextView(thisContext).apply {
        layoutParams = FrameLayout.LayoutParams(
            textPlacementStyling!!.textViewFrame.width, textPlacementStyling!!.textViewFrame.height
        )
        setBackgroundColor(Color.TRANSPARENT)
        setTypeface(null, textPlacementStyling!!.normalFont)
        setTextColor(textPlacementStyling!!.normalTextColor)
        text = textPlacementModel!!.contentText ?: ""
    }
    return textView
}

fun HTMLContentRenderer.createActionButton(): Button {
    val style = textPlacementStyling!!
    textPlacementModel!!

    val button = Button(thisContext).apply {
        text = textPlacementModel?.actionLink ?: ""
        setTypeface(null, style.buttonFont)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, style.buttonTextSize)
        setTextColor(style.buttonTextColor)

        width = style.buttonFrame.width
        height = style.buttonFrame.height

        contentDescription = textPlacementModel?.actionLink ?: ""

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = style.buttonCornerRadius
            setColor(style.buttonBackgroundColor)
        }

        val pressedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = style.buttonCornerRadius
            setColor(commonUtils.darkerColor(style.buttonBackgroundColor))
        }

        val states = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            addState(intArrayOf(), drawable)
        }

        background = states

        setPadding(
            style.buttonPadding.left,
            style.buttonPadding.top,
            style.buttonPadding.right,
            style.buttonPadding.bottom
        )

        setOnClickListener { handleButtonTap(this) }
    }


    return button
}

fun HTMLContentRenderer.handleButtonTap(sender: Button) {
    sender.contentDescription ?: return
    handleLinkInteraction()
}

fun HTMLContentRenderer.handleLinkInteraction() {
    val actionType = textPlacementModel?.actionType?.let { htmlContentParser.handleActionType(it) }
    if (actionType == PlacementActionType.SHOW_OVERLAY) {
        handlePopupPlacement(textPlacementModel!!, responseModel!!)
    } else {
        showAlert(Constants.nativeSDKAlertTitle(), Constants.missingTextPlacementError)
    }
}
