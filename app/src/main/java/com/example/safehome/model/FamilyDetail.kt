package com.example.safehome.model

data class FamilyDetail(
    val `data`: List<FamilyDetails>,
    val message: String,
    val statusCode: Int
)