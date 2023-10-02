package com.example.safehome.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GetAllNoticeStatus(
    val message: String,
    val statusCode: Int,
    val `data`: NoticeData
)

data class NoticeData(
    val `noticedata`: List<Notice>? = ArrayList<Notice>()
)

data class Notice(
    @SerializedName("noticeType")
    val noticeType: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("noticeTypeId")
    val noticeTypeId: Int,
    @SerializedName("postedBy")
    val postedBy: String,
    @SerializedName("createdBy")
    val createdBy: Int,
    @SerializedName("createdOn")
    val createdOn: String,
    @SerializedName("readStatusId")
    val readStatusId: Int,
    @SerializedName("noticeId")
    val noticeId: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("readStatus")
    val readStatus: Boolean
) : Serializable
