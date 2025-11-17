//------------------------------------------------------------------------------
//  File:          PlacementRequestBuilder.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.networking.requestbuilders

import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.PlacementData
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody
import com.breadfinancial.breadpartners.sdk.utilities.BreadPartnersExtensions.takeIfNotEmpty

/**
 * Builder class to create a PlacementRequest for fetching placements.
 */
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
            ENV = merchantConfiguration?.env?.value.takeIfNotEmpty(),
            PRICE = placementData?.order?.totalPrice?.value?.toLong(),
            CARDHOLDER_TIER = merchantConfiguration?.cardholderTier.takeIfNotEmpty(),
            STORE_NUMBER = merchantConfiguration?.storeNumber.takeIfNotEmpty(),
            LOYALTY_ID = merchantConfiguration?.loyaltyID.takeIfNotEmpty(),
            OVERRIDE_KEY = merchantConfiguration?.overrideKey.takeIfNotEmpty(),
            CLIENT_VAR_1 = merchantConfiguration?.clientVariable1.takeIfNotEmpty(),
            CLIENT_VAR_2 = merchantConfiguration?.clientVariable2.takeIfNotEmpty(),
            CLIENT_VAR_3 = merchantConfiguration?.clientVariable3.takeIfNotEmpty(),
            CLIENT_VAR_4 = merchantConfiguration?.clientVariable4.takeIfNotEmpty(),
            DEPARTMENT_ID = merchantConfiguration?.departmentId.takeIfNotEmpty(),
            channel = merchantConfiguration?.channel.takeIfNotEmpty(),
            subchannel = merchantConfiguration?.subchannel.takeIfNotEmpty(),
            CMP = merchantConfiguration?.campaignID.takeIfNotEmpty(),
            ALLOW_CHECKOUT = placementData?.allowCheckout ?: false,
            LOCATION = placementData?.locationType?.value.takeIfNotEmpty(),
        )

        val placement = PlacementRequestBody(
            id = placementData?.placementId.takeIfNotEmpty(), context = context
        )

        placements.add(placement)
    }

    fun build(): PlacementRequest {
        return PlacementRequest(
            placements = placements, brandId = brandId
        )
    }
}
