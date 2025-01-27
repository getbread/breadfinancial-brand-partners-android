package com.breadfinancial.breadpartners.sdk.core.models

import android.graphics.Color
import android.graphics.Typeface
import com.breadfinancial.breadpartners.sdk.networking.models.PlacementRequest

data class PlacementsConfiguration(
    val configModel: PlacementRequest,
    var textPlacementStyling: TextPlacementStyling?,
    var popUpStyling: PopUpStyling?,
)

data class TextPlacementStyling(
    val normalFont: Int = Typeface.NORMAL,
    val normalTextColor: Int = Color.BLACK,
    val clickableFont:Int = Typeface.NORMAL,
    val clickableTextColor: Int = Color.BLUE,
    val textViewFrame: TextViewFrame
)

data class TextViewFrame(
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
    val actionButtonColor: Int = Color.GRAY
)

data class PopupTextStyle(
    val font: Typeface? = null, val textColor: Int? = null, val textSize: Float? = null
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
