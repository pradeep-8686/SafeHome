package com.example.safehome.model

import java.io.Serializable

data class UpcomingMeetingsModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) : Serializable {
    data class Data(
        val meetingData: List<MeetingData>,
        val totalRecords: Int
    ): Serializable {
        data class MeetingData(
            val aganda: String,
            val approvalStatusId: Int,
            val approvalStatusName: String,
            val attendResponseStatus: String,
            val attendResponseStatusId: Int,
            val attendStatusId: Int,
            val createdBy: Int,
            val createdByName: String,
            val createdOn: String,
            val endTime: String,
            val facilityId: Int,
            val facilityName: String,
            var meetingDate: String,
            val meetingId: Int,
            val meetingMinutes: String,
            val meetingStatus: String,
            val modifiedBy: Any,
            val modifiedOn: Any,
            val organisedBy: String,
            val startTime: String,
            val statusId: Int,
            val statusName: String,
            val subMeetingId: Int,
            val topicId: Int,
            val topicName: String,
            val userId: Int
        ) : Serializable
    }
}