package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.security

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.breadfinancial.breadpartners.sdk.R

class ChallengeDialog(
    private val htmlContent: String,
    private val baseUrl: String,
    private val onComplete: () -> Unit
) : DialogFragment() {

    private var challengeCompleted: Boolean = false

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_challenge, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val webView = view.findViewById<WebView>(R.id.challengeWebView)
        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)

        closeButton.setOnClickListener {
            dismiss()
        }

        webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!challengeCompleted) {
                    challengeCompleted = true
                } else {
                    dismiss()
                    onComplete()
                }
            }
        }

        // Load the challenge HTML with a base URL to allow iframe loading
        webView.loadDataWithBaseURL(
            baseUrl,
            htmlContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    override fun onDestroyView() {
        val webView = view?.findViewById<WebView>(R.id.challengeWebView)
        webView?.destroy()
        super.onDestroyView()
    }
}