package com.example.safehome.visitors

data class InviteVisitorRequestModel(
    val VisitorTypeId: Int? = null,
    val VisitorTypeName: String? = null,
    val VisitorTypeServiceProviderId: Int? = null,
    val StaffId: Int? = null,
    val Name: String? = null,
    val MobileNo: String? = null,
    val AllowFor: String? = null,
    val VehicleNumber: String? = null,
    val RepeatId: Int? = null,
    val Comments: String? = null,
    val StartDate: String? = null, //31-10-2023
    val EndDate: String? = null, //31-10-2023
    val InTime: String? = null, //3:30 PM
    val ApprovalStatusId: Int? = null,
    val ResidentId: String? = null

)
