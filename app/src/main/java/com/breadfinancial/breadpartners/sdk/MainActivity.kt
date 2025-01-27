package com.breadfinancial.breadpartners.sdk

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.breadfinancial.breadpartners.sdk.core.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersSDKSetup
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle
import com.breadfinancial.breadpartners.sdk.core.models.TextPlacementStyling
import com.breadfinancial.breadpartners.sdk.core.models.TextViewFrame
import com.breadfinancial.breadpartners.sdk.databinding.ActivityMainBinding
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnerDefaults

class MainActivity : AppCompatActivity() {

    private fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay

        val metrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION") display.getMetrics(metrics)
        }

        return metrics.widthPixels
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAndFetchPlacementUI()
    }

    private fun setupAndFetchPlacementUI() {


        BreadPartnersSDK.getInstance().setup(
            sdkSetup = BreadPartnersSDKSetup(
                integrationKey = "", buyer = BreadPartnerDefaults().buyer, enableLog = true
            ), applicationContent = application
        )

        val textPlacementRequest = BreadPartnerDefaults().textPlacementRequestType1

        val styleStructSet = BreadPartnerDefaults().styleSet1

        val textPlacementStyling = TextPlacementStyling(
            normalFont = Typeface.BOLD,
            normalTextColor = styleStructSet.normalTextColor,
            clickableFont = Typeface.BOLD,
            clickableTextColor = styleStructSet.clickableTextColor,
            textViewFrame = TextViewFrame(
                width = getScreenWidth(this), height = 200
            )
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
            actionButtonColor = styleStructSet.actionButtonColor
        )

        val config = PlacementsConfiguration(
            configModel = textPlacementRequest,
            textPlacementStyling = textPlacementStyling,
            popUpStyling = popUpStyling,
        )

        BreadPartnersSDK.getInstance().registerPlacements(config, this) { event ->
            when (event) {
                is BreadPartnerEvent.RenderTextView -> {
                    val view = event.view
                    val textViewId = binding.interactiveTextOne.id
                    val textView = findViewById<TextView>(
                        textViewId
                    )
                    val parent = textView?.parent as ViewGroup
                    val index = parent.indexOfChild(textView)
                    parent.removeViewAt(index)
                    parent.addView(view, index)
                    print("BreadPartnerSDK::Successfully rendered view.")
                }

                is BreadPartnerEvent.RenderPopupView -> {
                    val view = event.view
                    showYesNoAlert(this) { userConfirmed ->
                        if (userConfirmed) {
                            view.show(this.supportFragmentManager, "PopupDialog")
                        } else {
                            println("User canceled No")
                        }
                    }
                    print("BreadPartnerSDK::Successfully rendered PopupView.")
                }

                BreadPartnerEvent.TextClicked -> {
                    print("BreadPartnerSDK::Text element was clicked!")
                }

                BreadPartnerEvent.ActionButtonTapped -> {
                    print("BreadPartnerSDK::Popup action button was tapped!")
                }

                is BreadPartnerEvent.ScreenName -> {
                    val name = event.name
                    print("BreadPartnerSDK::Screen name: ${name}")
                }

                is BreadPartnerEvent.WebViewSuccess -> {
                    val result = event.result
                    print("BreadPartnerSDK::WebView success with result: ${result}")
                }

                is BreadPartnerEvent.WebViewFailure -> {
                    val error = event.error
                    print(
                        "BreadPartnerSDK::WebView interaction failed with error: ${error}"
                    )
                }

                BreadPartnerEvent.PopupClosed -> {
                    print("BreadPartnerSDK::Popup closed!")
                }

                is BreadPartnerEvent.SdkError -> {
                    val error = event.error
                    print("BreadPartnerSDK::SDK encountered an error: ${error}")
                }
            }
        }

//        val config2 = PlacementsConfiguration(
//            configModel = BreadPartnerDefaults().textPlacementRequestType4,
//            textPlacementStyling = textPlacementStyling,
//            popUpStyling = popUpStyling,
//        )
//
//        BreadPartnersSDK.getInstance().submitRTPS(config2, this) { event ->
//            when (event) {
//                is BreadPartnerEvent.RenderPopupView -> {
//                    val view = event.view
//                    view.show(this.supportFragmentManager, "PopupDialog")
//                    print("BreadPartnerSDK::Successfully rendered PopupView.")
//                }
//
//                else -> {
//
//                }
//            }
//        }

    }
}


fun showYesNoAlert(context: Context, onResult: (Boolean) -> Unit) {
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
    builder.create().show()
}