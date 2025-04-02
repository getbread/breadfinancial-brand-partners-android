//------------------------------------------------------------------------------
//  File:          PlacementsConfiguration.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.core.models

import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface

/**
 * Data class that provides configurations for the `registerPlacement` or `submitRTPS` methods.
 *
 * @property placementData Defines text placements on the brand partner screen for the `registerPlacementFlow`.
 * @property rtpsData Specifies the real-time pre-screen configuration for the prescreen flow.
 */
data class PlacementsConfiguration(
    val placementData: PlacementData? = null,
    val rtpsData: RTPSData? = null
)

/**
 * Structure used to provide styling configurations for the `PopupController`.
 *
 * - Colors are defined using `UIColor` or `CGColor` for various popup elements like header background, border.
 * - Text styles are configured using `PopupTextStyle` structure for titles, headers, and other text elements.
 * - Button style can be optionally specified using `PopupActionButtonStyle`.
 */
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

/**
 * Structure that defines text styling config for popup elements.
 *
 * - `font`: Specifies the font family and font size for the text.
 * - `textColor`: Specifies the color of the text.
 * - `textSize`: Specifies the size of the text.
 */
data class PopupTextStyle(
    val font: Typeface? = null, val textColor: Int? = null, val textSize: Float? = null
)

/**
 * Structure that defines style configurations for action buttons in popups.
 *
 * - `font`: Specifies the font for the button title.
 * - `textColor`: Specifies the color of the button title text.
 * - `frame`: Specifies the frame dimensions for the button.
 * - `backgroundColor`: Specifies the background color of the button.
 * - `cornerRadius`: Specifies the corner radius for rounded button edges.
 * - `padding`: Specifies the padding within the button and title.
 */
data class PopupActionButtonStyle(
    val font: Int = Typeface.NORMAL,
    val textColor: Int = Color.WHITE,
    val textSize: Float = 12F,
    val backgroundColor: Int = Color.BLACK,
    val cornerRadius: Float = 60.0F
)

data class ViewFrame(
    val width: Int, val height: Int
)
