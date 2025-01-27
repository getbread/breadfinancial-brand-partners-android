package com.breadfinancial.breadpartners.sdk.networking

// Enum to define different types of API URLs
sealed class APIUrlType {
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

    private val baseURL = "https://brands.kmsmep.com"
    private val rtpsBaseURL = "https://acquire1uat.comenity.net/api"

    // Generates the correct URL based on the URL type
    val url: String
        get() = when (urlType) {
            is APIUrlType.BrandStyle -> "$baseURL/brands/${urlType.brandId}/style"
            is APIUrlType.BrandConfig -> "$baseURL/brands/${urlType.brandId}/config"
            APIUrlType.GeneratePlacements -> "$baseURL/generatePlacements"
            APIUrlType.ViewPlacement -> "$baseURL/ep/v1/view-placement"
            APIUrlType.ClickPlacement -> "$baseURL/ep/v1/click-placement"
            APIUrlType.PreScreen -> "$rtpsBaseURL/prescreen"
            APIUrlType.VirtualLookup -> "$rtpsBaseURL/virtual_lookup"
        }
}
