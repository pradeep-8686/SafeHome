package com.example.safehome.model

import com.google.gson.annotations.SerializedName

//data class UpcomingEventsModel(
//    val `data`: UpComingEvents,
//    val message: String,
//    val statusCode: Int
//)
//
//data class UpComingEvents(
//    val events: List<Events>,
//    val totalRecords: Int
//)
//
//data class Events(
//    val eventId: Int,
//    val facilityId: Int,
//    val facilityName: String,
//    val residentId: Int,
//    val eventName: String,
//    val startDate: String,
//    val endDate: String,
//    val startTime: String,
//    val endTime: String,
//    val cgstPercentage: Double,
//    val sgstPercentage: Double,
//    val charge: Double,
//    val chargeFor: String,
//    val totalAmount: Double,
//    val cgst: Double,
//    val sgst: Double,
//    val statusId: Int,
//    val statusName: String,
//    val eventTypeId: Int,
//    val eventTypeName: String,
//    val approvalStatusId: Int,
//    val paymentStatusId: Int = 0,
//    val paymentStatusName: String = "",
//    val contactDetails: String = "",
//    val noOfDays: Int,
//    val noOfHours: Int,
//    val approvalStatusName: String = "",
//    val receiptPath: String = "",
//    val generateReceipt: String = "",
//    val vendorRequired: String = "",
//    val comments: String = "",
//    val fullName: String = "",
//    val block: String = "",
//    val flatNo: String = "",
//    val flatId: Int = 0,
//    val blockId: Int = 0,
//    val createdBy: Int = 0,
//    val createdByName: String = "",
//    val createdByRole: String = "",
//    val createdOn: String = "",
//    val modifiedBy: String = "",
//    val modifiedOn: String = "",
//)
data class UpcomingEventsModel (
    @SerializedName("statusCode" ) var statusCode : Int?    = null,
    @SerializedName("message"    ) var message    : String? = null,
    @SerializedName("data"       ) var data       : Data1
)

data class Data1 (
    @SerializedName("events"       ) var events       : List<Events>,
    @SerializedName("totalRecords" ) var totalRecords : Int?              = null
)

data class Events (
    @SerializedName("eventId"            ) var eventId            : Int?    = 0,
    @SerializedName("facilityId"         ) var facilityId         : Int?    = 0,
    @SerializedName("facilityName"       ) var facilityName       : String? = "",
    @SerializedName("residentId"         ) var residentId         : Int?    = 0,
    @SerializedName("eventName"          ) var eventName          : String? = "",
    @SerializedName("startDate"          ) var startDate          : String? = "",
    @SerializedName("endDate"            ) var endDate            : String? = "",
    @SerializedName("startTime"          ) var startTime          : String? = "",
    @SerializedName("endTime"            ) var endTime            : String? = "",
    @SerializedName("cgstPercentage"     ) var cgstPercentage     : Int?    = 0,
    @SerializedName("sgstPercentage"     ) var sgstPercentage     : Int?    = 0,
    @SerializedName("charge"             ) var charge             : Int?    = 0,
    @SerializedName("chargeFor"          ) var chargeFor          : String? = "",
    @SerializedName("totalAmount"        ) var totalAmount        : Int?    = 0,
    @SerializedName("cgst"               ) var cgst               : Int?    = 0,
    @SerializedName("sgst"               ) var sgst               : Int?    = 0,
    @SerializedName("statusId"           ) var statusId           : Int?    = 0,
    @SerializedName("statusName"         ) var statusName         : String? = "",
    @SerializedName("eventTypeId"        ) var eventTypeId        : Int?    = 0,
    @SerializedName("eventTypeName"      ) var eventTypeName      : String? = "",
    @SerializedName("approvalStatusId"   ) var approvalStatusId   : Int?    = 0,
    @SerializedName("paymentStatusId"    ) var paymentStatusId    : String? = "",
    @SerializedName("paymentStatusName"  ) var paymentStatusName  : String? = "",
    @SerializedName("contactDetails"     ) var contactDetails     : String? = "",
    @SerializedName("noOfDays"           ) var noOfDays           : Int?    = 0,
    @SerializedName("noOfHours"          ) var noOfHours          : Int?    = 0,
    @SerializedName("approvalStatusName" ) var approvalStatusName : String? = "",
    @SerializedName("receiptPath"        ) var receiptPath        : String? = "",
    @SerializedName("generateReceipt"    ) var generateReceipt    : String? = "",
    @SerializedName("vendorRequired"     ) var vendorRequired     : String? = "",
    @SerializedName("comments"           ) var comments           : String? = "",
    @SerializedName("fullName"           ) var fullName           : String? = "",
    @SerializedName("block"              ) var block              : String? = "",
    @SerializedName("flatNo"             ) var flatNo             : String? = "",
    @SerializedName("flatId"             ) var flatId             : Int?    = 0,
    @SerializedName("blockId"            ) var blockId            : Int?    = 0,
    @SerializedName("createdBy"          ) var createdBy          : Int?    = 0,
    @SerializedName("createdByName"      ) var createdByName      : String? = "",
    @SerializedName("createdByRole"      ) var createdByRole      : String? = "",
    @SerializedName("createdOn"          ) var createdOn          : String? = "",
    @SerializedName("modifiedBy"         ) var modifiedBy         : String? = "",
    @SerializedName("modifiedOn"         ) var modifiedOn         : String? = ""

)