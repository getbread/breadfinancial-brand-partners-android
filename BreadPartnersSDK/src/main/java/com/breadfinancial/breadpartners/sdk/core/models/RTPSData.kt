package com.breadfinancial.breadpartners.sdk.core.models

data class RTPSData(
    var financingType: String? = null,
    var order: Order? = null,
    var locationType: BreadPartnersLocationType? = null,
    var cardType: String? = null,
    var country: String? = null,
    var prescreenId: Long? = null,
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
