package com.breadfinancial.breadpartners.sdk.utilities

import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

object BreadPartnersExtensions {
    fun ViewGroup.replaceTextView(
        oldTextView: TextView?, newTextView: AppCompatTextView
    ) {
        val index = this.indexOfChild(oldTextView)
        if (index != -1 && oldTextView != null) {

            // Remove old view
            removeViewAt(index)

            // Configure the new TextView
            newTextView.apply {
                id = oldTextView.id


                Log.d("TextViewDebug", "New layoutParams without check: ${oldTextView.layoutParams}")
                // Ensure layoutParams are preserved and width/height are set to wrap_content
                this.layoutParams.apply {
                    width = oldTextView.layoutParams.width
                    height = oldTextView.layoutParams.height

                    if (this is ViewGroup.MarginLayoutParams) {
                        val oldMargins = oldTextView.layoutParams as? ViewGroup.MarginLayoutParams
                        oldMargins?.let {
                            // Copy margins from oldTextView to the new TextView
                            setMargins(
                                it.leftMargin, it.topMargin, it.rightMargin, it.bottomMargin
                            )
                        }
                    }
                }

                // Copy properties like gravity, text alignment
                gravity = (oldTextView as? AppCompatTextView)?.gravity ?: Gravity.START
                textAlignment = oldTextView.textAlignment

                // Copy padding, background, and visibility
                setPadding(
                    oldTextView.paddingLeft,
                    oldTextView.paddingTop,
                    oldTextView.paddingRight,
                    oldTextView.paddingBottom
                )
                background = oldTextView.background
                visibility = oldTextView.visibility

            }

            // Add the new view at the same index
            addView(newTextView, index)
        }
    }
}
