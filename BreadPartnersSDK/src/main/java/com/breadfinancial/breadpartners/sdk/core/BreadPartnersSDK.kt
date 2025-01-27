package com.breadfinancial.breadpartners.sdk.core

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchBrandConfig
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersSDKSetup
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
    internal val htmlContentRenderer: HTMLContentRenderer by lazy {
        HTMLContentRenderer(
            htmlContentParser = htmlContentParser,
            analyticsManager = analyticsManager,
            logger = logger,
            apiClient = apiClient,
            alertHandler = alertHandler,
            commonUtils = commonUtils,
            callback = callback
        )
    }
    private val breadPartnerDefaults: BreadPartnerDefaults by lazy { BreadPartnerDefaults() }
    internal val coroutineScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main) }

    internal lateinit var application: Application
    internal lateinit var thisContext: AppCompatActivity
    internal var breadPartnersSDKSetup: BreadPartnersSDKSetup? = null
    internal var placementsConfiguration: PlacementsConfiguration? = null
    internal var brandConfiguration: BrandConfigResponse? = null
    internal var rtpsFlow: Boolean = false
    internal var prescreenId: String? = null

    fun setUpInjectables() {
        placementsConfiguration?.textPlacementStyling ?: run {
            placementsConfiguration?.textPlacementStyling =
                breadPartnerDefaults.textPlacementStyling
        }

        placementsConfiguration?.popUpStyling ?: run {
            placementsConfiguration?.popUpStyling = breadPartnerDefaults.popUpStyling
        }
        alertHandler.initialize(thisContext)
    }

    fun setup(sdkSetup: BreadPartnersSDKSetup, applicationContent: Application) {
        breadPartnersSDKSetup = sdkSetup
        logger.loggingEnabled = sdkSetup.enableLog
        this.application = applicationContent
    }

    fun registerPlacements(
        placementsConfiguration: PlacementsConfiguration,
        viewContext: AppCompatActivity,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.placementsConfiguration = placementsConfiguration
        this.callback = callback
        this.thisContext = viewContext
        setUpInjectables()
        fetchBrandConfig()
    }

    fun submitRTPS(
        placementsConfiguration: PlacementsConfiguration,
        viewContext: AppCompatActivity,
        callback: (BreadPartnerEvent) -> Unit
    ) {
        this.placementsConfiguration = placementsConfiguration
        this.callback = callback
        this.thisContext = viewContext
        this.rtpsFlow = true
        setUpInjectables()
        fetchBrandConfig()
    }
}
