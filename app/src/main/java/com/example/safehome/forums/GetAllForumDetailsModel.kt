package com.example.safehome.forums

import java.io.Serializable

data class GetAllForumDetailsModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) :Serializable{
    data class Data(
        val forum: ArrayList<Forum>,
        val totalRecords: Int
    ) :Serializable{
        data class Forum(
            val attachment: String,
            val block: String,
            val commentCount: Int,
            val comments: ArrayList<Comment>,
            val controlName: String,
            val createdBy: Int,
            val createdByName: String,
            val createdOn: String,
            val description: String,
            val flat: Any,
            val flatNo: String,
            val forumControlID: Int,
            val forumID: Int,
            val forumResultID: Int,
            val keepQuestionFor: String,
            val postedBy: String,
            val postedDate: String,
            val postedTime: String,
            val postedTo: List<PostedTo>,
            val topic: String,
            val userId: Int,
            val userResponceID: Int,
            val userResponceName: String,
            val viewCount: Int
        ):Serializable {
            data class Comment(
                val comment: String,
                val commentCreatedBy: Any,
                val commentCreatedByName: Any,
                val commentId: Int,
                val commentReplies: List<Any>,
                val forumId: Int
            ):Serializable

            data class PostedTo(
                val forumId: Int,
                val name: String,
                val postedToId: Int
            ):Serializable
        }
    }
}
