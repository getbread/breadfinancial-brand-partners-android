package com.breadfinancial.breadpartners.sdk.core.models

data class BreadPartnersRtpsConfig(
    var financingType: FinancingType? = null,
    var order: Order? = null,
    var locationType: LocationType? = null,
    var cardType: String? = null,
    var country: String? = null,
    var prescreenId: Int? = null,
    var correlationData: String? = null,
    var customerAcceptedOffer: Boolean? = null,
    var channel: String? = null,
    var subChannel: String? = null,
    var mockResponse: BreadPartnersMockOptions? = null
)

enum class BreadPartnersMockOptions(val value: String) {
    SUCCESS("success"),
    NO_HIT("noHit"),
    MAKE_OFFER("makeOffer"),
    ACKNOWLEDGE("ackknowledge"),
    EXISTING_ACCOUNT("existingAccount"),
    EXISTING_OFFER("existingOffer"),
    NEW_OFFER("newOffer"),
    ERROR("error")
}
