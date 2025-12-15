//------------------------------------------------------------------------------
//  File:          OpenExperienceView.kt
//  Author(s):     Bread Financial
//  Date:          27 March 2025
//
//  Descriptions:  This file is part of the BreadPartnersSDK for Android,
//  providing UI components and functionalities to integrate Bread Financial
//  services into partner applications.
//
//  © 2025 Bread Financial
//------------------------------------------------------------------------------

package com.breadfinancial.breadpartnersexample.sdk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.breadfinancial.breadpartners.sdk.core.BreadPartnersSDK
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnerEvent
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersAddress
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersBuyer
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersEnvironment
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersFinancingType
import com.breadfinancial.breadpartners.sdk.core.models.BreadPartnersLocationType
import com.breadfinancial.breadpartners.sdk.core.models.CurrencyValue
import com.breadfinancial.breadpartners.sdk.core.models.MerchantConfiguration
import com.breadfinancial.breadpartners.sdk.core.models.Name
import com.breadfinancial.breadpartners.sdk.core.models.Order
import com.breadfinancial.breadpartners.sdk.core.models.PickupInformation
import com.breadfinancial.breadpartners.sdk.core.models.PlacementData
import com.breadfinancial.breadpartners.sdk.core.models.PlacementsConfiguration
import com.breadfinancial.breadpartnersexample.sdk.databinding.ViewOpenexperienceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OpenExperienceView : BottomSheetDialogFragment() {

    private lateinit var binding: ViewOpenexperienceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ViewOpenexperienceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height =
                (resources.displayMetrics.heightPixels * 0.9).toInt()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openExperienceFlow()
    }


    fun openExperienceFlow() {

        // MARK:For development purposes
        // These configurations are used to test different types of placement requests.
        // Each placement type corresponds to a specific configuration that includes parameters
        // like placement ID, SDK transaction ID, environment, price, and brand ID.
        // This allows testing of various placement setups by fetching specific configurations
        // based on the placement type key.
        val placementRequestType = TestData.shared.placementConfigurations["textPlacementRequestType200"] ?: emptyMap()
        val placementID = placementRequestType["placementID"] as String?
        val price = placementRequestType["price"] as? Int?
        val loyaltyId = placementRequestType["loyaltyId"] as? String?
        val channel = placementRequestType["channel"] as? String?
        val subChannel = placementRequestType["subchannel"] as? String?
        val env = placementRequestType["env"] as? BreadPartnersEnvironment?
        val location = placementRequestType["location"] as? BreadPartnersLocationType?
        val breadPartnersFinancingType =
            placementRequestType["financingType"] as? BreadPartnersFinancingType?

        /**
         * Configuration for defining placement options in BreadPartners.
         *
         * Modify the Placement ID and total price to test different placements.
         */
        val placementData = PlacementData(
            financingType = breadPartnersFinancingType,
            locationType = location,
            placementId = placementID,
        )

        val placementsConfiguration = PlacementsConfiguration(
            placementData = placementData
        )

        val merchantConfiguration = MerchantConfiguration(
            loyaltyID = loyaltyId,
            storeNumber = "8883",
            env = env,
            channel = channel,
            subchannel = subChannel
        )

        BreadPartnersSDK.getInstance().openExperienceForPlacement(
            merchantConfiguration = merchantConfiguration,
            placementsConfiguration = placementsConfiguration,
            viewContext = requireContext()
        ) { event ->
            when (event) {
                is BreadPartnerEvent.RenderPopupView -> {
                    val view = event.dialogFragment
                    view.show(parentFragmentManager, "PopupDialog")
                }

                is BreadPartnerEvent.OnSDKEventLog -> {}

                else -> {
                    Log.i("BreadPartnerSDK::", "Event:$event")
                }
            }
        }

    }
}