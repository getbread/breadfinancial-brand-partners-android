package com.breadfinancial.breadpartners.sdk.htmlhandling.uicomponents.models

data class TextPlacementModel(
    val actionType: String?,
    val actionTarget: String?,
    val contentText: String?,
    val actionLink: String?,
    val actionContentId: String?
)

enum class PlacementActionType(val value: String) {
    SHOW_OVERLAY("SHOW_OVERLAY"),
    REDIRECT("REDIRECT"),
    BREAD_APPLY("BREAD_APPLY"),
    REDIRECT_INTERNAL("REDIRECT_INTERNAL"),
    VERSATILE_ECO("VERSATILE_ECO"),
    NO_ACTION("NO_ACTION");

    companion object {
        fun fromValue(value: String): PlacementActionType? {
            return entries.find { it.value == value }
        }
    }
}
