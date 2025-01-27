package com.breadfinancial.breadpartners.sdk.core

import android.view.View
import androidx.fragment.app.DialogFragment

sealed class BreadPartnerEvent {
    data class RenderTextView(val view: View) : BreadPartnerEvent()
    data class RenderPopupView(val view: DialogFragment) : BreadPartnerEvent()
    object TextClicked : BreadPartnerEvent()
    object ActionButtonTapped : BreadPartnerEvent()
    data class ScreenName(val name: String) : BreadPartnerEvent()
    data class WebViewSuccess(val result: Any) : BreadPartnerEvent()
    data class WebViewFailure(val error: Throwable) : BreadPartnerEvent()
    object PopupClosed : BreadPartnerEvent()
    data class SdkError(val error: Throwable) : BreadPartnerEvent()
}