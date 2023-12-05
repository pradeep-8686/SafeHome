package com.example.safehome.enews

import java.io.Serializable

data class UserENewsListModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
):Serializable {
    data class Data(
        val eNews: List<ENew>,
        val totalRecords: Int
    ): Serializable {
        data class ENew(
            val comments: String,
            val createdBy: Int,
            val createdByName: String,
            val createdByRole: String,
            val createdOn: String,
            val eNews: String,
            val eNewsId: Int,
            val eNewsImages: List<ENewsImage>,
            val fromDate: String,
            val keepEnewsFor: String,
            val keepEnewsForId: Int,
            val modifiedBy: Int,
            val modifiedOn: String,
            val postedDate: String,
            val postedTime: String,
            val postedTo: List<PostedTo>,
            val timeStamp: String,
            val toDate: String,
            val topIcName: String
        ): Serializable {
            data class ENewsImage(
                val attachementPath: String,
                val eNewsId: Int,
                val eNewsImageId: Int
            ): Serializable

            data class PostedTo(
                val eNewsId: Int,
                val name: String,
                val postedToId: Int
            ):Serializable
        }
    }
}