package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import android.widget.FrameLayout

internal class BreadFinancialWebViewInterstitial(private val context: Context) {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    fun replaceViewWithWebView(
        parent: ViewGroup, url: String, onUrlLoaded: (String) -> Unit
    ) {
        webView = WebView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
                allowContentAccess = false
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }
            setWebContentsDebuggingEnabled(true)
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let {
                        onUrlLoaded(it)
                    }
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    error?.let {
                        onUrlLoaded(it.toString())
                    }
                }
            }
            webChromeClient = WebChromeClient()
            addJavascriptInterface(MessageHandler(this), "MessageHandler")

            loadUrl(url)
        }

        parent.addView(webView)
    }

    fun destroyWebView() {
        if (::webView.isInitialized) {
            webView.destroy()
        }
    }

    private inner class MessageHandler(webView: WebView) {
        @JavascriptInterface
        fun postMessage(message: String) {
            Log.d("BreadPartnersSDK:", "WebViewMessage: $message")
        }
    }
}