package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
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
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean {
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    url?.let { onUrlLoaded(it) }
                    view?.evaluateJavascript(jsScript) {
                        Log.i("BreadPartnerSDK::", "JSResult: $it")
                    }
                }
            }
            addJavascriptInterface(WebMessageInterface(context), "AndroidInterface")

            loadUrl(url)
        }

        parent.addView(webView)
    }

    fun destroyWebView() {
        if (::webView.isInitialized) {
            webView.destroy()
        }
    }
}


val jsScript = """
    setTimeout(function() {
        try {
            window.AndroidInterface.postMessage(JSON.stringify({
                action: {
                    type: "SAY_HELLO",
                    payload: {
                        name: "Sdk",
                        ack: false
                    }
                },
                fmcMessage: true
            }));
        } catch (err) {
            console.error('Error sending initial message:', err);
        }

        window.addEventListener('message', (event) => {
            try {
                window.AndroidInterface.postMessage(JSON.stringify({
                    action: {
                        type: "message",
                        payload: {
                            name: event.data,
                            ack: false
                        }
                    },
                    fmcMessage: true
                }));
            } catch (err) {
                console.error('Error sending HANDSHAKE message:', err);
            }
        });
    }, 10000);
""".trimIndent()
