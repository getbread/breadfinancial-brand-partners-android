package com.breadfinancial.breadpartners.sdk

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupActionButtonStyle
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle
import com.breadfinancial.breadpartners.sdk.core.models.StyleStruct
import com.breadfinancial.breadpartners.sdk.core.models.ViewFrame
import com.breadfinancial.breadpartners.sdk.databinding.ActivityMainBinding
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnerDefaults
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceButton
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceTextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var styleStructSet: StyleStruct

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAndFetchPlacementUI()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAndFetchPlacementUI() {

        BreadPartnersSDK.getInstance().setup(
            enableLog = true,
            integrationKey = "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7",
            applicationContent = application
        )

        styleStructSet = BreadPartnerDefaults().styleSet3

        val popUpStyling = PopUpStyling(
            loaderColor = styleStructSet.loaderColor,
            crossColor = styleStructSet.crossColor,
            dividerColor = styleStructSet.dividerColor,
            borderColor = styleStructSet.borderColor,
            titlePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.BOLD
                ), textColor = styleStructSet.titleTextColor, textSize = styleStructSet.textSizeBold
            ),
            subTitlePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.NORMAL
                ),
                textColor = styleStructSet.subTitleTextColor,
                textSize = styleStructSet.textSizeRegular
            ),
            headerPopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.BOLD
                ),
                textColor = styleStructSet.headerTextColor,
                textSize = styleStructSet.textSizeSemiBold
            ),
            headerBgColor = styleStructSet.headerBgColor,
            headingThreePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.BOLD
                ),
                textColor = styleStructSet.parsedRedColor,
                textSize = styleStructSet.textSizeSemiBold
            ),
            paragraphPopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.BOLD
                ),
                textColor = styleStructSet.paragraphTextColor,
                textSize = styleStructSet.textSizeSmall
            ),
            connectorPopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.BOLD
                ),
                textColor = styleStructSet.connectorTextColor,
                textSize = styleStructSet.textSizeSemiBold
            ),
            disclosurePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    styleStructSet.baseFontFamily, Typeface.BOLD
                ),
                textColor = styleStructSet.disclosureTextColor,
                textSize = styleStructSet.textSizeSmall
            ),
            actionButtonStyle = PopupActionButtonStyle(
                font = Typeface.BOLD,
                textColor = Color.WHITE,
                frame = ViewFrame(width = 100, height = 50),
                padding = Rect(2, 2, 2, 2),
                backgroundColor = styleStructSet.clickableTextColor,
                cornerRadius = 60.0F
            )
        )

        val config = PlacementsConfiguration(
            placementConfig = BreadPartnerDefaults().placementConfig1,
            popUpStyling = popUpStyling,
        )

        BreadPartnersSDK.getInstance().registerPlacements(
            setupConfig = BreadPartnerDefaults().setupConfig1,
            config,
            this,
            splitTextAndAction = false
        ) { event ->
            when (event) {
                is BreadPartnerEvent.RenderTextViewWithLink -> {
                    val textView = binding.textView

                    val spannable = event.spannableText

                    val clickableSpans =
                        spannable.getSpans(0, spannable.length, ClickableSpan::class.java)
                    val normalTextEndIndex =
                        clickableSpans.firstOrNull()?.let { spannable.getSpanStart(it) } ?: 0

                    spannable.apply {
                        setSpan(
                            ForegroundColorSpan(Color.BLACK),
                            0,
                            normalTextEndIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        setSpan(
                            AbsoluteSizeSpan(17, true),
                            0,
                            normalTextEndIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        setSpan(
                            ForegroundColorSpan(styleStructSet.clickableTextColor),
                            normalTextEndIndex,
                            spannable.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        setSpan(
                            AbsoluteSizeSpan(17, true),
                            normalTextEndIndex,
                            spannable.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    textView.text = spannable
                    textView.movementMethod = LinkMovementMethod.getInstance()

                    Log.i("BreadPartnerSDK", "::Successfully RenderTextViewWithLink.")
                }


                is BreadPartnerEvent.RenderSeparateTextAndButton -> {
                    val customFont = ResourcesCompat.getFont(
                        this,
                        this.resources.getIdentifier("josefinsans_bold", "font", this.packageName)
                    )
                    event.textView.apply {
                        setTextColor(Color.BLACK)
                        textSize = 24f
                        typeface = customFont
                    }

                    event.button.apply {
                        setTextColor(Color.WHITE)
                        textSize = 24f
                        typeface = customFont
                        setPadding(50, 10, 50, 25)

                        val drawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 55.0f
                            setColor(styleStructSet.clickableTextColor)
                        }

                        val pressedDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 55.0f
                            setColor(styleStructSet.clickableTextColor)
                        }

                        val states = StateListDrawable().apply {
                            addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
                            addState(intArrayOf(), drawable)
                        }

                        background = states
                    }

                    binding.actionButton.visibility = View.VISIBLE
                    val parent = binding.textView.parent as ViewGroup
                    parent.replaceTextView(binding.textView, event.textView)
                    parent.replaceButton(binding.actionButton, event.button)

                    Log.i("BreadPartnerSDK", "::Successfully RenderSeparateTextAndButton.")
                }

                is BreadPartnerEvent.RenderPopupView -> {
                    val view = event.dialogFragment
                    showYesNoAlert(this) { userConfirmed ->
                        if (userConfirmed) {
                            view.show(this.supportFragmentManager, "PopupDialog")
                        } else {
                            println("User canceled No")
                        }
                    }
                    Log.i("BreadPartnerSDK", "::Successfully rendered PopupView.")
                }

                else -> {
                    Log.i("BreadPartnerSDK", "::Event:${event}")
                }
            }
        }

    }

    fun preScreenCheck(view: View) {
        val config2 = PlacementsConfiguration(
            rtpsConfig = BreadPartnerDefaults().rtpsConfig1,
            popUpStyling = null,
        )

        BreadPartnersSDK.getInstance().submitRTPS(
            setupConfig = BreadPartnerDefaults().setupConfig1, config2, this
        ) { event ->
            when (event) {
                is BreadPartnerEvent.RenderPopupView -> {
                    val view = event.dialogFragment
                    view.show(this.supportFragmentManager, "PopupDialog")
                    Log.i("BreadPartnerSDK", "::Successfully rendered PopupView.")
                }

                else -> {

                }
            }
        }
    }

    private fun showYesNoAlert(context: Context, onResult: (Boolean) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you authenticated?")

        builder.setPositiveButton("Yes") { dialog: DialogInterface, _ ->
            onResult(true)
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog: DialogInterface, _ ->
            onResult(false)
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(styleStructSet.clickableTextColor)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(styleStructSet.clickableTextColor)
    }
}

