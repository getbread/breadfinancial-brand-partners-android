//------------------------------------------------------------------------------
//  File:          HTMLContentRenderer.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.htmlhandling

import android.content.Context
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.htmlhandling.extensions.renderTextAndButton
import com.breadfinancial.breadpartners.sdk.htmlhandling.extensions.renderTextViewWithLink
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.TextPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.networking.models.BrandConfigResponse
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementsResponse
import com.breadfinancial.breadpartners.sdk.utilities.AlertHandler
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import com.breadfinancial.breadpartners.sdk.utilities.Logger

/**
 * Responsible for parsing HTML content and rendering corresponding native UI elements.
 */
class HTMLContentRenderer(
    val integrationKey: String,
    val htmlContentParser: HTMLContentParser,
    val analyticsManager: AnalyticsManager,
    val alertHandler: AlertHandler,
    val commonUtils: CommonUtils,
    val logger: Logger,
    val apiClient: APIClient,
    var merchantConfiguration: MerchantConfiguration?,
    var placementsConfiguration: PlacementsConfiguration?,
    var brandConfiguration: BrandConfigResponse?,
    var splitTextAndAction: Boolean = false,
    val callback: (BreadPartnerEvent) -> Unit?
) {

    var textPlacementModel: TextPlacementModel? = null
    var responseModel: PlacementsResponse? = null
    var thisContext: Context? = null

    /**
     * Handles rendering of text-based placement using the provided response model.
     */
    fun handleTextPlacement(
        responseModel: PlacementsResponse,
        thisContext: Context
    ) {
        this.responseModel = responseModel
        this.thisContext = thisContext
        val indexOfPlacement = 0
        textPlacementModel = htmlContentParser.extractTextPlacementModel(
            htmlContent = responseModel.placementContent?.get(indexOfPlacement)?.contentData?.htmlContent
                ?: ""
        )

        logger.logTextPlacementModelDetails(textPlacementModel!!)
        analyticsManager.sendViewPlacement(responseModel)
        if (splitTextAndAction) {
            renderTextAndButton()
        } else {
            renderTextViewWithLink()
        }

    }

    /**
     * Renders a popup overlay based on the given overlay placement model and response data.
     * Sets up and displays the popup UI with dynamic content and configuration.
     */
    fun handlePopupPlacement(
        textPlacementModel: TextPlacementModel, responseModel: PlacementsResponse
    ) {
        val popupPlacementHTMLContent = responseModel.placementContent?.find {
            it.id == textPlacementModel.actionContentId
        }

        val popupPlacementModel = htmlContentParser.extractPopupPlacementModel(
            popupPlacementHTMLContent?.contentData?.htmlContent ?: ""
        ) ?: return showAlert(
            Constants.nativeSDKAlertTitle(), Constants.popupPlacementParsingError
        )

        logger.logPopupPlacementModelDetails(popupPlacementModel)

        val overlayType = htmlContentParser.handleOverlayType(popupPlacementModel.overlayType)
            ?: return showAlert(
                Constants.nativeSDKAlertTitle(), Constants.missingPopupPlacementError
            )
        analyticsManager.sendClickPlacement(responseModel)

        createPopupOverlay(popupPlacementModel, overlayType)
    }

    /**
     * Initializes and displays a popup overlay based on the provided model and type.
     */
    fun createPopupOverlay(
        popupPlacementModel: PopupPlacementModel, overlayType: PlacementOverlayType
    ) {
        val popupDialog = PopupDialog(
            integrationKey,
            popupPlacementModel,
            overlayType,
            alertHandler = alertHandler,
            logger = logger,
            commonUtils = commonUtils,
            apiClient = apiClient,
            htmlContentParser = htmlContentParser,
            callback = callback,
            merchantConfiguration = merchantConfiguration,
            placementsConfiguration = placementsConfiguration,
            brandConfiguration = brandConfiguration
        )
        configurePopupPresentation(popupDialog)
    }

    /**
     * Triggers the display of the popup dialog by invoking the render callback.
     */
    private fun configurePopupPresentation(popupDialog: PopupDialog) {
        callback(BreadPartnerEvent.RenderPopupView(dialogFragment = popupDialog))
    }

    /**
     * Displays a simple alert dialog with a title, message, and a default OK button.
     */
    fun showAlert(title: String, message: String) {
        alertHandler.showAlert(title = title, message = message, showOkButton = true)
    }
}