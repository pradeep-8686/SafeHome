package com.example.safehome.model

import java.io.Serializable

data class GetAllPollDetailsModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
):Serializable {
    data class Data(
        val polls: List<Poll>,
        val totalRecords: Int
    ):Serializable {
        data class Poll(
            val attachPhoto: String,
            val block: String,
            val comments: String,
            val controlName: String,
            val createdBy: Int,
            val createdByName: String,
            val createdOn: String,
            val flatNo: String,
            val fromDate: String,
            val keepFor: String,
            val keepPollForId: Int,
            val pollControlId: Int,
            val pollId: Int,
            val pollOptions: List<PollOption>,
            val pollResultID: Int,
            val postedBy: String,
            val postedDate: String,
            val postedTime: String,
            val postedTo: List<PostedTo>,
            val question: String,
            val resultTobePublic: Boolean,
            val timeStamp: String,
            val toDate: String,
            val totalVotes: Int,
            val userId: Int,
            val userResponceId: Int,
            val userResponceName: String
        ):Serializable {
            data class PollOption(
                val optionId: Int,
                val optionName: String,
                val pollId: Int,
                val voteCount: Int,
                val votePercentage: Int
            ):Serializable

            data class PostedTo(
                val name: String,
                val pollId: Int,
                val postedToId: Int
            ):Serializable
        }
    }
}