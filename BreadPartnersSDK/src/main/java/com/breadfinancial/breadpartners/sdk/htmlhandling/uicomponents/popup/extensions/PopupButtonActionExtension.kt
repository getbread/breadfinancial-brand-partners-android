package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.extensions

import com.breadfinancial.breadpartners.sdk.core.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog

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
    }
}