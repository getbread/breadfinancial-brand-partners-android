package com.breadfinancial.breadpartners.sdk.core

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.extensions.executeSecurityCheck
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchBrandConfig
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchPlacementData
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadSDKEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupActionButtonStyle
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle
import com.breadfinancial.breadpartners.sdk.core.models.ViewFrame
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentParser
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentRenderer
import com.breadfinancial.breadpartners.sdk.htmlhandling.JsoupHTMLParser
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.security.RecaptchaManager
import com.breadfinancial.breadpartners.sdk.utilities.AlertHandler
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import com.breadfinancial.breadpartners.sdk.utilities.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class BreadPartnersSDK private constructor() {
    companion object {
        private var instance: BreadPartnersSDK? = null

        fun getInstance(): BreadPartnersSDK {
            if (instance == null) {
                instance = BreadPartnersSDK()
            }
            return instance!!
        }
    }

    internal lateinit var integrationKey: String
    private var enableLog: Boolean = false

    private lateinit var callback: ((BreadPartnerEvent) -> Unit?)
    val logger: Logger by lazy {
        Logger(
            outputStream = System.out, isLoggingEnabled = true
        )
    }
    internal val alertHandler: AlertHandler by lazy { AlertHandler(contextRef = null) }
    internal val commonUtils: CommonUtils by lazy {
        CommonUtils(
            handler = Handler(Looper.getMainLooper()), alertHandler = alertHandler
        )
    }
    internal val apiClient: APIClient by lazy {
        APIClient(
            coroutineScope = CoroutineScope(Dispatchers.IO),
            logger = logger,
            commonUtils = commonUtils
        )
    }
    internal val recaptchaManager: RecaptchaManager by lazy { RecaptchaManager(logger = logger) }
    private val analyticsManager: AnalyticsManager by lazy {
        AnalyticsManager(
            apiClient = apiClient, commonUtils = commonUtils
        )
    }
    private val jsoupHTMLParser: JsoupHTMLParser by lazy { JsoupHTMLParser() }
    private val htmlContentParser: HTMLContentParser by lazy {
        HTMLContentParser(
            alertHandler = alertHandler, htmlParser = jsoupHTMLParser
        )
    }

    internal val coroutineScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

    internal var htmlContentRenderer: HTMLContentRenderer? = null
    internal lateinit var application: Application
    internal lateinit var thisContext: Context

    internal var merchantConfiguration: MerchantConfiguration? = null
    internal var placementsConfiguration: PlacementsConfiguration? = null
    internal var brandConfiguration: BrandConfigResponse? = null
    internal var rtpsFlow: Boolean = false
    internal var prescreenId: Int? = null
    private var splitTextAndAction: Boolean = false

    private fun setUpInjectables() {

        // region Default Popup action button Style
        val actionButtonStyle = PopupActionButtonStyle(
            font = Typeface.BOLD,
            textColor = Color.WHITE,
            frame = ViewFrame(width = 200, height = 50),
            backgroundColor = Color.parseColor("#d50132"),
            cornerRadius = 8.0F,
            padding = Rect(16, 8, 16, 8)
        )
        // endregion

        // region Default Popup Style
        val popUpStyling = PopUpStyling(
            loaderColor = Color.parseColor("#0f2233"),
            crossColor = Color.BLACK,
            dividerColor = Color.parseColor("#ececec"),
            borderColor = Color.parseColor("#ececec"),
            titlePopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.BLACK,
                textSize = 16.0f
            ),
            subTitlePopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.GRAY,
                textSize = 12.0f
            ),
            headerPopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.GRAY,
                textSize = 14.0f
            ),
            headerBgColor = Color.parseColor("#ececec"),
            headingThreePopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.parseColor("#d50132"),
                textSize = 14.0f
            ),
            paragraphPopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.GRAY,
                textSize = 10.0f
            ),
            connectorPopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.BLACK,
                textSize = 14.0f
            ),
            disclosurePopupTextStyle = PopupTextStyle(
                font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
                textColor = Color.GRAY,
                textSize = 10.0f
            ),
            actionButtonStyle = actionButtonStyle
        )
        //endregion

        placementsConfiguration?.popUpStyling ?: run {
            placementsConfiguration?.popUpStyling = popUpStyling
        }
        placementsConfiguration?.popUpStyling ?: run {
            placementsConfiguration?.popUpStyling?.actionButtonStyle = actionButtonStyle
        }
        alertHandler.initialize(context = thisContext)

        analyticsManager.setApiKey(integrationKey)

        htmlContentRenderer = HTMLContentRenderer(
            integrationKey = integrationKey,
            htmlContentParser = htmlContentParser,
            analyticsManager = analyticsManager,
            logger = logger,
            apiClient = apiClient,
            alertHandler = alertHandler,
            commonUtils = commonUtils,
            callback = callback,
            merchantConfiguration = merchantConfiguration,
            placementsConfiguration = placementsConfiguration,
            brandConfiguration = brandConfiguration,
            splitTextAndAction = splitTextAndAction
        )
    }

    /**
     * Call this function when the app launches.
     *
     * @param environment Specifies the SDK environment, such as production (.prod) or development (stage).
     * @param integrationKey A unique key specific to the brand.
     * @param enableLog Set this to `true` if you want to see debug logs.
     * @param application `Application` context for the entire app.
     *
     */
    fun setup(
        environment: BreadSDKEnvironment = BreadSDKEnvironment.PROD,
        integrationKey: String,
        enableLog: Boolean = false,
        application: Application
    ) {
        APIUrl.setEnvironment(environment)
        this.integrationKey = integrationKey
        logger.loggingEnabled = enableLog
        this.application = application

        fetchBrandConfig()
    }

    /**
     * Use this function to display text placements in your app's UI.
     *
     * @param merchantConfiguration Provide user account details in this configuration.
     * @param placementsConfiguration Specify the pre-defined placement details required for building the UI.
     * @param splitTextAndAction Set this to `true` if you want the placement to return either text with a link or a combination of text and button.
     * @param viewContext The current `Activity` context where the view will be displayed.
     * @param callback A function that handles user interactions and ongoing events related to the placements.
     */
    fun registerPlacements(
        merchantConfiguration: MerchantConfiguration,
        placementsConfiguration: PlacementsConfiguration,
        viewContext: Context,
        splitTextAndAction: Boolean = false,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.merchantConfiguration = merchantConfiguration
        this.placementsConfiguration = placementsConfiguration
        this.splitTextAndAction = splitTextAndAction
        this.callback = callback
        this.thisContext = viewContext

        setUpInjectables()

        if (brandConfiguration == null) {
            callback.invoke(
                BreadPartnerEvent.SdkError(
                    error = Exception("Brand configurations are missing or unavailable.")
                )
            )
        }

        fetchPlacementData()

    }

    /**
     * Call this function to check if the user qualifies for a pre-screen card application.
     * This call will be completely silent, with no impact on user behavior.
     * Everything will be managed within the SDK, and the brand partner's app only needs to send data.
     * If any step fails within the RTPS flow, the user will not experience any UI behavior changes.
     * If RTPS succeeds, a popup should be displayed via the callback to show the "Approved" flow.
     *
     * @param merchantConfiguration Provide user account details in this configuration.
     * @param placementsConfiguration Specify the pre-defined placement details required for building the UI.
     * @param viewContext The current `AppCompatActivity` context where the view will be displayed.
     * @param callback A function that handles user interactions and ongoing events related to the placements.
     */
    fun silentRTPSRequest(
        merchantConfiguration: MerchantConfiguration,
        placementsConfiguration: PlacementsConfiguration,
        viewContext: Context,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.merchantConfiguration = merchantConfiguration
        this.placementsConfiguration = placementsConfiguration
        this.callback = callback
        this.thisContext = viewContext
        this.rtpsFlow = true

        setUpInjectables()

        if (brandConfiguration == null) {
            callback.invoke(
                BreadPartnerEvent.SdkError(
                    error = Exception("Brand configurations are missing or unavailable.")
                )
            )
        }

        executeSecurityCheck()
    }
}
