package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface

class WebMessageInterface(private val context: Context) {

    @JavascriptInterface
    fun postMessage(json: String) {
        Log.i("BreadPartnerSDK::", "Message: $json")
    }
}
