package com.example.safehome.model

data class Tenant(
    val approvalStatusId: Int=1,
    val createdBy: Int=1,
    val createdOn: String="",
    val email: String="",
    val firstName: String="",
    val imagePath: String="",
    val isDeleted: Boolean=false,
    val lastName: String="",
    val mobileNo: String="",
    val modifiedBy: Int=1,
    val modifiedOn: String="",
    val residentId: Int=1,
    val statusId: Int=1,
    val tenantsId: Int=1,
    val userId: Int=1,
    val type: String = "HadData",
val flatNo:String = "",
    val block: String =""
)