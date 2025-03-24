package com.breadfinancial.breadpartners.sdk.htmlhandling.extensions

import android.text.Html
import android.text.Spanned
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


fun Elements.getHtmlAsSpanned(): Spanned =
    Html.fromHtml(first()?.html().orEmpty().trimIndent(), Html.FROM_HTML_MODE_LEGACY)

fun Element.getHtmlAsSpanned(): Spanned = Html.fromHtml(this.html().trimIndent(), Html.FROM_HTML_MODE_LEGACY)

