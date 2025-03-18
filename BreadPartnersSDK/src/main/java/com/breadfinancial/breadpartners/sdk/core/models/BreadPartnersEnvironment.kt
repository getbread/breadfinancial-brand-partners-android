package com.breadfinancial.breadpartners.sdk.core.models

/**
 * Represents the different environments the SDK can operate in.
 *
 * @property STAGE Use this environment for testing and development.
 * @property PROD **Default** Use this environment for production.
 */
enum class BreadPartnersEnvironment(val value: String) {
    STAGE("STAGE"),
    PROD("PROD")
}