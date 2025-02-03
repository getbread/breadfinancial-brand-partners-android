package com.breadfinancial.breadpartners.sdk

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupActionButtonStyle
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle
import com.breadfinancial.breadpartners.sdk.core.models.StyleStruct
import com.breadfinancial.breadpartners.sdk.core.models.TextPlacementStyling
import com.breadfinancial.breadpartners.sdk.core.models.ViewFrame
import com.breadfinancial.breadpartners.sdk.databinding.ActivityMainBinding
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnerDefaults
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceButton
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceTextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var styleStructSet: StyleStruct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAndFetchPlacementUI()
    }

    private fun setupAndFetchPlacementUI() {

        BreadPartnersSDK.getInstance().setup(
            setupConfig = BreadPartnerDefaults().setupConfig1, applicationContent = application
        )

        styleStructSet = BreadPartnerDefaults().styleSet3

        val textPlacementStyling = TextPlacementStyling(
            normalFont = Typeface.BOLD,
            normalTextColor = styleStructSet.normalTextColor,
            clickableFont = Typeface.BOLD,
            clickableTextColor = styleStructSet.clickableTextColor,
            textViewFrame = ViewFrame(
                width = ViewGroup.LayoutParams.MATCH_PARENT, height = 200
            ),
            buttonTextColor = Color.WHITE,
            buttonFrame = ViewFrame(width = 300, height = 55),
            buttonPadding = Rect(10, 0, 10, 0),
            buttonBackgroundColor = styleStructSet.clickableTextColor,
            buttonCornerRadius = 60.0F,
            buttonTextSize = 12.0F,
        )

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
            textPlacementStyling = textPlacementStyling,
            popUpStyling = popUpStyling,
        )

        BreadPartnersSDK.getInstance()
            .registerPlacements(config, this, splitTextAndAction = true) { event ->
                when (event) {
                    is BreadPartnerEvent.RenderTextViewWithLink -> {
                        val textView = binding.interactiveTextOne
                        val parent = textView.parent as ViewGroup
                        parent.replaceTextView(
                            textView, event.appCompatTextView
                        )

                        Log.i("BreadPartnerSDK", "::Successfully RenderTextViewWithLink.")
                    }

                    is BreadPartnerEvent.RenderSeparateTextAndButton -> {
                        val textView = binding.interactiveTextOne
                        val buttonView = binding.actionButton
                        buttonView.visibility = View.VISIBLE
                        val parent = textView.parent as ViewGroup
                        parent.replaceTextView(
                            textView, event.textView
                        )
                        parent.replaceButton(
                            buttonView, event.button
                        )

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
        BreadPartnersSDK.getInstance().setup(
            setupConfig = BreadPartnerDefaults().setupConfig2, applicationContent = application
        )

        val config2 = PlacementsConfiguration(
            rtpsConfig = BreadPartnerDefaults().rtpsConfig1,
            textPlacementStyling = null,
            popUpStyling = null,
        )

        BreadPartnersSDK.getInstance().submitRTPS(config2, this) { event ->
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
