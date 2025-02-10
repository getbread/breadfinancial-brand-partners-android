package com.breadfinancial.breadpartners.sdk.networking

import com.breadfinancial.breadpartners.sdk.core.models.BreadSDKEnvironment

// Enum to define different types of API URLs
sealed class APIUrlType {
    data class RTPSWebURL(val type: String) : APIUrlType()
    data class BrandStyle(val brandId: String) : APIUrlType()
    data class BrandConfig(val brandId: String) : APIUrlType()
    data object GeneratePlacements : APIUrlType()
    data object ViewPlacement : APIUrlType()
    data object ClickPlacement : APIUrlType()
    data object PreScreen : APIUrlType()
    data object VirtualLookup : APIUrlType()
}

// A centralized class for constructing and managing API URLs
class APIUrl(private val urlType: APIUrlType) {

    companion object {
        private var currentBreadSDKEnvironment: BreadSDKEnvironment = BreadSDKEnvironment.PROD

        /// Set the environment globally
        fun setEnvironment(breadSDKEnvironment: BreadSDKEnvironment) {
            currentBreadSDKEnvironment = breadSDKEnvironment
        }
    }

    private val baseURL: String
    private val rtpsBaseURL: String

    init {
        when (currentBreadSDKEnvironment) {
            BreadSDKEnvironment.STAGE -> {
                baseURL = "https://brands.kmsmep.com"
                rtpsBaseURL = "https://acquire1uat.comenity.net"
            }

            BreadSDKEnvironment.PROD -> {
                baseURL = "https://brands.kmsmep.com"
                rtpsBaseURL = "https://acquire1uat.comenity.net"
            }
        }
    }

    // Generates the correct URL based on the URL type
    val url: String
        get() = when (urlType) {
            is APIUrlType.RTPSWebURL -> "$rtpsBaseURL/prescreen/${urlType.type}"
            is APIUrlType.BrandStyle -> "$baseURL/brands/${urlType.brandId}/style"
            is APIUrlType.BrandConfig -> "$baseURL/brands/${urlType.brandId}/config"
            APIUrlType.GeneratePlacements -> "$baseURL/generatePlacements"
            APIUrlType.ViewPlacement -> "$baseURL/ep/v1/view-placement"
            APIUrlType.ClickPlacement -> "$baseURL/ep/v1/click-placement"
            APIUrlType.PreScreen -> "$rtpsBaseURL/api/prescreen"
            APIUrlType.VirtualLookup -> "$rtpsBaseURL/api/virtual_lookup"
        }
}
