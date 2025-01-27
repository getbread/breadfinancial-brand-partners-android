package com.breadfinancial.breadpartners.sdk.utilities

object Constants {
    fun nativeSDKAlertTitle(): String {
        return "Bread Partner"
    }

    fun catchError(message: String): String {
        return "$error $message"
    }

    const val securityCheckAlertTitle = "Pre-Fraud Check"
    const val securityCheckFailureAlertTitle =
        "Unable to Verify. Please call us at 1-800-xxx-xxxx for assistance."

    const val okButton = "Ok"

    const val securityCheckAlertAcknolwedgeMessage =
        "Your web view will load once the captcha verification is successfully completed. This ensures that all transactions are secure."

    fun securityCheckAlertFailedMessage(error: String): String {
        return "Error: ${error}"
    }

    const val error = "Error:"

    fun generatePlacementAPIError(message: String): String {
        return "$error $message"
    }

    const val consecutivePlacementRequestDataError = "Consecutive placement request data not found"

    const val apiResToJsonMapError = "Unable to convert response to map."

    const val textPlacementError = "$error Unable to handle text placement type."
    const val missingTextPlacementError = "Unhandled text placement type."
    const val noTextPlacementError = "No text placement type found."
    const val textPlacementParsingError = "Unable to parse text placement."

    const val popupPlacementParsingError = "$error Unable to parse popup placement."
    const val missingPopupPlacementError = "Unhandled popup placement type."

    fun unableToLoadWebURL(message: String): String {
        return "$error Web Url Loading Issue: $message"
    }
}