package com.breadfinancial.breadpartners.sdk.networking.requestbuilders

import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersRtpsConfig
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.networking.models.RTPSRequest

class RTPSRequestBuilder(
    private val merchantConfiguration: MerchantConfiguration,
    private val rtpsConfig: BreadPartnersRtpsConfig
) {

    fun build(): RTPSRequest {
        val buyer = merchantConfiguration.buyer

        return RTPSRequest(
            urlPath = "screenname",
            firstName = buyer?.givenName,
            lastName = buyer?.familyName,
            address1 = buyer?.billingAddress?.address1,
            city = buyer?.billingAddress?.region,
            state = buyer?.billingAddress?.locality,
            zip = buyer?.billingAddress?.postalCode,
            storeNumber = merchantConfiguration.storeNumber,
            location = rtpsConfig.locationType?.name,
            channel = merchantConfiguration.channel,
            subchannel = merchantConfiguration.subchannel,
            reCaptchaToken = null,
            mockResponse = rtpsConfig.mockResponse?.name,
            overrideConfig = RTPSRequest.OverrideConfig(enhancedPresentment = true)
        )
    }
}
