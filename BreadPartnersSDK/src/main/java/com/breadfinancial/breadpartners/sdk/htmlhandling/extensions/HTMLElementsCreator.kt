//------------------------------------------------------------------------------
//  File:          HTMLElementsCreator.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartners.sdk.htmlhandling.extensions

import android.text.Html
import android.text.Spanned
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


fun Elements.getHtmlAsSpanned(): Spanned =
    Html.fromHtml(first()?.html().orEmpty().trimIndent(), Html.FROM_HTML_MODE_LEGACY)

fun Element.getHtmlAsSpanned(): Spanned = Html.fromHtml(this.html().trimIndent(), Html.FROM_HTML_MODE_LEGACY)

