package com.breadfinancial.breadpartners.sdk.utilities

import android.graphics.Color
import android.graphics.Typeface
import com.breadfinancial.breadpartners.sdk.core.models.Address
import com.breadfinancial.breadpartners.sdk.core.models.Buyer
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle
import com.breadfinancial.breadpartners.sdk.core.models.StyleStruct
import com.breadfinancial.breadpartners.sdk.core.models.TextPlacementStyling
import com.breadfinancial.breadpartners.sdk.core.models.TextViewFrame
import com.breadfinancial.breadpartners.sdk.networking.models.ContextRequestBody
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequestBody

class BreadPartnerDefaults {

    val buyer = Buyer(
        givenName = "John",
        familyName = "Smith",
        additionalName = "C.",
        birthDate = "1974-08-21",
        email = "jsmith@breadfinance.com",
        phone = "2123344141",
        billingAddress = Address(
            address1 = "323 something lane",
            address2 = "apt. B",
            country = "US",
            locality = "NYC",
            region = "NY",
            postalCode = "11222"
        ),
        shippingAddress = Address(
            address1 = "323 something lane",
            address2 = "apt. B",
            country = "US",
            locality = "NYC",
            region = "NY",
            postalCode = "11222"
        )
    )


    val textPlacementRequestType1 = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                id = "03d69ff1-f90c-41b2-8a27-836af7f1eb98", context = ContextRequestBody(
                    SDK_TID = "69d7bfdd-a06c-4e16-adfb-58e03a3c7dbe",
                    ENV = "STAGE",
                    PRICE = 73900,
                    channel = "P",
                    subchannel = "X",
                    ALLOW_CHECKOUT = false
                )
            )
        ), brandId = "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
    )

    val textPlacementRequestType2 = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                id = "8828d6d9-e993-41cc-8744-fa3857c12c4a", context = ContextRequestBody(
                    SDK_TID = "6f42d67e-cff4-4575-802a-e90a838981bb",
                    ENV = "STAGE",
                    LOCATION = "Category",
                    PRICE = 119900,
                    channel = "A",
                    subchannel = "X",
                    ALLOW_CHECKOUT = false
                )
            )
        ), brandId = "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
    )

    val textPlacementRequestType3 = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                id = "03d69ff1-f90c-41b2-8a27-836af7f1eb98", context = ContextRequestBody(
                    SDK_TID = "6f42d67e-cff4-4575-802a-e90a838981ss",
                    ENV = "STAGE",
                    LOCATION = "Product",
                    PRICE = 119900,
                    channel = "A",
                    subchannel = "X",
                    ALLOW_CHECKOUT = false
                )
            )
        ), brandId = "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
    )

    val textPlacementRequestType4 = PlacementRequest(
        placements = listOf(
            PlacementRequestBody(
                id = "",
                context = ContextRequestBody(
                    ENV = "STAGE",
                    LOCATION = "RTPS-Approval",
                    embeddedUrl = "https://acquire1uat.comenity.net/prescreen/offer?mockMO=success&mockPA=success&mockVL=success&embedded=true&clientKey=8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7&prescreenId=79233069&cardType=&urlPath=%2F&firstName=Carol&lastName=Jones&address1=3075%20Loyalty%20Cir&city=Columbus&state=OH&zip=43219&storeNumber=2009&location=checkout&channel=O"
                )
            )
        ),
        brandId = "8a9fcd35-7f4d-4e3c-a9cc-6f6e98064df7"
    )

    val dashboardWebViewURL =
        "https://acquire1uat.comenity.net/unified/checkout-start?clientName=aspire&embedded=true&workflow=unifiedPrequalCheckout&mockPrequalApp=success&mockUD=success&mockBraintreeTokenize=success&mockVCI=successWithIssuedCard&mockBraintreeTokens=success&mockBuyer=success&mockUPC=successWithPrepareCheckout&mockVCIS=success&mockUO=success&epId=6f42d67e-cff4-4575-802a-e90a838981bb&location=Category&channel=C&subchannel=X"

    val styleSet1 = StyleStruct(
        parsedRedColor = Color.parseColor("#d50132"),
        parsedGreyColor = Color.parseColor("#ececec"),
        loaderColor = Color.parseColor("#0f2233"),
        crossColor = Color.BLACK,
        dividerColor = Color.parseColor("#ececec"),
        borderColor = Color.parseColor("#ececec"),
        headerBgColor = Color.parseColor("#ececec"),
        actionButtonColor = Color.parseColor("#d50132"),

        baseFontFamily = "Arial-BoldMT",
        textSizeBold = 16.0f,
        textSizeSemiBold = 14.0f,
        textSizeRegular = 12.0f,
        textSizeSmall = 10.0f,

        normalTextColor = Color.BLACK,
        clickableTextColor = Color.parseColor("#d50132"),
        titleTextColor = Color.BLACK,
        subTitleTextColor = Color.GRAY,
        headerTextColor = Color.GRAY,
        paragraphTextColor = Color.GRAY,
        connectorTextColor = Color.BLACK,
        disclosureTextColor = Color.GRAY,

        popupHeaderFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupTitleFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupSubTitleFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupParagraphFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupConnectorFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupDisclosureFont = Typeface.create("Arial-BoldMT", Typeface.BOLD)
    )

    val styleSet2 = StyleStruct(
        parsedRedColor = Color.parseColor("#FF935F"),
        parsedGreyColor = Color.parseColor("#ececec"),
        loaderColor = Color.parseColor("#FF935F"),
        crossColor = Color.parseColor("#FF935F"),
        dividerColor = Color.parseColor("#FFBE9F"),
        borderColor = Color.parseColor("#ececec"),
        headerBgColor = Color.parseColor("#FFBE9F"),
        actionButtonColor = Color.parseColor("#FF935F"),

        baseFontFamily = "Arial-BoldMT",
        textSizeBold = 17.0f,
        textSizeSemiBold = 15.0f,
        textSizeRegular = 13.0f,
        textSizeSmall = 11.0f,

        normalTextColor = Color.BLACK,
        clickableTextColor = Color.parseColor("#FF935F"),
        titleTextColor = Color.BLACK,
        subTitleTextColor = Color.GRAY,
        headerTextColor = Color.GRAY,
        paragraphTextColor = Color.GRAY,
        connectorTextColor = Color.BLACK,
        disclosureTextColor = Color.GRAY,

        popupHeaderFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupTitleFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupSubTitleFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupParagraphFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupConnectorFont = Typeface.create("Arial-BoldMT", Typeface.BOLD),
        popupDisclosureFont = Typeface.create("Arial-BoldMT", Typeface.BOLD)
    )

    val textPlacementStyling = TextPlacementStyling(
        normalFont = Typeface.BOLD,
        normalTextColor = Color.parseColor("#0000FF"),
        clickableFont = Typeface.BOLD,
        clickableTextColor = Color.parseColor("#0000FF"),
        textViewFrame = TextViewFrame(
            width = 300, height = 200
        )
    )
    val popUpStyling = PopUpStyling(
        loaderColor = Color.parseColor("#0f2233"),
        crossColor = Color.BLACK,
        dividerColor = Color.parseColor("#ececec"),
        borderColor = Color.parseColor("#ececec"),
        titlePopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.BLACK,
            textSize = 16.0f
        ),
        subTitlePopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.GRAY,
            textSize = 12.0f
        ),
        headerPopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.GRAY,
            textSize = 14.0f
        ),
        headerBgColor = Color.parseColor("#ececec"),
        headingThreePopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.parseColor("#d50132"),
            textSize = 14.0f
        ),
        paragraphPopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.GRAY,
            textSize = 10.0f
        ),
        connectorPopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.BLACK,
            textSize = 14.0f
        ),
        disclosurePopupTextStyle = PopupTextStyle(
            font = Typeface.create("Arial-BoldMT", Typeface.BOLD),
            textColor = Color.GRAY,
            textSize = 10.0f
        ),
        actionButtonColor = Color.parseColor("#d50132")
    )
}
