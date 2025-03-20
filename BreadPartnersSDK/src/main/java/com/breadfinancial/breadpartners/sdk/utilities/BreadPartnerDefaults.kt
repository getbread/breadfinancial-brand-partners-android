package com.breadfinancial.breadpartners.sdk.utilities

import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersFinancingType
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersLocationType

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
            "financingType" to BreadPartnersFinancingType.INSTALLMENTS,
            "env" to BreadPartnersEnvironment.STAGE,
            "price" to 73900,
            "channel" to "P",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
        ),
        "textPlacementRequestType2" to mapOf(
            "placementID" to "8828d6d9-e993-41cc-8744-fa3857c12c4a",
            "sdkTid" to "6f42d67e-cff4-4575-802a-e90a838981bb",
            "financingType" to BreadPartnersFinancingType.INSTALLMENTS,
            "env" to BreadPartnersEnvironment.STAGE,
            "location" to BreadPartnersLocationType.CATEGORY,
            "price" to 119900,
            "channel" to "A",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
        ),
        "textPlacementRequestType3" to mapOf(
            "placementID" to "03d69ff1-f90c-41b2-8a27-836af7f1eb98",
            "sdkTid" to "6f42d67e-cff4-4575-802a-e90a838981ss",
            "financingType" to BreadPartnersFinancingType.INSTALLMENTS,
            "env" to BreadPartnersEnvironment.STAGE,
            "location" to BreadPartnersLocationType.PRODUCT,
            "price" to 119900,
            "channel" to "A",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
        ),
        "textPlacementRequestType4" to mapOf(
            "env" to BreadPartnersEnvironment.STAGE,
            "brandId" to "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7",
            "location" to BreadPartnersLocationType.CHECKOUT,
            "embeddedUrl" to "https://acquire1uat.comenity.net/prescreen/offer?mockMO=success&mockPA=success&mockVL=success&embedded=true&clientKey=8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7&prescreenId=79233069&cardType=&urlPath=%2F&firstName=Carol&lastName=Jones&address1=3075%20Loyalty%20Cir&city=Columbus&state=OH&zip=43219&storeNumber=2009&location=checkout&channel=O"
        ),
        "textPlacementRequestType5" to mapOf(
            "placementID" to "dadc4588-d67f-45f9-8096-81c1264fc2f3",
            "sdkTid" to "6f42d67e-cff4-4575-802a-e90a838981ss",
            "env" to BreadPartnersEnvironment.STAGE,
            "location" to BreadPartnersLocationType.FOOTER,
            "price" to 11000,
            "channel" to "F",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "b9464be2-3ea3-4018-80ed-e903f75acb18"
        ),
        "textPlacementRequestType6" to mapOf(
            "placementID" to "a0348301-dc9a-4c34-b68d-dacb40fe3696",
            "sdkTid" to "6f42d67e-cff4-4575-802a-e90a838981ss",
            "env" to BreadPartnersEnvironment.STAGE,
            "price" to 0,
            "channel" to "X",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "217a0943-8031-457d-b9e3-7375c8af3a22"
        ),
        // TIna provided data
        "textPlacementRequestType7" to mapOf(
            "placementID" to "25a8a961-d226-4ee7-b80e-8f41b2d8ee94",
            "sdkTid" to "",
            "env" to BreadPartnersEnvironment.STAGE,
            "price" to 150000,
            "channel" to "X",
            "subchannel" to "X",
            "allowCheckout" to false,
            "brandId" to "217a0943-8031-457d-b9e3-7375c8af3a22"
        ),
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


