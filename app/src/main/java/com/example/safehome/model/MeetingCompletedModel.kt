package com.example.safehome.model

data class MeetingCompletedModel(
    val meetingName: String,
    val location: String,
    val organisedBy: String,
    val meetingDate: String,
    val time: String,
    val isAttended : Boolean
)