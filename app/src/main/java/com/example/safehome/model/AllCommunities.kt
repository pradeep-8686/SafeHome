package com.example.safehome.model

data class AllCommunities(
    val `data`: List<CommunityDetails>,
    val message: String,
    val statusCode: Int
)