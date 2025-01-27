package com.breadfinancial.breadpartners.sdk.core.models

import kotlinx.serialization.Serializable

@Serializable
data class BreadPartnersSDKSetup(
    val integrationKey: String,
    val buyer: Buyer,
    val enableLog: Boolean = false
)

@Serializable
data class Buyer(
    val givenName: String? = null,
    val familyName: String? = null,
    val additionalName: String? = null,
    val birthDate: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val billingAddress: Address? = null,
    val shippingAddress: Address? = null
)

@Serializable
data class Address(
    val address1: String? = null,
    val address2: String? = null,
    val country: String? = null,
    val locality: String? = null,
    val region: String? = null,
    val postalCode: String? = null
)
