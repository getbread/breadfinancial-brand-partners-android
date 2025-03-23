package com.breadfinancial.breadpartners.sdk.networking.requestbuilders

import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.RTPSData
import com.breadfinancial.breadpartners.sdk.networking.models.RTPSRequest

class RTPSRequestBuilder(
    private val merchantConfiguration: MerchantConfiguration,
    private val rtpsData: RTPSData,
    private val reCaptchaToken: String
) {

    fun build(): RTPSRequest {
        val buyer = merchantConfiguration.buyer
        if (rtpsData.prescreenId == null) {
            // Fetch PreScreenID using prescreen end point with user info and recaptcha token
            return RTPSRequest(
                urlPath = "screenname",
                firstName = buyer?.givenName,
                lastName = buyer?.familyName,
                address1 = buyer?.billingAddress?.address1,
                city = buyer?.billingAddress?.region,
                state = buyer?.billingAddress?.locality,
                zip = buyer?.billingAddress?.postalCode,
                storeNumber = merchantConfiguration.storeNumber,
                location = rtpsData.locationType?.value,
                channel = merchantConfiguration.channel,
                subchannel = merchantConfiguration.subchannel,
                reCaptchaToken = reCaptchaToken,
                mockResponse = rtpsData.mockResponse?.value,
                overrideConfig = RTPSRequest.OverrideConfig(enhancedPresentment = true)
            )
        } else {
            // Validate PreScreenID using virtuallookup
            return RTPSRequest(
                urlPath = "screenname",
                prescreenId = rtpsData.prescreenId?.toString(),
                channel = merchantConfiguration.channel,
                subchannel = merchantConfiguration.subchannel,
                mockResponse = rtpsData.mockResponse?.value,
                overrideConfig = RTPSRequest.OverrideConfig(enhancedPresentment = true)
            )
        }

    }
}
