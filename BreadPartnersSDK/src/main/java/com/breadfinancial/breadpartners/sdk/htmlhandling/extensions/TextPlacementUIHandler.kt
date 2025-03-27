//------------------------------------------------------------------------------
//  File:          TextPlacementUIHandler.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.htmlhandling.extensions

import android.widget.Button
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

fun HTMLContentRenderer.renderTextViewWithLink() {
    val interactiveText = InteractiveText(thisContext!!)
    val spannable = interactiveText.configure(textPlacementModel!!) {
        handleLinkInteraction()
    }
    callback(BreadPartnerEvent.RenderTextViewWithLink(spannableText = spannable))
}

fun HTMLContentRenderer.handleButtonTap(sender: Button) {
    sender.contentDescription ?: return
    handleLinkInteraction()
}

fun HTMLContentRenderer.handleLinkInteraction() {
    val actionType = textPlacementModel?.actionType?.let { htmlContentParser.handleActionType(it) }
    when (actionType) {
        PlacementActionType.SHOW_OVERLAY -> {
            handlePopupPlacement(textPlacementModel!!, responseModel!!)
        }

        PlacementActionType.NO_ACTION -> {
            callback(BreadPartnerEvent.TextClicked)
        }

        else -> {
            showAlert(Constants.nativeSDKAlertTitle(), Constants.missingTextPlacementError)
        }
    }
}
