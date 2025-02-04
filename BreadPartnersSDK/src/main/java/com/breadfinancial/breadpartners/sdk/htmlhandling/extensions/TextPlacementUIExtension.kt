package com.breadfinancial.breadpartners.sdk.htmlhandling.extensions

import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
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
    val spannable = interactiveText.configure(textPlacementModel!!) {
        handleLinkInteraction()
    }
    callback(BreadPartnerEvent.RenderTextViewWithLink(spannableText = spannable))
}

fun HTMLContentRenderer.createPlainTextView(): TextView {
    val textView = TextView(thisContext).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(Color.TRANSPARENT)
        text = textPlacementModel!!.contentText ?: ""
    }
    return textView
}

fun HTMLContentRenderer.createActionButton(): Button {
    textPlacementModel!!

    val button = Button(thisContext).apply {
        text = textPlacementModel?.actionLink ?: ""
        contentDescription = textPlacementModel?.actionLink ?: ""
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
