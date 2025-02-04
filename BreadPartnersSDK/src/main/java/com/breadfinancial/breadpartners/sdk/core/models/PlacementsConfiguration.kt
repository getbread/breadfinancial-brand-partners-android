package com.breadfinancial.breadpartners.sdk.core.models

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface

data class PlacementsConfiguration(
    val placementConfig: BreadPartnersPlacementConfig? = null,
    val rtpsConfig: BreadPartnersRtpsConfig? = null,
    var popUpStyling: PopUpStyling? = null,
)

data class PopUpStyling(
    val loaderColor: Int = Color.BLACK,
    val crossColor: Int = Color.BLACK,
    val dividerColor: Int = Color.LTGRAY,
    val borderColor: Int = Color.BLACK,
    val titlePopupTextStyle: PopupTextStyle,
    val subTitlePopupTextStyle: PopupTextStyle,
    val headerPopupTextStyle: PopupTextStyle,
    val headerBgColor: Int = Color.LTGRAY,
    val headingThreePopupTextStyle: PopupTextStyle,
    val paragraphPopupTextStyle: PopupTextStyle,
    val connectorPopupTextStyle: PopupTextStyle,
    val disclosurePopupTextStyle: PopupTextStyle,
    var actionButtonStyle: PopupActionButtonStyle? = null,
)

data class PopupTextStyle(
    val font: Typeface? = null, val textColor: Int? = null, val textSize: Float? = null
)

data class PopupActionButtonStyle(
    val font: Int = Typeface.NORMAL,
    val textColor: Int = Color.WHITE,
    val textSize: Float = 12F,
    val frame: ViewFrame = ViewFrame(width = 100, height = 50),
    val padding: Rect = Rect(0, 0, 0, 0),
    val backgroundColor: Int = Color.BLACK,
    val cornerRadius: Float = 8.0F
)

data class ViewFrame(
    val width: Int, val height: Int
)

data class StyleStruct(
    // Colors
    val parsedRedColor: Int, // Use Int for Color (Android equivalent of UIColor)
    val parsedGreyColor: Int,
    val loaderColor: Int,
    val crossColor: Int,
    val dividerColor: Int,
    val borderColor: Int,
    val headerBgColor: Int,
    val actionButtonColor: Int,

    // Fonts
    val baseFontFamily: String,
    val textSizeBold: Float, // Use Float for text sizes (Android equivalent of CGFloat)
    val textSizeSemiBold: Float,
    val textSizeRegular: Float,
    val textSizeSmall: Float,

    // Text Colors
    val normalTextColor: Int,
    val clickableTextColor: Int,
    val titleTextColor: Int,
    val subTitleTextColor: Int,
    val headerTextColor: Int,
    val paragraphTextColor: Int,
    val connectorTextColor: Int,
    val disclosureTextColor: Int,

    // Popup-specific elements
    val popupHeaderFont: Typeface?, // Use Typeface for fonts (Android equivalent of UIFont)
    val popupTitleFont: Typeface?,
    val popupSubTitleFont: Typeface?,
    val popupParagraphFont: Typeface?,
    val popupConnectorFont: Typeface?,
    val popupDisclosureFont: Typeface?
)
