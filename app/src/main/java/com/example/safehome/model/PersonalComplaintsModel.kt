package com.example.safehome.model

data class PersonalComplaintsModel(
    val status: String,
    val complaintType: String,
    val category: String,
    val priority: String,
    val assignTo: String,
    val discloseDetails: String,
    val description: String,
    val createdAt: String,
    val icon : Int,
    val attachPhoto: ArrayList<Int>
)