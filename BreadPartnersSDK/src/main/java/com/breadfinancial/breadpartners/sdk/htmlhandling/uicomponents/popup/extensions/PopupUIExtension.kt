package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.extensions

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import com.breadfinancial.breadpartners.sdk.R
import com.breadfinancial.breadpartners.sdk.core.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.BreadFinancialWebViewInterstitial
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupDialog
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.PopupElements
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.applyTextStyle
import com.bumptech.glide.Glide

fun PopupDialog.setupUI() {

    val configModel = BreadPartnersSDK.getInstance().placementsConfiguration
    val popupStyle = configModel!!.popUpStyling

    closeButton = popupView.findViewById(R.id.close_button)
    brandLogo = popupView.findViewById(R.id.brand_logo)
    dividerTop = popupView.findViewById(R.id.divider_top)
    dividerBottom = popupView.findViewById(R.id.divider_bottom)
    titleLabel = popupView.findViewById(R.id.title_label)
    subtitleLabel = popupView.findViewById(R.id.subtitle_label)
    headerView = popupView.findViewById(R.id.header_view)
    headerLabel = popupView.findViewById(R.id.header_label)
    disclosureLabel = popupView.findViewById(R.id.disclosure_label)
    bottomBanner = popupView.findViewById(R.id.bottom_banner)
    actionButton = popupView.findViewById(R.id.action_button)
    contentContainer = popupView.findViewById(R.id.content_container)
    contentStackView = popupView.findViewById(R.id.content_stack_view)
    overlayProductView = popupView.findViewById(R.id.overlay_product_view)
    overlayEmbeddedView = popupView.findViewById(R.id.overlay_embedded_view)
    loader = popupView.findViewById(R.id.loader_indicator)

    closeButton.setOnClickListener {
        closeButtonTapped()
    }
    closeButton.setColorFilter(popupStyle!!.crossColor)

    Glide.with(this).load(popupModel.brandLogoUrl).into(brandLogo)

    titleLabel.text = popupModel.overlayTitle
    titleLabel.applyTextStyle(popupStyle.titlePopupTextStyle)
    subtitleLabel.text = popupModel.overlaySubtitle
    subtitleLabel.applyTextStyle(popupStyle.subTitlePopupTextStyle)
    disclosureLabel.text = popupModel.disclosure
    disclosureLabel.applyTextStyle(popupStyle.disclosurePopupTextStyle)
    headerLabel.text = popupModel.overlayContainerBarHeading
    headerLabel.applyTextStyle(popupStyle.headerPopupTextStyle)

    actionButton.text = popupModel.primaryActionButtonAttributes?.buttonText ?: "Action"
    actionButton.setBackgroundColor(popupStyle.actionButtonColor)

    actionButton.setOnClickListener {
        callback(BreadPartnerEvent.PopupClosed)

        onActionButtonTapped() }

    context?.let {
        webViewManager = BreadFinancialWebViewInterstitial(it)
        addSectionsToLinearLayout(
            popupModel, contentStackView, it, popupStyle
        )

        PopupElements.shared.decorateLinearLayout(
            linearLayout = contentContainer, borderColor = popupStyle.borderColor
        )
        headerView.setBackgroundColor(Color.GRAY)
        PopupElements.shared.decorateLinearLayout(
            linearLayout = headerView,
            bottomLeftRadius = 0f,
            bottomRightRadius = 0f,
            backgroundColor = popupStyle.headerBgColor,
            borderColor = popupStyle.headerBgColor,
        )
    }

    when (overlayType) {
        PlacementOverlayType.EMBEDDED_OVERLAY -> displayEmbeddedOverlay(popupModel)
        PlacementOverlayType.SINGLE_PRODUCT_OVERLAY -> displayProductOverlay()
    }

    if (overlayType == PlacementOverlayType.SINGLE_PRODUCT_OVERLAY) {
        fetchWebViewPlacement()
    }
}

fun PopupDialog.addSectionsToLinearLayout(
    popupModel: PopupPlacementModel,
    container: LinearLayout,
    context: Context,
    popupStyle: PopUpStyling
) {
    val bodyDivModel = popupModel.dynamicBodyModel.bodyDiv
    val tagPriorityList = listOf("h3", "p", "connector")
    val tagConnector = "connector"

    bodyDivModel.entries.forEachIndexed { index, (_, value) ->
        // Handle connectors on odd indexes
        if (index % 2 != 0) {
            findTagValue(popupModel, tagConnector)?.let { content ->
                PopupElements.shared.createLabelForTag(
                    popupStyle, tagConnector, content, context
                )?.let {
                    container.addView(it)
                }
            }
        }

        // Handle other tags (h3, p)
        val tagValuePairs = value.tagValuePairs
        tagPriorityList.filterNot { it == tagConnector }.forEach { tag ->
            tagValuePairs[tag]?.let { content ->
                PopupElements.shared.createLabelForTag(popupStyle, tag, content, context)?.let {
                    container.addView(it)
                }
            }
        }
    }
}

@Suppress("SameParameterValue")
fun PopupDialog.findTagValue(popupModel: PopupPlacementModel, searchTag: String): String? {
    for (bodyDiv in popupModel.dynamicBodyModel.bodyDiv) {
        for ((key, tagValuePair) in bodyDiv.value.tagValuePairs) {
            if (key == searchTag) {
                return tagValuePair
            }
        }
    }
    return null
}

fun PopupDialog.displayProductOverlay() {
    loader.visibility = View.GONE
    overlayEmbeddedView.visibility = View.GONE
    bottomBanner.visibility = View.VISIBLE
    overlayProductView.visibility = View.VISIBLE
}