package com.breadfinancial.breadpartners.sdk.htmlhandling.extensions

import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentRenderer

fun HTMLContentRenderer.createPlainTextView(): TextView {
    val textView = TextView(thisContext).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(Color.TRANSPARENT)
        text = textPlacementModel!!.contentText ?: ""
    }
    return textView
}

fun HTMLContentRenderer.createActionButton(): Button {
    textPlacementModel!!

    val button = Button(thisContext).apply {
        text = textPlacementModel?.actionLink ?: ""
        contentDescription = textPlacementModel?.actionLink ?: ""
        setOnClickListener { handleButtonTap(this) }
    }

    return button
}