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
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersAddress
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersBuyer
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersMockOptions
import com.breadfinancial.breadpartners.sdk.core.models.PlacementData
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersRtpsConfig
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.CurrencyValue
import com.breadfinancial.breadpartners.sdk.core.models.BreadSDKEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.FinancingType
import com.breadfinancial.breadpartners.sdk.core.models.LocationType
import com.breadfinancial.breadpartners.sdk.core.models.Name
import com.breadfinancial.breadpartners.sdk.core.models.Order
import com.breadfinancial.breadpartners.sdk.core.models.PickupInformation
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupActionButtonStyle
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle
import com.breadfinancial.breadpartners.sdk.core.models.ViewFrame
import com.breadfinancial.breadpartners.sdk.databinding.ActivityMainBinding
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnerDefaults
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceButton
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.replaceTextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var style: Map<String, Any>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAndFetchPlacementUI()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupAndFetchPlacementUI() {

        // MARK:For development purposes
        // These configurations are used to test different types of placement requests.
        // Each placement type corresponds to a specific configuration that includes parameters
        // like placement ID, SDK transaction ID, environment, price, and brand ID.
        // This allows testing of various placement setups by fetching specific configurations
        // based on the placement type key.
        val placementRequestType =
            BreadPartnerDefaults.shared.placementConfigurations["textPlacementRequestType1"]
        val placementID = placementRequestType!!["placementID"] as String
        val price = placementRequestType["price"] as Int
        val brandId = placementRequestType["brandId"] as String

        // MARK: For development purposes
        style = BreadPartnerDefaults.shared.styleStruct["cadet"]!!

        val primaryColor = style["primaryColor"] as String
        val secondaryColor = style["secondaryColor"] as String
        val tertiaryColor = style["tertiaryColor"] as String
        val blackColor = "#000000"

        val fontFamily = style["fontFamily"] as String

        val smallTextSize = style["small"] as Int
        val mediumTextSize = style["medium"] as Int
        val largeTextSize = style["large"] as Int
        val xlargeTextSize = style["xlarge"] as Int

        val customFont = ResourcesCompat.getFont(
            this, this.resources.getIdentifier(fontFamily, "font", this.packageName)
        )

        binding.preScreenBtn.typeface = customFont
        binding.preScreenBtn.textSize = largeTextSize.toFloat()
        binding.preScreenBtn.setTextColor(Color.parseColor(primaryColor))

        /**
         * Call this function when the app launches.
         *
         * @param breadSDKEnvironment Specifies the environment in which the SDK will operate (e.g., STAGE, PROD).
         * @param enableLog Determines whether logging is enabled for debugging purposes.
         * @param integrationKey A unique identifier for the brand.
         * @param applicationContent The application context required for SDK initialization.
         */
        BreadPartnersSDK.getInstance().setup(
            breadSDKEnvironment = BreadSDKEnvironment.STAGE,
            enableLog = true,
            integrationKey = brandId,
            applicationContent = application
        )

        /**
         * Prepares a popup styling configuration object for each style element.
         */
        val popUpStyling = PopUpStyling(
            loaderColor = Color.parseColor(primaryColor),
            crossColor = Color.parseColor(primaryColor),
            dividerColor = Color.parseColor(tertiaryColor),
            borderColor = Color.parseColor(tertiaryColor),
            titlePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.BOLD
                ), textColor = Color.parseColor(blackColor), textSize = xlargeTextSize.toFloat()
            ),
            subTitlePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.NORMAL
                ), textColor = Color.parseColor(blackColor), textSize = mediumTextSize.toFloat()
            ),
            headerPopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.BOLD
                ), textColor = Color.parseColor(blackColor), textSize = mediumTextSize.toFloat()
            ),
            headerBgColor = Color.parseColor(tertiaryColor),
            headingThreePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.BOLD
                ), textColor = Color.parseColor(primaryColor), textSize = largeTextSize.toFloat()
            ),
            paragraphPopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.BOLD
                ), textColor = Color.parseColor(secondaryColor), textSize = smallTextSize.toFloat()
            ),
            connectorPopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.BOLD
                ), textColor = Color.parseColor(primaryColor), textSize = smallTextSize.toFloat()
            ),
            disclosurePopupTextStyle = PopupTextStyle(
                font = Typeface.create(
                    fontFamily, Typeface.BOLD
                ), textColor = Color.parseColor(secondaryColor), textSize = smallTextSize.toFloat()
            ),
            actionButtonStyle = PopupActionButtonStyle(
                font = Typeface.BOLD,
                textColor = Color.WHITE,
                frame = ViewFrame(width = 100, height = 50),
                padding = Rect(2, 2, 2, 2),
                backgroundColor = Color.parseColor(primaryColor),
                cornerRadius = 60.0F
            )
        )

        /**
         * Configuration for defining placement options in BreadPartners.
         *
         * Modify the Placement ID and total price to test different placements.
         */
        val placementData = PlacementData(
            financingType = FinancingType.INSTALLMENTS,
            locationType = LocationType.CATEGORY,
            placementId = placementID,
            domID = "123",
            order = Order(
                subTotal = CurrencyValue(currency = "USD", value = 0.0),
                totalDiscounts = CurrencyValue(currency = "USD", value = 0.0),
                totalPrice = CurrencyValue(currency = "USD", value = price.toDouble()),
                totalShipping = CurrencyValue(currency = "USD", value = 0.0),
                totalTax = CurrencyValue(currency = "USD", value = 0.0),
                discountCode = "string",
                pickupInformation = PickupInformation(
                    name = Name(
                        givenName = "John", familyName = "Doe"
                    ), phone = "+14539842345", address = BreadPartnersAddress(
                        address1 = "156 5th Avenue",
                        locality = "New York",
                        postalCode = "10019",
                        region = "US-NY",
                        country = "US"
                    ), email = "john.doe@gmail.com"
                ),
                fulfillmentType = "type",
                items = emptyList()
            )
        )

        val placementsConfiguration = PlacementsConfiguration(
            placementData = placementData,
            popUpStyling = null
        )

        val merchantConfiguration = MerchantConfiguration(
            buyer = BreadPartnersBuyer(
                givenName = "Jack",
                familyName = "Seamus",
                additionalName = "C.",
                birthDate = "1974-08-21",
                email = "johncseamus@gmail.com",
                phone = "+13235323423",
                billingAddress = BreadPartnersAddress(
                    address1 = "323 something lane",
                    address2 = "apt. B",
                    country = "USA",
                    locality = "NYC",
                    region = "NY",
                    postalCode = "11222"
                ),
                shippingAddress = null
            ),
            loyaltyID = "xxxxxx",
            storeNumber = "1234567",
            env = "STAGE",
            channel = "P",
            subchannel = "X"
        )

        BreadPartnersSDK.getInstance().registerPlacements(
            merchantConfiguration = merchantConfiguration,
            placementsConfiguration = placementsConfiguration,
            viewContext = this,
            splitTextAndAction = true
        ) { event ->
            when (event) {
                is BreadPartnerEvent.RenderTextViewWithLink -> {

                    /**
                     * Handles rendering of a text view with a clickable link.
                     *
                     * - Modifies the font, text color, and link color for the text view.
                     * - Adds the text view to the main view and sets up its layout constraints.
                     */

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
                            ForegroundColorSpan(Color.parseColor(primaryColor)),
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
                    textView.typeface = customFont
                    textView.movementMethod = LinkMovementMethod.getInstance()
                }


                is BreadPartnerEvent.RenderSeparateTextAndButton -> {

                    /**
                     * Handles rendering of a popup view.
                     *
                     * - Modifies the font, text color for the text view, and the button's title color, font, and background.
                     * - Adds the text view and button to the main view and sets up their layout constraints.
                     */
                    event.textView.apply {
                        setTextColor(Color.BLACK)
                        textSize = 24f
                        typeface = customFont
                    }

                    event.button.apply {
                        setTextColor(Color.WHITE)
                        textSize = 24f
                        typeface = customFont
                        setPadding(50, 0, 50, 0)

                        val drawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 55.0f
                            setColor(Color.parseColor(primaryColor))
                        }

                        val pressedDrawable = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = 55.0f
                            setColor(Color.parseColor(primaryColor))
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

                }

                is BreadPartnerEvent.RenderPopupView -> {

                    /**
                     * Handles rendering of a popup view.
                     *
                     * Example:
                     * Implement some process prior to loading the WebView popup
                     * (e.g., checking if the customer is authenticated).
                     */
                    val view = event.dialogFragment
                    showYesNoAlert(this) { userConfirmed ->
                        if (userConfirmed) {
                            view.show(this.supportFragmentManager, "PopupDialog")
                        } else {
                            println("User canceled No")
                        }
                    }
                }

                else -> {
                    Log.i("BreadPartnerSDK::", "Event:${event}")
                }
            }
        }

    }

    fun preScreenCheck(view: View) {
        val rtpsConfig = BreadPartnersRtpsConfig(
            locationType = LocationType.CHECKOUT,
            order = Order(
                totalPrice = CurrencyValue(
                    currency = "USD",
                    value = 5000.0
                )
            ),
            mockResponse = BreadPartnersMockOptions.SUCCESS
        )

        val placementsConfiguration = PlacementsConfiguration(
            rtpsConfig = rtpsConfig,
            popUpStyling = null,
        )

        val merchantConfiguration = MerchantConfiguration(
            buyer = BreadPartnersBuyer(
                givenName = "Jack",
                familyName = "Seamus",
                additionalName = "C.",
                birthDate = "1974-08-21",
                email = "johncseamus@gmail.com",
                phone = "+13235323423",
                billingAddress = BreadPartnersAddress(
                    address1 = "323 something lane",
                    address2 = "apt. B",
                    country = "USA",
                    locality = "NYC",
                    region = "NY",
                    postalCode = "11222"
                ),
                shippingAddress = null
            ),
            loyaltyID = "xxxxxx",
            storeNumber = "1234567",
            env = "STAGE",
            channel = "P",
            subchannel = "X"
        )

        BreadPartnersSDK.getInstance().silentRTPSRequest(
            merchantConfiguration = merchantConfiguration,
            placementsConfiguration = placementsConfiguration,
            viewContext = this
        ) { event ->
            when (event) {
                is BreadPartnerEvent.RenderPopupView -> {
                    val view = event.dialogFragment
                    view.show(this.supportFragmentManager, "PopupDialog")
                    Log.i("BreadPartnerSDK::", "Successfully rendered PopupView.")
                }

                else -> {
                    Log.i("BreadPartnerSDK::", "Event:${event}")
                }
            }
        }
    }

    private fun showYesNoAlert(context: Context, onResult: (Boolean) -> Unit) {
        val primaryColor = style["primaryColor"] as String
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
            ?.setTextColor(Color.parseColor(primaryColor))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(Color.parseColor(primaryColor))
    }
}

