package com.breadfinancial.breadpartners.sdk.core.models

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface

data class PlacementsConfiguration(
    val placementConfig: BreadPartnersPlacementConfig? = null,
    val rtpsConfig: BreadPartnersRtpsConfig? = null,
    var textPlacementStyling: TextPlacementStyling? = null,
    var popUpStyling: PopUpStyling? = null,
)

data class TextPlacementStyling(
    val normalFont: Int = Typeface.NORMAL,
    val normalTextColor: Int = Color.BLACK,
    val clickableFont: Int = Typeface.NORMAL,
    val clickableTextColor: Int = Color.BLUE,
    val textViewFrame: ViewFrame = ViewFrame(width = 100, height = 100),

    val buttonFont: Int = Typeface.NORMAL,
    val buttonTextColor: Int = Color.WHITE,
    val buttonFrame: ViewFrame = ViewFrame(width = 100, height = 100),
    val buttonPadding: Rect = Rect(0, 0, 100, 100),
    val buttonBackgroundColor: Int = Color.BLACK,
    val buttonCornerRadius: Float = 8.0F,
    val buttonTextSize: Float = 12F,
)

data class ViewFrame(
    val width: Int, val height: Int
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
