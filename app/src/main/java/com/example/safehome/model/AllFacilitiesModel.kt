package com.example.safehome.model

data class AllFacilitiesModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val facilitys: List<Facility>,
        val totalRecords: Int
    ) {
        data class Facility(
            val cgstpercentageForOutSidersChargeByDay: Double,
            val cgstpercentageForOutSidersChargeByHour: Double,
            val cgstpercentageForResidentsChargeByDay: Double,
            val cgstpercentageForResidentsChargeByHour: Double,
            val chargeable: String,
            val comments: String,
            val createdBy: Int,
            val createdByName: String,
            val createdOn: String,
            val facilityId: Int,
            val facilityImages: List<FacilityImage>,
            val modifiedBy: Int,
            val modifiedOn: String,
            val name: String,
            val outSidersChargeByDay: Double,
            val outSidersChargeByHour: Double,
            val residentsChargeByDay: Double,
            val residentsChargeByHour: Double,
            val sgstpercentageForOutSidersChargeByDay: Double,
            val sgstpercentageForOutSidersChargeByHour: Double,
            val sgstpercentageForResidentsChargeByDay: Double,
            val sgstpercentageForResidentsChargeByHour: Double,
            val statusId: Int,
            val statusName: String
        ) {
            data class FacilityImage(
                val createdBy: Int,
                val createdOn: String,
                val facility: Any,
                val facilityId: Int,
                val facilityImageId: Int,
                val imagePath: String,
                val isDeleted: Boolean,
                val status: Any,
                val updatedBy: Any,
                val updatedOn: Any
            )
        }
    }
}