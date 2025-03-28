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

/**
 * Renders both text and a button from the HTMLContent
 */
fun HTMLContentRenderer.renderTextAndButton() {
    val plainTextView = createPlainTextView()
    val actionButton = createActionButton()

    callback(BreadPartnerEvent.RenderSeparateTextAndButton(plainTextView, actionButton))
}

/**
 * Renders a TextView from the HTMLContent with clickable link.
 */
fun HTMLContentRenderer.renderTextViewWithLink() {
    val interactiveText = InteractiveText(thisContext!!)
    val spannable = interactiveText.configure(textPlacementModel!!) {
        handleLinkInteraction()
    }
    callback(BreadPartnerEvent.RenderTextViewWithLink(spannableText = spannable))
}

/**
 * Handles tap events on buttons rendered.
 */
fun HTMLContentRenderer.handleButtonTap(sender: Button) {
    sender.contentDescription ?: return
    handleLinkInteraction()
}

/**
 * Handles user interactions with links rendered.
 */
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
