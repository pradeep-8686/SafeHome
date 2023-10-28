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
    @SerializedName("documentPath")
    val documentPath: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("noticeTypeId")
    val noticeTypeId: Int,
    @SerializedName("postedBy")
    val postedBy: String,
    @SerializedName("noticeTypeName")
    val noticeTypeName: String,
    @SerializedName("noticeFor")
    val noticeFor: String,
    @SerializedName("createdBy")
    val createdBy: Int,
    @SerializedName("noticeForId")
    val noticeForId: Int,
    @SerializedName("createdOn")
    val createdOn: String,
    @SerializedName("postedDate")
    val postedDate: String,
    @SerializedName("postedTime")
    val postedTime: String,
    @SerializedName("postedTo")
    val postedTo: String,
    @SerializedName("readStatusId")
    val readStatusId: Int,
    @SerializedName("noticeId")
    val noticeId: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("roleId")
    val roleId: Int,
    @SerializedName("readStatus")
    val readStatus: Boolean
) : Serializable
