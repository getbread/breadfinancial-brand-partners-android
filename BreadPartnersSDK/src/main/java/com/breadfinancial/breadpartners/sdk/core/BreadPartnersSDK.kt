//------------------------------------------------------------------------------
//  File:          BreadPartnersSDK.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchBrandConfig
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchPlacementData
import com.breadfinancial.breadpartners.sdk.core.extensions.preScreenLookupCall
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
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
import java.util.UUID

/**
 * Primary interface class for interacting with the Bread Partners SDK.
 * Provides entry points for initialization, configuration, and SDK-level actions.
 */
class BreadPartnersSDK private constructor() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: BreadPartnersSDK? = null

        fun getInstance(): BreadPartnersSDK {
            if (instance == null) {
                instance = BreadPartnersSDK()
            }
            return instance!!
        }
    }

    internal lateinit var integrationKey: String

    lateinit var callback: ((BreadPartnerEvent) -> Unit?)

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
    val htmlContentParser: HTMLContentParser by lazy {
        HTMLContentParser(
            alertHandler = alertHandler, htmlParser = jsoupHTMLParser
        )
    }

    internal val coroutineScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

    internal var htmlContentRenderer: HTMLContentRenderer? = null
    internal lateinit var application: Application
    internal lateinit var thisContext: Context

    internal var sdkEnvironment: BreadPartnersEnvironment = BreadPartnersEnvironment.STAGE
    internal var merchantConfiguration: MerchantConfiguration? = null
    internal var placementsConfiguration: PlacementsConfiguration? = null
    internal var brandConfiguration: BrandConfigResponse? = null
    internal var openPlacementExperience: Boolean = false
    internal var rtpsFlow: Boolean = false
    internal var prescreenId: Long? = null
    private var splitTextAndAction: Boolean = false

    private fun setUpInjectables() {

        logger.callback = callback

        if (brandConfiguration == null) {
            callback.invoke(
                BreadPartnerEvent.SdkError(
                    error = Exception("Brand configurations are missing or unavailable.")
                )
            )
        }

        merchantConfiguration?.env = sdkEnvironment

        alertHandler.initialize(
            context = thisContext, rtpsFlow = rtpsFlow, logger = logger, callback = callback
        )

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
        environment: BreadPartnersEnvironment = BreadPartnersEnvironment.PROD,
        integrationKey: String,
        enableLog: Boolean = false,
        application: Application
    ) {
        APIUrl.setEnvironment(environment)
        sdkEnvironment = environment
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
        this.rtpsFlow = false
        this.openPlacementExperience = false

        setUpInjectables()

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
        this.openPlacementExperience = false

        setUpInjectables()

//        executeSecurityCheck()
        preScreenLookupCall(token = UUID.randomUUID().toString())
    }

    /**
     * Display an overlay to the customer without requiring them to click on a placement to trigger it.
     *
     * @param merchantConfiguration Provide user account details in this configuration.
     * @param placementsConfiguration Specify the pre-defined placement details required for building the UI.
     * @param viewContext The current `Activity` context where the view will be displayed.
     * @param callback A function that handles user interactions and ongoing events related to the placements.
     */
    fun openExperienceForPlacement(
        merchantConfiguration: MerchantConfiguration,
        placementsConfiguration: PlacementsConfiguration,
        viewContext: Context,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.merchantConfiguration = merchantConfiguration
        this.placementsConfiguration = placementsConfiguration
        this.callback = callback
        this.thisContext = viewContext
        this.rtpsFlow = false
        this.openPlacementExperience = true

        setUpInjectables()

        fetchPlacementData()

    }
}
