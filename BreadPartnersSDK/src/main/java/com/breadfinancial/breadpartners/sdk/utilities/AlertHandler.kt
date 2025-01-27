package com.breadfinancial.breadpartners.sdk.utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import android.widget.TextView

class AlertHandler(
    private var contextRef: Context? = null // Now context is directly injected
) {

    private var alertDialog: AlertDialog? = null

    // Initialize with a context, can be injected via constructor
    fun initialize(context: Context) {
        contextRef = context
    }

    fun showAlert(
        title: String,
        message: String,
        showOkButton: Boolean = false
    ) {
        val context = contextRef

        // Ensure context is not null and is a valid Activity
        if (context !is Activity || context.isFinishing || context.isDestroyed) {
            throw IllegalStateException("Invalid or inactive context. Ensure the Activity is running.")
        }

        alertDialog?.takeIf { it.isShowing }?.dismiss()

        presentAlert(context, title, message, showOkButton)
    }

    private fun presentAlert(
        context: Context,
        title: String,
        message: String,
        showOkButton: Boolean
    ) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(title)
            .setMessage(message)
            .setCancelable(true)

        if (showOkButton) {
            builder.setPositiveButton("OK") { _, _ -> }
        }

        alertDialog = builder.create()

        alertDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Apply rounded corners using a shape drawable
        val cornerRadius = 20f
        val radii = FloatArray(8) { cornerRadius }
        val roundRectShape = RoundRectShape(radii, null, null)
        val shapeDrawable = ShapeDrawable(roundRectShape)
        shapeDrawable.paint.color = Color.WHITE

        // Set the background drawable for the root view of the alert dialog
        alertDialog?.window?.decorView?.background = shapeDrawable

        // Align title, message, and button to the center
        val titleTextView = alertDialog?.findViewById<TextView>(android.R.id.title)
        val messageTextView = alertDialog?.findViewById<TextView>(android.R.id.message)
        val positiveButton = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)

        titleTextView?.gravity = Gravity.CENTER
        messageTextView?.gravity = Gravity.CENTER
        positiveButton?.gravity = Gravity.CENTER

        alertDialog?.show()
    }

    fun hideAlert() {
        alertDialog?.takeIf { it.isShowing }?.dismiss()
    }
}
