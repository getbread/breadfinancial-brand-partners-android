package com.breadfinancial.breadpartners.sdk.networking.requestbuilders

import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementData
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody

class PlacementRequestBuilder(
    integrationKey: String,
    merchantConfiguration: MerchantConfiguration?,
    placementData: PlacementData?
) {
    private var placements: MutableList<PlacementRequestBody> = mutableListOf()
    private var brandId: String = integrationKey

    init {
        createPlacementRequestBody(merchantConfiguration, placementData)
    }

    private fun createPlacementRequestBody(
        merchantConfiguration: MerchantConfiguration?, placementData: PlacementData?
    ) {
        val context = ContextRequestBody(
            ENV = merchantConfiguration?.env?.value,
            PRICE = placementData?.order?.totalPrice?.value?.toLong(),
            CARDHOLDER_TIER = merchantConfiguration?.cardholderTier,
            STORE_NUMBER = merchantConfiguration?.storeNumber,
            LOYALTY_ID = merchantConfiguration?.loyaltyID,
            OVERRIDE_KEY = merchantConfiguration?.overrideKey,
            CLIENT_VAR_1 = merchantConfiguration?.clientVariable1,
            CLIENT_VAR_2 = merchantConfiguration?.clientVariable2,
            CLIENT_VAR_3 = merchantConfiguration?.clientVariable3,
            CLIENT_VAR_4 = merchantConfiguration?.clientVariable4,
            DEPARTMENT_ID = merchantConfiguration?.departmentId,
            channel = merchantConfiguration?.channel,
            subchannel = merchantConfiguration?.subchannel,
            CMP = merchantConfiguration?.campaignID,
            ALLOW_CHECKOUT = placementData?.allowCheckout ?: false
        )

        val placement = PlacementRequestBody(
            id = placementData?.placementId, context = context
        )

        placements.add(placement)
    }

    fun build(): PlacementRequest {
        return PlacementRequest(
            placements = placements, brandId = brandId
        )
    }
}
