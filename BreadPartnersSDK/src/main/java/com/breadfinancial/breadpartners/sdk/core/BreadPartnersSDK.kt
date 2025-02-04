package com.breadfinancial.breadpartners.sdk.core

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchBrandConfig
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchPlacementData
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersSetupConfig
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentParser
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentRenderer
import com.breadfinancial.breadpartners.sdk.htmlhandling.JsoupHTMLParser
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.security.RecaptchaManager
import com.breadfinancial.breadpartners.sdk.utilities.AlertHandler
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnerDefaults
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
    private val logger: Logger by lazy {
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
            coroutineScope = CoroutineScope(Dispatchers.IO), logger = logger
        )
    }
    internal val recaptchaManager: RecaptchaManager by lazy { RecaptchaManager() }
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


    private val breadPartnerDefaults: BreadPartnerDefaults by lazy { BreadPartnerDefaults() }
    internal val coroutineScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

    internal var htmlContentRenderer: HTMLContentRenderer? = null
    internal lateinit var application: Application
    internal lateinit var thisContext: AppCompatActivity

    internal var setupConfig: BreadPartnersSetupConfig? = null
    internal var placementsConfiguration: PlacementsConfiguration? = null
    internal var brandConfiguration: BrandConfigResponse? = null
    internal var rtpsFlow: Boolean = false
    internal var prescreenId: String? = null
    private var splitTextAndAction: Boolean = false

    private fun setUpInjectables() {

        placementsConfiguration?.popUpStyling ?: run {
            placementsConfiguration?.popUpStyling = breadPartnerDefaults.popUpStyling
        }
        placementsConfiguration?.popUpStyling ?: run {
            placementsConfiguration?.popUpStyling?.actionButtonStyle =
                breadPartnerDefaults.actionButtonStyle
        }
        alertHandler.initialize(thisContext)

        htmlContentRenderer = HTMLContentRenderer(
            integrationKey = integrationKey,
            htmlContentParser = htmlContentParser,
            analyticsManager = analyticsManager,
            logger = logger,
            apiClient = apiClient,
            alertHandler = alertHandler,
            commonUtils = commonUtils,
            callback = callback,
            setupConfig = setupConfig,
            placementsConfiguration = placementsConfiguration,
            brandConfiguration = brandConfiguration,
            splitTextAndAction = splitTextAndAction
        )
    }

    /**
     * Call this function when the app launches.
     *
     * @param integrationKey A unique key specific to the brand.
     * @param enableLog Set this to `true` if you want to see debug logs.
     * @param applicationContent `Application` context for the entire app.
     *
     */
    fun setup(integrationKey: String, enableLog: Boolean = false, applicationContent: Application) {
        this.integrationKey = integrationKey
        logger.loggingEnabled = enableLog
        this.application = applicationContent

        fetchBrandConfig()
    }

    /**
     * Use this function to display text placements in your app's UI.
     *
     * @param setupConfig Provide user account details in this configuration.
     * @param placementsConfiguration Specify the pre-defined placement details required for building the UI.
     * @param splitTextAndAction Set this to `true` if you want the placement to return either text with a link or a combination of text and button.
     * @param viewContext The current `AppCompatActivity` context where the view will be displayed.
     * @param callback A function that handles user interactions and ongoing events related to the placements.
     */
    fun registerPlacements(
        setupConfig: BreadPartnersSetupConfig,
        placementsConfiguration: PlacementsConfiguration,
        viewContext: AppCompatActivity,
        splitTextAndAction: Boolean = false,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.setupConfig = setupConfig
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
     *
     * @param setupConfig Provide user account details in this configuration.
     * @param placementsConfiguration Specify the pre-defined placement details required for building the UI.
     * @param viewContext The current `AppCompatActivity` context where the view will be displayed.
     * @param callback A function that handles user interactions and ongoing events related to the placements.
     */
    fun submitRTPS(
        setupConfig: BreadPartnersSetupConfig,
        placementsConfiguration: PlacementsConfiguration,
        viewContext: AppCompatActivity,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.setupConfig = setupConfig
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

//                        executeSecurityCheck()
//                        preScreenLookupCall(token= "")
        fetchPlacementData()

    }
}
