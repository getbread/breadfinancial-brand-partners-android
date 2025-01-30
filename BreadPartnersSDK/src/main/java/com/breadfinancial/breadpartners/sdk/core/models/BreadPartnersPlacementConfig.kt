package com.breadfinancial.breadpartners.sdk.core.models

data class BreadPartnersPlacementConfig(
    var financingType: FinancingType? = null,
    var locationType: LocationType? = null,
    var placementId: String? = null,
    var domID: String? = null,
    var allowCheckout: Boolean? = null,
    var order: Order? = null,
    var defaultSelectedCardKey: String? = null,
    var selectedCardKey: String? = null
)

enum class LocationType {
    BAG, BANNER, CART, CATEGORY, CHECKOUT, DASHBOARD, FOOTER, HOMEPAGE,
    LANDING, LOYALTY, MOBILE, PRODUCT, HEADER, SEARCH
}

enum class FinancingType {
    CARD, INSTALLMENTS, VERSATILE
}

data class Order(
    var subTotal: CurrencyValue? = null,
    var totalDiscounts: CurrencyValue? = null,
    var totalPrice: CurrencyValue? = null,
    var totalShipping: CurrencyValue? = null,
    var totalTax: CurrencyValue? = null,
    var discountCode: String? = null,
    var pickupInformation: PickupInformation? = null,
    var fulfillmentType: String? = null,
    var items: List<Item>? = null
)

data class CurrencyValue(
    var currency: String? = null,
    var value: Double? = null
)

data class PickupInformation(
    var name: Name? = null,
    var phone: String? = null,
    var address: BreadPartnersAddress? = null,
    var email: String? = null
)

data class Name(
    var givenName: String? = null,
    var familyName: String? = null,
    var additionalName: String? = null
)

data class Item(
    var name: String? = null,
    var category: String? = null,
    var quantity: Int? = null,
    var unitPrice: CurrencyValue? = null,
    var unitTax: CurrencyValue? = null,
    var sku: String? = null,
    var itemUrl: String? = null,
    var imageUrl: String? = null,
    var description: String? = null,
    var shippingCost: CurrencyValue? = null,
    var shippingProvider: String? = null,
    var shippingDescription: String? = null,
    var shippingTrackingNumber: String? = null,
    var shippingTrackingUrl: String? = null,
    var fulfillmentType: String? = null
)
