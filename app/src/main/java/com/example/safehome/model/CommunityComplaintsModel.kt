package com.example.safehome.model

data class CommunityComplaintsModel(
    val status: String,
    val complaintType: String,
    val category: String,
    val priority: String,
    val assignTo: String,
    val discloseDetails: String,
    val description: String,
    val createdAt: String,
    val attachPhoto: ArrayList<Int>
)