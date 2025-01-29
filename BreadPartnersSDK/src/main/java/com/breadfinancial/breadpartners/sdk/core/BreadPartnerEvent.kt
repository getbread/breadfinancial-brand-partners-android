package com.breadfinancial.breadpartners.sdk.core

import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment

sealed class BreadPartnerEvent {
    data class RenderTextView(val appCompatTextView: AppCompatTextView) : BreadPartnerEvent()
    data class RenderPopupView(val dialogFragment: DialogFragment) : BreadPartnerEvent()
    object TextClicked : BreadPartnerEvent()
    object ActionButtonTapped : BreadPartnerEvent()
    data class ScreenName(val name: String) : BreadPartnerEvent()
    data class WebViewSuccess(val result: Any) : BreadPartnerEvent()
    data class WebViewFailure(val error: Throwable) : BreadPartnerEvent()
    object PopupClosed : BreadPartnerEvent()
    data class SdkError(val error: Throwable) : BreadPartnerEvent()
}