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
import android.util.Log
import com.breadfinancial.breadpartners.sdk.core.extensions.fetchPlacementData
import com.breadfinancial.breadpartners.sdk.core.extensions.preScreenLookupCall
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.APIUrl
import com.breadfinancial.breadpartners.sdk.networking.APIUrlType
import com.breadfinancial.breadpartners.sdk.networking.HTTPMethod
import com.breadfinancial.breadpartners.sdk.networking.Result
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnerDefaults
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    internal lateinit var application: Application
    internal var brandConfiguration: BrandConfigResponse? = null
    internal var sdkEnvironment: BreadPartnersEnvironment = BreadPartnersEnvironment.STAGE
    internal var enableLog: Boolean = false

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
        this.integrationKey = integrationKey
        this.application = application
        this.sdkEnvironment = environment
        this.enableLog = enableLog
        fetchBrandConfig()
    }

    /**
     * Retrieves brand-specific configurations, such as the Recaptcha key.
     */
    private fun fetchBrandConfig() {
        val apiUrl = APIUrl(
            urlType = APIUrlType.BrandConfig(integrationKey)
        ).url
        APIClient().request(
            urlString = apiUrl, method = HTTPMethod.GET, body = null
        ) { result ->
            when (result) {
                is Result.Success -> {
                    CommonUtils().decodeJSON(result.data.toString(),
                        BrandConfigResponse::class.java,
                        onSuccess = { response ->
                            brandConfiguration = response

                        },
                        onError = { error ->
                            Log.i("", "")
                        })
                }

                is Result.Failure -> {
                    Log.i("", "")
                }
            }
        }
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
        CoroutineScope(Dispatchers.Main).launch {
            if (placementsConfiguration.popUpStyling == null) {
                placementsConfiguration.popUpStyling =
                    BreadPartnerDefaults.shared.createPopUpStyling(viewContext)
            }
            fetchPlacementData(
                merchantConfiguration,
                placementsConfiguration,
                viewContext,
                splitTextAndAction,
                false,
                callback
            )
        }
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
        CoroutineScope(Dispatchers.Main).launch {
            if (placementsConfiguration.popUpStyling == null) {
                placementsConfiguration.popUpStyling =
                    BreadPartnerDefaults.shared.createPopUpStyling(viewContext)
            }
            preScreenLookupCall(
                merchantConfiguration, placementsConfiguration, viewContext, callback
            )
        }
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
        CoroutineScope(Dispatchers.Main).launch {
            if (placementsConfiguration.popUpStyling == null) {
                placementsConfiguration.popUpStyling =
                    BreadPartnerDefaults.shared.createPopUpStyling(viewContext)
            }
            fetchPlacementData(
                merchantConfiguration,
                placementsConfiguration,
                viewContext,
                splitTextAndAction = false,
                openPlacementExperience = true,
                callback = callback
            )
        }
    }
}
