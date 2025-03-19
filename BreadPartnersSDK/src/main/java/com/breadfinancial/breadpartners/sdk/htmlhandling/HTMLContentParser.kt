package com.breadfinancial.breadpartners.sdk.htmlhandling

import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementActionType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PrimaryActionButtonModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.TextPlacementModel
import com.breadfinancial.breadpartners.sdk.utilities.AlertHandler
import com.breadfinancial.breadpartners.sdk.utilities.Constants
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

interface HTMLParser {
    fun parse(htmlContent: String): Document
}

class JsoupHTMLParser : HTMLParser {
    override fun parse(htmlContent: String): Document {
        return Jsoup.parse(htmlContent)
    }
}

class HTMLContentParser(
    private val alertHandler: AlertHandler,
    private val htmlParser: HTMLParser
) {

    fun extractTextPlacementModel(htmlContent: String): TextPlacementModel {
        val document = htmlParser.parse(htmlContent)

        val actionType = document.select("[data-action-type]").attr("data-action-type")
            .takeIf { it.isNotEmpty() }
        val actionTarget =
            document.select("[data-action-target]").attr("data-action-target")
                .takeIf { it.isNotEmpty() }
        val actionContentId =
            document.select("[data-action-content-id]").attr("data-action-content-id")
                .takeIf { it.isNotEmpty() }
        val contentText = document.select(".epjs-body").first()?.ownText().orEmpty().trim()
        val actionLink =
            document.select(".epjs-body-action a").text().takeIf { it.isNotEmpty() }
        val paymentDetailsSup = document.select("sup").text()
        val paymentDetails =
            document.select(".ep-text-placement").text().replace(actionLink.orEmpty(), "")
                .replace(paymentDetailsSup, "").trim()

        val finalContentText = when {
            contentText == paymentDetails -> contentText
            contentText.isEmpty() -> paymentDetails
            paymentDetails.isEmpty() -> contentText
            else -> "$contentText $paymentDetails"
        }.takeIf { it.isNotEmpty() }

        return TextPlacementModel(
            actionType = actionType,
            actionTarget = actionTarget,
            contentText = finalContentText,
            actionLink = actionLink,
            actionContentId = actionContentId
        )
    }

    fun extractPopupPlacementModel(htmlContent: String): PopupPlacementModel? {
        return try {
            val document = htmlParser.parse(htmlContent)

            val overlayType =
                document.select("[data-overlay-metadata]").first()?.attr("data-overlay-type")
                    .orEmpty()
            val brandLogoUrl = document.select(".brand.logo img").first()?.attr("src").orEmpty()
            val webViewUrl = document.select("iframe").first()?.attr("src").orEmpty()
            val overlayTitle =
                document.select(".epjs-css-overlay-title").first()?.text().orEmpty()
            val overlaySubtitle =
                document.select(".epjs-css-overlay-subtitle").first()?.text().orEmpty()
            val overlayContainerBarHeading =
                document.select(".epjs-css-overlay-body-title-bar").first()?.ownText().orEmpty()
            val bodyHeader =
                document.select(".epjs-css-overlay-header").first()?.text().orEmpty()
            val disclosure =
                document.select(".epjs-css-overlay-disclosures").first()?.text().orEmpty()
            val primaryActionButtonAttributes =
                extractPrimaryCTAButtonAttributes(document, ".action-button")

            val dynamicBodyModel = processDynamicBodyModel(document)

            PopupPlacementModel(
                overlayType = overlayType,
                brandLogoUrl = brandLogoUrl,
                location = "",
                webViewUrl = webViewUrl,
                overlayTitle = overlayTitle,
                overlaySubtitle = overlaySubtitle,
                overlayContainerBarHeading = overlayContainerBarHeading,
                bodyHeader = bodyHeader,
                primaryActionButtonAttributes = primaryActionButtonAttributes,
                dynamicBodyModel = dynamicBodyModel,
                disclosure = disclosure
            )
        } catch (error: Exception) {
            alertHandler.showAlert(
                title = Constants.nativeSDKAlertTitle(),
                message = Constants.catchError(message = error.message.orEmpty()),
                showOkButton = true
            )
            null
        }
    }

    private fun processDynamicBodyModel(document: Document): PopupPlacementModel.DynamicBodyModel {
        val bodyContainer = document.select(".epjs-css-overlay-body-content").first()
        val mutableBodyDiv = mutableMapOf<String, PopupPlacementModel.DynamicBodyContent>()

        bodyContainer?.let {
            val valueProps = it.select(".epjs-css-overlay-value-prop")
            val connectors = it.select(".epjs-css-overlay-value-prop-connector")
            var sequenceCounter = 1

            valueProps.forEach { valueProp ->
                val bodyContent = PopupPlacementModel.DynamicBodyContent(
                    valueProp.children().associate { child -> child.tagName() to child.text() })
                mutableBodyDiv["div${sequenceCounter++}"] = bodyContent
            }

            connectors.forEach { connector ->
                val connectorBodyContent = PopupPlacementModel.DynamicBodyContent(
                    mapOf("connector" to connector.text())
                )
                mutableBodyDiv["div${sequenceCounter++}"] = connectorBodyContent
            }
        }

        return PopupPlacementModel.DynamicBodyModel(mutableBodyDiv.toMap())
    }

    private fun extractPrimaryCTAButtonAttributes(
        document: Document, selector: String
    ): PrimaryActionButtonModel? {
        val button = document.select(selector).first() ?: return null

        return PrimaryActionButtonModel(
            dataOverlayType = document.select(".epjs-css-modal-footer")
                .first()?.attr("data-overlay-type"),
            dataContentFetch = button.attr("data-content-fetch").takeIf { it.isNotEmpty() },
            dataActionTarget = button.attr("data-action-target").takeIf { it.isNotEmpty() },
            dataActionType = button.attr("data-action-type").takeIf { it.isNotEmpty() },
            dataActionContentId = button.attr("data-action-content-id").takeIf { it.isNotEmpty() },
            dataLocation = button.attr("data-location").takeIf { it.isNotEmpty() },
            buttonText = button.select("span").text().takeIf { it.isNotEmpty() }
        )
    }

    fun handleActionType(response: String): PlacementActionType? {
        return try {
            PlacementActionType.valueOf(response)
        } catch (error: Exception) {
            alertHandler.showAlert(
                title = Constants.nativeSDKAlertTitle(),
                message = Constants.catchError(message = "${error.message}"),
                showOkButton = true
            )
            null
        }
    }

    fun handleOverlayType(response: String): PlacementOverlayType? {
        return try {
            PlacementOverlayType.valueOf(response)
        } catch (error: Exception) {
            alertHandler.showAlert(
                title = Constants.nativeSDKAlertTitle(),
                message = Constants.catchError(message = "${error.message}"),
                showOkButton = true
            )
            null
        }
    }
}
