package com.example.safehome.model

data class GetAllAssociationMembersModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val associationMembers: List<AssociationMember>,
        val totalRecords: Int
    ) {
        data class AssociationMember(
            val block: String,
            val blockId: Int,
            val comments: String,
            val createdBy: Int,
            val createdByName: String,
            val createdOn: String,
            val email: String,
            val endDate: String,
            val firstName: String,
            val flatId: Int,
            val flatNo: String,
            val lastName: String,
            val memberId: Int,
            val mobileNo: String,
            val modifiedBy: Int,
            val modifiedOn: String,
            val residentId: Int,
            val roleId: Int,
            val roleName: String,
            val startDate: String,
            val statusId: Int,
            val statusName: String
        )
    }
}