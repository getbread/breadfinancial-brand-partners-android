//------------------------------------------------------------------------------
//  File:          PlacementData.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.core.models

/**
 * Data model representing placement-related configuration
 */
data class PlacementData(
    var financingType: BreadPartnersFinancingType? = null,
    var locationType: BreadPartnersLocationType? = null,
    var placementId: String? = null,
    var domID: String? = null,
    var allowCheckout: Boolean? = null,
    var order: Order? = null,
    var defaultSelectedCardKey: String? = null,
    var selectedCardKey: String? = null
)

/**
 * Enum representing different placement location types used for configuring
 * where Bread Partners UI elements appear within the host app.
 */
enum class BreadPartnersLocationType(val value: String) {
    BAG("bag"), BANNER("banner"), CART("cart"), CATEGORY("category"), CHECKOUT("checkout"), DASHBOARD(
        "dashboard"
    ),
    FOOTER("footer"), HOMEPAGE("homepage"), LANDING("landing"), LOYALTY("loyalty"), MOBILE("mobile"), PRODUCT(
        "product"
    ),
    HEADER("header"), SEARCH("search")
}

/**
 * Enum representing different financial types used for placement configurations.
 */
enum class BreadPartnersFinancingType(val value: String) {
    CARD("card"), INSTALLMENTS("installments"), VERSATILE("versatile")
}

/**
 * Data model representing order information.
 */
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

/**
 * Data model representing currency value information.
 */
data class CurrencyValue(
    var currency: String? = null, var value: Double? = null
)

/**
 * Data model representing pickup information.
 */
data class PickupInformation(
    var name: Name? = null,
    var phone: String? = null,
    var address: BreadPartnersAddress? = null,
    var email: String? = null
)


/**
 * Data model representing pickup information.
 */
data class Name(
    var givenName: String? = null,
    var familyName: String? = null,
    var additionalName: String? = null
)

/**
 * Data model representing cart item information.
 */
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
