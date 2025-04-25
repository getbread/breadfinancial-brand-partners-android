//------------------------------------------------------------------------------
//  File:          BreadFinancialWebViewInterstitial.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

@file:Suppress("UNUSED_PARAMETER", "unused", "SpellCheckingInspection")

package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.utilities.Logger
import org.json.JSONObject

/**
 * Manages WebView interactions and events within the SDK.
 */
internal class BreadFinancialWebViewInterstitial(
    private val context: Context,
    private val logger: Logger,
    private val callback: (BreadPartnerEvent) -> Unit?
) {
    private var webView: WebView? = null

    /**
     * Replaces the given parent view with a WebView and loads the specified URL.
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun replaceViewWithWebView(
        parent: ViewGroup, url: String, onPageLoadCompleted: (String) -> Unit
    ) {
        webView = WebView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setWebContentsDebuggingEnabled(true)
            }

            logger.logLoadingURL(url = url)
            webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    injectAnchorInterceptorScript(view)

                    url?.let {
                        onPageLoadCompleted(it)
                    }
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    error?.let {
                        onPageLoadCompleted(it.toString())
                    }
                }
            }
            addJavascriptInterface(WebAppInterface(this), "Android")

            loadUrl(url)
        }

        parent.addView(webView)
    }

    fun destroyWebView() {
        webView?.let {
            Handler(Looper.getMainLooper()).post {
                it.destroy()
                webView = null
            }
        }
    }

    /**
     * Interface to handle messages sent from JavaScript running in the WebView.
     */
    private inner class WebAppInterface(webView: WebView) {

        @JavascriptInterface
        fun onAppRestartClicked(url: String) {
            listener?.onAppRestartClicked(url)
        }

        @JavascriptInterface
        fun logAnchorTags(data: String) {
//            logger.printWebAnchorLogs(data)
        }

        @JavascriptInterface
        fun openExternally(url: String) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }

        @JavascriptInterface
        fun postMessage(message: String) {
            Log.d("BreadPartnersSDK:", "WebViewMessage: $message")

            try {
                if (message == "undefined") {
                    return
                }
                val parsedData = JSONObject(message)
                val action = parsedData.optJSONObject("action")
                val type = action?.optString("type")

                when (type) {
                    "HEIGHT_CHANGED" -> {
                        // Handle height change if needed
                    }

                    "LOAD_ADOBE_TRACKING_ID" -> {
                        action.optJSONObject("payload")?.let { payload ->
                            val adobeTrackingId = payload.optString("adobeTrackingId")
                            logger.printLog("BreadPartnersSDK: AdobeTrackingID: $adobeTrackingId")
                        }
                    }

                    "VIEW_PAGE" -> {
                        action.optJSONObject("payload")?.let { payload ->
                            val pageName = payload.optString("pageName")
                            callback(BreadPartnerEvent.ScreenName(name = pageName))
                        }
                    }

                    "CANCEL_APPLICATION" -> {
                        callback(BreadPartnerEvent.PopupClosed)
                    }

                    "SUBMIT_APPLICATION" -> {
                        callback(BreadPartnerEvent.ScreenName(name = "submit-application"))
                    }

                    "RECEIVE_APPLICATION_RESULT" -> {
                        action.optJSONObject("payload")?.let { payload ->
                            logger.logApplicationResultDetails(payload)
                            callback(BreadPartnerEvent.WebViewSuccess(result = payload))
                        }
                    }

                    "APPLICATION_COMPLETED" -> {
                        callback(BreadPartnerEvent.ScreenName(name = "application-completed"))
                        callback(BreadPartnerEvent.PopupClosed)
                    }

                    "OFFER_RESPONSE" -> {
                        val payload = action.optString("payload")
                        if (payload == "NO" || payload == "NOT_ME") {
                            callback(BreadPartnerEvent.PopupClosed)
                        }
                    }

                    else -> {
                        callback(BreadPartnerEvent.OnSDKEventLog(message))
                    }
                }
            } catch (e: Exception) {
                callback(BreadPartnerEvent.SdkError(error = e))
            }
        }
    }

    private fun injectAnchorInterceptorScript(view: WebView?) {
        view?.evaluateJavascript(
            """
        (function() {
            function isVisible(elem) {
                return !!(elem.offsetWidth || elem.offsetHeight || elem.getClientRects().length);
            }        
            function handleAnchors() {
                const anchors = document.querySelectorAll('a[target="_blank"], a[data-open-externally="true"]');
                const anchorsHTML = Array.from(anchors).map(a => a.outerHTML);
                
                if (anchorsHTML.length > 0) {
                    window.Android.logAnchorTags('AnchorTags:\n' + anchorsHTML.join('\n'));
                } else {
                    window.Android.logAnchorTags('AnchorTags:No anchors found with target="_blank" or data-open-externally="true"');
                }

                anchors.forEach(a => {
                    // Prevent attaching multiple listeners
                    if (!a.__handled__) {
                        a.__handled__ = true;
                        a.addEventListener('click', function(event) {
                            event.preventDefault();
                            window.Android.openExternally(a.href);
                        });
                    }
                });
            }

            function handleRestartButton() {
                const btn = document.querySelector('#appRestart');
                if (btn && isVisible(btn)) {
                    if (!btn.__handled__) {
                        btn.__handled__ = true;
                        btn.addEventListener('click', function(event) {
                            event.preventDefault();
                            if (btn.href) {
                                window.Android.onAppRestartClicked(btn.href);
                            }
                        });
                    }
                }
            }


            // Initial run
            handleAnchors();
            handleRestartButton();
            
            // Watch for DOM changes
            const observer = new MutationObserver(function(mutations) {
                let shouldHandle = false;
                for (const mutation of mutations) {
                    if (mutation.addedNodes.length) {
                        shouldHandle = true;
                        break;
                    }
                }
                if (shouldHandle) {
                    handleAnchors();
                    handleRestartButton();
                }
            });

            observer.observe(document.body, {
                childList: true,
                subtree: true
            });
        })();
        """.trimIndent(), null
        )
    }

    interface WebViewRestartButtonListener {
        fun onAppRestartClicked(url: String)
    }

    private var listener: WebViewRestartButtonListener? = null

    fun setOnAppRestartListener(listener: WebViewRestartButtonListener) {
        this.listener = listener
    }
}