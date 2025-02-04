package com.breadfinancial.breadpartners.sdk.networking.requestbuilders

import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersPlacementConfig
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersSetupConfig
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody

class PlacementRequestBuilder(
    integrationKey: String,
    setupConfig: BreadPartnersSetupConfig?,
    placementConfig: BreadPartnersPlacementConfig?
) {
    private var placements: MutableList<PlacementRequestBody> = mutableListOf()
    private var brandId: String = integrationKey

    init {
        createPlacementRequestBody(setupConfig, placementConfig)
    }

    private fun createPlacementRequestBody(
        setupConfig: BreadPartnersSetupConfig?, placementConfig: BreadPartnersPlacementConfig?
    ) {
        val context = ContextRequestBody(
            ENV = setupConfig?.env,
            PRICE = placementConfig?.order?.totalPrice?.value?.toLong(),
            channel = setupConfig?.channel,
            subchannel = setupConfig?.subchannel,
            ALLOW_CHECKOUT = placementConfig?.allowCheckout ?: false
        )

        val placement = PlacementRequestBody(
            id = placementConfig?.placementId, context = context
        )

        placements.add(placement)
    }

    fun build(): PlacementRequest {
        return PlacementRequest(
            placements = placements, brandId = brandId
        )
    }
}
