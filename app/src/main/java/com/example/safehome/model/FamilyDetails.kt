package com.example.safehome.model

data class FamilyDetails(
    val id: Int=1,
    val residentId: Int = 0,
    val firstName: String="",
    val lastName: String="",
    val mobileNo: String="",
    val email: String = "",
    val status: String="",
    val imagePath: String="",
    val createdBy: Int=1,
    val ageGroup: String="",
    val createdOn: String="",
    val modifiedBy: String="",
    val modifiedOn: String="",
    val client: String="",
    val user: String="",
    val type: String = "HadData",
    val clientId: Int=1
)