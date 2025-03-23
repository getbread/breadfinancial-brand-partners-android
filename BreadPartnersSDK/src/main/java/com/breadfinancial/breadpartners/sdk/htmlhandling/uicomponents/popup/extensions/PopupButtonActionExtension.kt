package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.extensions

import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog
import com.breadfinancial.breadpartners.sdk.utilities.Constants

fun PopupDialog.closeButtonTapped() {
    callback(BreadPartnerEvent.PopupClosed)
    dismiss()
    webViewManager.destroyWebView()
    return
}

fun PopupDialog.onActionButtonTapped() {
    callback(BreadPartnerEvent.ActionButtonTapped)
    if (isWebViewPlacementModelInitialized()) {
        displayEmbeddedOverlay(webViewPlacementModel)
    } else {
        callback(
            BreadPartnerEvent.SdkError(
                error = Exception(Constants.somethingWentWrong)
            )
        )
    }
}