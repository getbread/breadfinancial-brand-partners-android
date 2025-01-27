package com.breadfinancial.breadpartners.sdk.utilities

import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.TextPlacementModel
import org.json.JSONObject
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class Logger(
    private val outputStream: OutputStream,
    private var isLoggingEnabled: Boolean
) {

    var loggingEnabled: Boolean
        get() = isLoggingEnabled
        set(value) {
            isLoggingEnabled = value
        }


    private fun generateDashLine(length: Int): String {
        return "-".repeat(length)
    }

    private fun printLog(message: String) {
        outputStream.write(message.toByteArray())
        outputStream.write("\n".toByteArray())  // Adding a newline after the log
    }

    fun logRequestDetails(
        url: String, method: String, headers: Map<String, String>?, body: ByteArray?
    ) {
        if (!isLoggingEnabled) return

        printLog("\n${generateDashLine(20)} Request Details ${generateDashLine(20)}")
        printLog("URL     : $url")
        printLog("Method  : $method")

        headers?.let {
            printLog("Headers : $it")
        } ?: printLog("Headers : None")

        body?.let {
            try {
                val jsonString = String(it, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(jsonString)
                val prettyJsonString = jsonObject.toString(4)
                printLog("Body    : $prettyJsonString")
            } catch (e: Exception) {
                printLog("Body    : None or Unformatted")
            }
        } ?: printLog("Body    : None or Unformatted")

        printLog("${generateDashLine(60)}\n")
    }

    fun logResponseDetails(
        url: String, statusCode: Int, headers: Map<String, List<String>>, body: ByteArray?
    ) {
        if (!isLoggingEnabled) return

        printLog("\n${generateDashLine(20)} Response Details ${generateDashLine(20)}")
        printLog("URL         : $url")
        printLog("Status Code : $statusCode")
        printLog("Headers     : $headers")

        body?.let {
            try {
                val jsonString = String(it, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(jsonString)
                val prettyJsonString = jsonObject.toString(4)
                printLog("Body        : $prettyJsonString")
            } catch (e: Exception) {
                val bodyString = String(it, StandardCharsets.UTF_8)
                printLog("Body        : $bodyString")
            }
        } ?: printLog("Body        : None or Unformatted")

        printLog("${generateDashLine(60)}\n")
    }

    fun logTextPlacementModelDetails(model: TextPlacementModel) {
        if (!isLoggingEnabled) return
        printLog("\n${generateDashLine(20)} Text Placement Model Details ${generateDashLine(20)}")
        printLog("Action Type       : ${model.actionType ?: "N/A"}")
        printLog("Action Target     : ${model.actionTarget ?: "N/A"}")
        printLog("Content Text      : ${model.contentText ?: "N/A"}")
        printLog("Action Link       : ${model.actionLink ?: "N/A"}")
        printLog("Action Content ID : ${model.actionContentId ?: "N/A"}")
        printLog("${generateDashLine(60)}\n")
    }

    fun logPopupPlacementModelDetails(model: PopupPlacementModel) {
        if (!isLoggingEnabled) return

        printLog("\n${generateDashLine(20)} Popup Placement Model Details ${generateDashLine(20)}")
        printLog("Overlay Type                  : ${model.overlayType}")
        printLog("Brand Logo URL                : ${model.brandLogoUrl}")
        printLog("WebView URL                   : ${model.webViewUrl}")
        printLog("Overlay Title                 : ${model.overlayTitle}")
        printLog("Overlay Subtitle              : ${model.overlaySubtitle}")
        printLog("Overlay Container Bar Heading : ${model.overlayContainerBarHeading}")
        printLog("Body Header                   : ${model.bodyHeader}")
        printLog("Disclosure                    : ${model.disclosure}")

        model.primaryActionButtonAttributes?.let { primaryActionButton ->
            printLog("\n${generateDashLine(20)} Primary Action Button Details ${generateDashLine(20)}")
            printLog("  Data Overlay Type       : ${primaryActionButton.dataOverlayType ?: "N/A"}")
            printLog("  Data Content Fetch      : ${primaryActionButton.dataContentFetch ?: "N/A"}")
            printLog("  Data Action Target      : ${primaryActionButton.dataActionTarget ?: "N/A"}")
            printLog("  Data Action Type        : ${primaryActionButton.dataActionType ?: "N/A"}")
            printLog("  Data Action Content ID  : ${primaryActionButton.dataActionContentId ?: "N/A"}")
            printLog("  Data Location           : ${primaryActionButton.dataLocation ?: "N/A"}")
            printLog("  Button Text             : ${primaryActionButton.buttonText ?: "N/A"}")
        } ?: run {
            printLog("${generateDashLine(20)} Primary Action Button: N/A ${generateDashLine(20)}")
        }

        if (model.dynamicBodyModel.bodyDiv.isNotEmpty()) {
            val logOutput =
                StringBuilder(
                    "\n${generateDashLine(20)} Dynamic Body Model Details ${
                        generateDashLine(
                            20
                        )
                    }\n"
                )
            model.dynamicBodyModel.bodyDiv.forEach { (key, bodyContent) ->
                logOutput.append("  Body Div Key [$key]:\n")
                bodyContent.tagValuePairs.forEach { (tag, value) ->
                    logOutput.append("    - $tag: $value\n")
                }
            }
            logOutput.append("${generateDashLine(60)}\n")
            printLog(logOutput.toString())
        } else {
            printLog("\n${generateDashLine(20)} Dynamic Body Model Details ${generateDashLine(20)}")
            printLog("Dynamic Body Model: N/A")
            printLog("${generateDashLine(60)}\n")
        }
    }
}

