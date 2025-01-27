package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.breadfinancial.breadpartners.sdk.core.models.TextPlacementStyling
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.TextPlacementModel

class InteractiveText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var tapHandler: ((String) -> Unit)? = null

    init {
        isClickable = true
        movementMethod = LinkMovementMethod.getInstance()
        isFocusableInTouchMode = true
    }

    fun configure(
        textPlacementModel: TextPlacementModel,
        textPlacementStyling: TextPlacementStyling,
        link: ((String) -> Unit)? = null
    ) {
        this.tapHandler = link

        val normalText = textPlacementModel.contentText ?: ""
        val clickableText = textPlacementModel.actionLink ?: ""

        val spannableContent = createSpannableText(normalText, clickableText, textPlacementStyling)

        text = spannableContent
    }

    private fun createSpannableText(
        normalText: String, clickableText: String, textPlacementStyling: TextPlacementStyling
    ): Spannable {

        textSize = 16.0F

        setTextColor(textPlacementStyling.normalTextColor)

        val layoutParams = LinearLayout.LayoutParams(
            (textPlacementStyling.textViewFrame.width), (textPlacementStyling.textViewFrame.height)
        )
        this.layoutParams = layoutParams

        setLinkTextColor(textPlacementStyling.clickableTextColor)

        val spannableString = SpannableString(normalText + clickableText)

        val normalTextEndIndex = normalText.length
        spannableString.setSpan(
            StyleSpan(textPlacementStyling.normalFont),
            0,
            normalTextEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableTextEndIndex = normalTextEndIndex + clickableText.length

        spannableString.setSpan(
            StyleSpan(textPlacementStyling.clickableFont),
            normalTextEndIndex,
            clickableTextEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    tapHandler?.invoke(clickableText)
                    (widget as AppCompatTextView).clearFocus()
                    widget.invalidate()
                }
            }, normalTextEndIndex, clickableTextEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

}
