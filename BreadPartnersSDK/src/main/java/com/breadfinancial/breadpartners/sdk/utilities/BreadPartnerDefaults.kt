package com.breadfinancial.breadpartners.sdk.utilities

/**
 * `BreadPartnerDefaults` class provides default configurations/styles/properties
 * used across the BreadPartner SDK.
 */
class BreadPartnerDefaults private constructor() {

    companion object {
        val shared: BreadPartnerDefaults by lazy { BreadPartnerDefaults() }
    }

    val placementConfigurations: Map<String, Map<String, Any>> = mapOf(
        "textPlacementRequestType1" to mapOf(
            "placementID" to "03d69ff1-f90c-41b2-8a27-836af7f1eb98",
            "sdkTid" to "69d7bfdd-a06c-4e16-adfb-58e03a3c7dbe",
            "env" to "STAGE",
            "price" to 73900,
            "channel" to "P",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
        ), "textPlacementRequestType2" to mapOf(
            "placementID" to "8828d6d9-e993-41cc-8744-fa3857c12c4a",
            "sdkTid" to "6f42d67e-cff4-4575-802a-e90a838981bb",
            "env" to "STAGE",
            "location" to "Category",
            "price" to 119900,
            "channel" to "A",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
        ), "textPlacementRequestType3" to mapOf(
            "placementID" to "03d69ff1-f90c-41b2-8a27-836af7f1eb98",
            "sdkTid" to "6f42d67e-cff4-4575-802a-e90a838981ss",
            "env" to "STAGE",
            "location" to "Product",
            "price" to 119900,
            "channel" to "A",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
        ), "textPlacementRequestType4" to mapOf(
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7",
            "location" to "RTPS-Approval",
            "embeddedUrl" to "https://acquire1uat.comenity.net/prescreen/offer?mockMO=success&mockPA=success&mockVL=success&embedded=true&clientKey=8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7&prescreenId=79233069&cardType=&urlPath=%2F&firstName=Carol&lastName=Jones&address1=3075%20Loyalty%20Cir&city=Columbus&state=OH&zip=43219&storeNumber=2009&location=checkout&channel=O"
        )
    )

    val styleStruct: Map<String, Map<String, Any>> = mapOf(
        "red" to mapOf(
            "primaryColor" to "#d50132",
            "secondaryColor" to "#69727b",
            "tertiaryColor" to "#ececec",
            "fontFamily" to "josefinsans_bold",
            "small" to 12,
            "medium" to 15,
            "large" to 18,
            "xlarge" to 20
        ), "orange" to mapOf(
            "primaryColor" to "#FF935F",
            "secondaryColor" to "#69727b",
            "tertiaryColor" to "#ececec",
            "fontFamily" to "lato_bold",
            "small" to 12,
            "medium" to 15,
            "large" to 18,
            "xlarge" to 20
        ), "cadet" to mapOf(
            "primaryColor" to "#13294b",
            "secondaryColor" to "#69727b",
            "tertiaryColor" to "#ececec",
            "fontFamily" to "poppins_bold",
            "small" to 12,
            "medium" to 15,
            "large" to 18,
            "xlarge" to 20
        )
    )
}


