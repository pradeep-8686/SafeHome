package com.example.safehome.policies

import java.io.Serializable

data class PoliciesModel(
    val `data`: Data,
    val message: String,
    val statusCode: Int
) : Serializable{
    data class Data(
        val communityPolicies: List<CommunityPolicy>,
        val totalRecords: Int
    ) : Serializable{
        data class CommunityPolicy(
            val comments: String,
            val createdBy: Int,
            val createdByName: String,
            val description: String,
            val modifiedBy: Int,
            val modifiedOn: String,
            val policeImages: List<Any>,
            val policyId: Int,
            val policyTopic: String,
            val postedBy: String,
            val postedDate: String,
            val postedTime: String,
            val postedTo: List<PostedTo>,
            val roleId: Int,
            val validUntil: String
        ) : Serializable {
            data class PostedTo(
                val name: String,
                val policyId: Int,
                val postedToId: Int
            ) :Serializable
        }
    }
}