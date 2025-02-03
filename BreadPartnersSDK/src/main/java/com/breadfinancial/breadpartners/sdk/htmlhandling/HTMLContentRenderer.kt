package com.breadfinancial.breadpartners.sdk.htmlhandling

import androidx.appcompat.app.AppCompatActivity
import com.breadfinancial.breadpartners.sdk.analytics.AnalyticsManager
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersSetupConfig
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.TextPlacementStyling
import com.breadfinancial.breadpartners.sdk.htmlhandling.extensions.renderSingleTextView
import com.breadfinancial.breadpartners.sdk.htmlhandling.extensions.renderTextAndButton
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

class HTMLContentRenderer(
    val htmlContentParser: HTMLContentParser,
    val analyticsManager: AnalyticsManager,
    val alertHandler: AlertHandler,
    val commonUtils: CommonUtils,
    val logger: Logger,
    val apiClient: APIClient,
    var setupConfig: BreadPartnersSetupConfig?,
    var placementsConfiguration: PlacementsConfiguration?,
    var brandConfiguration: BrandConfigResponse?,
    var splitTextAndAction: Boolean = false,
    val callback: (BreadPartnerEvent) -> Unit?
) {

    var textPlacementModel: TextPlacementModel? = null
    var responseModel: PlacementsResponse? = null
    var textPlacementStyling: TextPlacementStyling? = null
    var thisContext: AppCompatActivity? = null

    fun handleTextPlacement(
        responseModel: PlacementsResponse,
        thisContext: AppCompatActivity
    ) {
        this.responseModel = responseModel
        this.thisContext = thisContext
        val indexOfPlacement = 0
        textPlacementModel = htmlContentParser.extractTextPlacementModel(
            htmlContent = responseModel.placementContent?.get(indexOfPlacement)?.contentData?.htmlContent
                ?: ""
        )
        textPlacementStyling = placementsConfiguration?.textPlacementStyling

        logger.logTextPlacementModelDetails(textPlacementModel!!)
        analyticsManager.sendViewPlacement(responseModel)
        if (splitTextAndAction) {
            renderTextAndButton()
        } else {
            renderSingleTextView()
        }

    }

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

    fun createPopupOverlay(
        popupPlacementModel: PopupPlacementModel, overlayType: PlacementOverlayType
    ) {
        val popupDialog = PopupDialog(
            popupPlacementModel,
            overlayType,
            alertHandler = alertHandler,
            logger = logger,
            commonUtils = commonUtils,
            apiClient = apiClient,
            htmlContentParser = htmlContentParser,
            callback = callback,
            setupConfig = setupConfig,
            placementsConfiguration = placementsConfiguration,
            brandConfiguration = brandConfiguration
        )
        configurePopupPresentation(popupDialog)
    }

    fun configurePopupPresentation(popupDialog: PopupDialog) {
        callback(BreadPartnerEvent.RenderPopupView(dialogFragment = popupDialog))
    }

    fun showAlert(title: String, message: String) {
        alertHandler.showAlert(title = title, message = message, showOkButton = true)
    }
}