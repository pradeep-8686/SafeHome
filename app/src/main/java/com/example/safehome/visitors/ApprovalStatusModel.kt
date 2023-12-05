package com.example.safehome.visitors

import java.io.Serializable

data class ApprovalStatusModel(
    val `data`: List<Data>,
    val message: String,
    val statusCode: Int
) : Serializable{
    data class Data(
        val isActive: Boolean,
        val name: String,
        val statusAlias: String,
        val statusId: Int
    ) : Serializable
}