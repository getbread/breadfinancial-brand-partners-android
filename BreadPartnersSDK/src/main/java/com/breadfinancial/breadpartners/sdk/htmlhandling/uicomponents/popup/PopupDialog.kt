package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup

import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.breadfinancial.breadpartners.sdk.R
import com.breadfinancial.breadpartners.sdk.core.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.htmlhandling.HTMLContentParser
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.BreadFinancialWebViewInterstitial
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PlacementOverlayType
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models.PopupPlacementModel
import com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.popup.extensions.setupUI
import com.breadfinancial.breadpartners.sdk.networking.APIClient
import com.breadfinancial.breadpartners.sdk.utilities.AlertHandler
import com.breadfinancial.breadpartners.sdk.utilities.CommonUtils
import com.breadfinancial.breadpartners.sdk.utilities.LoaderIndicator
import com.breadfinancial.breadpartners.sdk.utilities.Logger
import com.bumptech.glide.Glide

class PopupDialog(
    internal val popupModel: PopupPlacementModel,
    internal val overlayType: PlacementOverlayType,
    internal val apiClient: APIClient,
    internal val alertHandler: AlertHandler,
    internal val commonUtils: CommonUtils,
    internal val htmlContentParser: HTMLContentParser,
    internal val logger: Logger,
    internal val callback: (BreadPartnerEvent) -> Unit?
) : DialogFragment() {

    internal lateinit var popupView: View
    internal lateinit var closeButton: ImageView
    internal lateinit var brandLogo: ImageView
    internal lateinit var dividerTop: View
    internal lateinit var dividerBottom: View
    internal lateinit var titleLabel: TextView
    internal lateinit var subtitleLabel: TextView
    internal lateinit var headerView: LinearLayout
    internal lateinit var headerLabel: TextView
    internal lateinit var disclosureLabel: TextView
    internal lateinit var contentContainer: LinearLayout
    internal lateinit var contentStackView: LinearLayout
    internal lateinit var bottomBanner: LinearLayout
    internal lateinit var actionButton: Button
    internal lateinit var overlayProductView: LinearLayout
    internal lateinit var overlayEmbeddedView: FrameLayout
    internal lateinit var webViewManager: BreadFinancialWebViewInterstitial
    internal lateinit var loader: LoaderIndicator
    internal lateinit var webViewPlacementModel: PopupPlacementModel
    internal fun isWebViewPlacementModelInitialized() = ::webViewPlacementModel.isInitialized

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        popupView = inflater.inflate(R.layout.popup_dialog, container, false)

        setupUI()

        return popupView
    }

    override fun onStart() {
        super.onStart()

        val cornerRadius = 32f
        val radii = FloatArray(8) { cornerRadius }
        val shape = RoundRectShape(radii, null, null)

        val shapeDrawable = ShapeDrawable(shape)
        shapeDrawable.paint.color = Color.WHITE
        shapeDrawable.paint.strokeWidth = 1f
        shapeDrawable.paint.color = Color.GRAY

        dialog?.window?.setBackgroundDrawable(shapeDrawable)

        dialog?.window?.setLayout(
            (requireContext().resources.displayMetrics.widthPixels * 0.9).toInt(),
            (requireContext().resources.displayMetrics.heightPixels * 0.9).toInt()
        )

        if (popupModel.location == "RTPS-Approval") {
            closeButton.visibility = View.GONE
        }
    }

    internal fun displayEmbeddedOverlay(popupPlacementModel: PopupPlacementModel) {
        bottomBanner.visibility = View.GONE
        overlayProductView.visibility = View.GONE
        loader.visibility = View.VISIBLE
        overlayEmbeddedView.visibility = View.VISIBLE
        val placeholder = popupView.findViewById<FrameLayout>(R.id.overlay_embedded_view)
        webViewManager.replaceViewWithWebView(
            placeholder, popupPlacementModel.webViewUrl
        ) {
            loader.visibility = View.GONE
        }
        Glide.with(this).load(popupPlacementModel.brandLogoUrl).into(brandLogo)
    }

}