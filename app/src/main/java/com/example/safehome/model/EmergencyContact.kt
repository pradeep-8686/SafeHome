package com.example.safehome.model

data class EmergencyContact(

    val `data`: Data,
    val message: String,
    val statusCode: Int
) {
    data class Data(
        val emergencyContacts: List<EmergencyContact>,
        val totalRecords: Int
    ) {
        data class EmergencyContact(
            val comments: String,
            val contactPerson: String,
            val contactTypeId: Int,
            val contactTypeName: String,
            val contactTypePerson: String,
            val createdBy: Int,
            val createdByName: String,
            val emergencyContactID: Int,
            val mobileNumber: String,
            val modifiedBy: Int,
            val modifiedOn: String,
            val postDate: String,
            val postedBy: String,
            val postedTo: List<PostedTo>,
            val roleId: Int,
            val secondaryNumber: String
        ) {
            data class PostedTo(
                val emergencyContactId: Int,
                val name: String,
                val postedToId: Int
            )
        }
    }
}
