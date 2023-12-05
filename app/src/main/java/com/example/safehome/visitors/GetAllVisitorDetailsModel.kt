package com.example.safehome.visitors

import java.io.Serializable

data class GetAllVisitorDetailsModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
):Serializable {
    data class Data(
        val events: List<Event>,
        val totalRecords: Int
    ): Serializable {
        data class Event(
            val allowFor: String ?= null,
            val approvalStatusId: Int ?= null,
            val approvalStatusName: String ?= null,
            val block: String ?= null,
            val blockId: Int ?= null,
            val comments: String ?= null,
            val createdBy: Int ?= null,
            val createdOn: String ?= null,
            val endDate: String ?= null,
            val flatId: Int ?= null,
            val flatNo: String ?= null,
            val imagePath: Any ?= null,
            val inTime: String ?= null,
            val invitedBy: String ?= null,
            val isDeleted: Boolean ?= null,
            val mobileNo: String ?= null,
            val modifiedBy: Any ?= null,
            val modifiedOn: Any ?= null,
            val name: String ?= null,
            val outTime: String ?= null,
            val repeatId: Int ?= null,
            val residentId: Int ?= null,
            val scheduleType: String ?= null,
            val seviceProviderName: String ?= null,
            val staffId: Int ?= null,
            val startDate: String ?= null,
            val vehicleNumber: String ?= null,
            val visitorId: Int ?= null,
            val visitorServiceProviderId: Int ?= null,
            val visitorStatusId: Int ?= null,
            val visitorStatusName: String ?= null,
            val visitorTypeId: Int ?= null,
            val visitorTypeName: String ?= null
        ): Serializable
    }
}