//------------------------------------------------------------------------------
//  File:          BreadPartnerDefaults.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.utilities

import android.graphics.Color
import android.graphics.Typeface
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersFinancingType
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersLocationType
import com.breadfinancial.breadpartners.sdk.core.models.PopUpStyling
import com.breadfinancial.breadpartners.sdk.core.models.PopupActionButtonStyle
import com.breadfinancial.breadpartners.sdk.core.models.PopupTextStyle

/**
 * `BreadPartnerDefaults` class provides default configurations/styles/properties
 * used across the BreadPartner SDK.
 */
class BreadPartnerDefaults private constructor() {

    companion object {
        val shared: BreadPartnerDefaults by lazy { BreadPartnerDefaults() }
    }

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

    // region Default Popup Style
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
        actionButtonStyle = PopupActionButtonStyle(
            font = Typeface.BOLD,
            textColor = Color.WHITE,
            backgroundColor = Color.parseColor("#d50132"),
            cornerRadius = 60.0F,
        )
    )
    //endregion

}


